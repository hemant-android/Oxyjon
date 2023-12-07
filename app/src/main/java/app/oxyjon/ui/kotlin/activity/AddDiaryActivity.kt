package app.oxyjon.ui.kotlin.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import app.oxyjon.MainApplication
import app.oxyjon.adapter.AutoCompleteFoodAdapter
import app.oxyjon.adapter.FoodSelectedAdapter
import app.oxyjon.adapter.MostSearchFoodItemAdapter
import app.oxyjon.adapter.SelectMedicineTimeAdapter
import app.oxyjon.bean.GetMostSearchFoodItemResponse
import app.oxyjon.bean.MedicineTimingModel
import app.oxyjon.database.AppDatabase
import app.oxyjon.database.AppExecutors
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.database.FoodDiary
import app.oxyjon.databinding.ActivityAddDiaryBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.retrofit.response.CommonResponse
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.ui.kotlin.activity.adapter.CalendarAdapter
import app.oxyjon.utils.CalendarData
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper.trackEvent
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddDiaryActivity : BaseActivity(), IApiCallback, FoodSelectedAdapter.ClickListener,
    MostSearchFoodItemAdapter.ClickListener, SelectMedicineTimeAdapter.ClickListener,
    CalendarAdapter.CalendarInterface {
    lateinit var binding: ActivityAddDiaryBinding
    var preferences: AppSharedPreferences? = null
    private var mDb: AppDatabase? = null

    private val mSelectedFoodArr: ArrayList<FoodDiary?>? = ArrayList()
    private val mMostSearchFoodArr: ArrayList<GetMostSearchFoodItemResponse.Datum>? = ArrayList()
    private val mMostSearchFoodItemAdapter = MostSearchFoodItemAdapter(this)
    private var navigationType: String? = ""
    private var selectDate: String? = ""
    private var selectedTime: String? = ""

    private var mFoodItem: FoodDiary? = null
    private var allFoodList: List<FoodDiary>? = null
    private val addMedicineArr = ArrayList<String>()
    private val mTimingArr = ArrayList<MedicineTimingModel>()
    private val medicineListArr = ArrayList<String>()
    var minteger = 0.0

    private var yyyy_MM_dd = ""
    var c = Calendar.getInstance()
    var formattedDate: String? = null
    var df: SimpleDateFormat? = null
    var sdf = SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH)
    var sdf_yyyy_MM_dd = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
    var cal = Calendar.getInstance(Locale.ENGLISH)
    var calendarAdapter = CalendarAdapter(this, ArrayList())
    var calendarList = ArrayList<CalendarData>()

    var decimalFormat = DecimalFormat()

    private val selectMedicineTimeAdapter: SelectMedicineTimeAdapter by lazy {
        SelectMedicineTimeAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MainApplication.currentActivity = this
        preferences = AppSharedPreferences.getInstance(this)
        mDb = AppDatabase.getInstance(this)

        val bundle = intent.extras
        if (bundle != null) {
            navigationType = bundle.getString("navigationType")
            selectDate = bundle.getString("selectDate")
            selectedTime = bundle.getString("foodTime")
        }

        decimalFormat.maximumFractionDigits = 1

        addMedicineArr.add("Early Morning")
        addMedicineArr.add("Breakfast")
        addMedicineArr.add("Morning Snack")
        addMedicineArr.add("Lunch")
        addMedicineArr.add("Evening Snack")
        addMedicineArr.add("Dinner")
        addMedicineArr.add("Bed Time")

        if (mTimingArr != null && mTimingArr.size > 0) {
            mTimingArr.clear()
        }
        for (i in addMedicineArr.indices) {
            val model = MedicineTimingModel()
            model.medicineTime = addMedicineArr[i]
            model.select = addMedicineArr[i] == selectedTime
            mTimingArr.add(model)
        }
        selectMedicineTimeAdapter.setData(mTimingArr)

        preferences!!.isFoodDiaryDialogPopup(true)

        df = SimpleDateFormat("dd-MM-yyyy")
        formattedDate = df!!.format(c.time)

        init()
        getDates()
        binding.tvMonthYearPicker!!.setOnClickListener { setupDatePickerDueDate() }


        if (isConnection(this@AddDiaryActivity)) {
            ApiCall.instance.getMostSearchFoodDiaryList(preferences!!.getprofileid(), this)
        } else {
            Toast.makeText(
                this@AddDiaryActivity, "please check your internet connection", Toast.LENGTH_SHORT
            ).show()
        }


        retrieveTasks()

        binding.rvSelectTime.apply {
            layoutManager = FlexboxLayoutManager(this@AddDiaryActivity).apply {
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.FLEX_START
            }
            binding.rvSelectTime.adapter = selectMedicineTimeAdapter
            selectMedicineTimeAdapter.setClickListener(this@AddDiaryActivity)
        }

        binding.rvRecentFoodItem!!.adapter = mMostSearchFoodItemAdapter
        mMostSearchFoodItemAdapter.setClickListener(this)

        if (!TextUtils.isEmpty(navigationType) && navigationType.equals(
                "edit", ignoreCase = true
            )
        ) {
            binding.llSearchView.visibility = View.GONE
            binding.llEditView.visibility = View.GONE

        } else {
            binding.llSearchView.visibility = View.VISIBLE
            binding.llEditView.visibility = View.GONE
            binding.autoCompleteTextView!!.requestFocus()
        }
        binding.imgBack!!.setOnClickListener {
            finish()
            hideKeyboard(this@AddDiaryActivity)
        }


        binding.edtQuantity.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(str: Editable) {
                val focussed: Boolean = binding.edtQuantity.hasFocus()
                if (focussed) {
                    if (str != null && !TextUtils.isEmpty(str)) {
                        var sminteger = binding.edtQuantity.text.toString().trim { it <= ' ' }

                        if (sminteger == ".") {
                            return
                        }
                        minteger = sminteger.toDouble()


                        if (mFoodItem?.calorieGm != null) {
                            val totalCalorie: Double = mFoodItem!!.calorieGm.toDouble() * minteger
                            binding.tvCalorie.text =
                                "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.quantityPrimary.toDouble())
                        }
                        if (mFoodItem?.proteinGm != null) {
                            val totalCalorie: Double = mFoodItem!!.proteinGm.toDouble() * minteger
                            binding.tvTotalProtein.text =
                                "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.quantityPrimary.toDouble())
                        }
                        if (mFoodItem?.carbsGm != null) {
                            val totalCalorie: Double = mFoodItem!!.carbsGm.toDouble() * minteger
                            binding.tvTotalCarbs.text =
                                "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.quantityPrimary.toDouble())
                        }
                        if (mFoodItem?.fatsGm != null) {
                            val totalCalorie: Double = mFoodItem!!.fatsGm.toDouble() * minteger
                            binding.tvTotalFats.text =
                                "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.quantityPrimary.toDouble())
                        }
                        if (mFoodItem?.fiberGm != null) {
                            val totalCalorie: Double = mFoodItem!!.fiberGm.toDouble() * minteger
                            binding.tvTotalFiber.text =
                                "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.quantityPrimary.toDouble())
                        }
                    }
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int,
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int,
            ) {
            }
        })

        binding.imgPlus.setOnClickListener {
            increaseInteger()
        }

        binding.imgMinius.setOnClickListener {
            decreaseInteger()
        }


        binding.tvSave.setOnClickListener {

            saveFoodItem()

        }

        binding.autoCompleteTextView!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(str: Editable?) {
                if (str != null && !TextUtils.isEmpty(str)) {

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }


    override fun onResume() {
        super.onResume()
        formattedDate = df!!.format(c.time)

        MainApplication.currentActivity = this
    }

    override fun onBackPressed() {
        preferences!!.isFoodDiaryDialogPopup(true)
        super.onBackPressed()
    }

    private fun increaseInteger() {
        binding.edtQuantity.clearFocus()

        if (!TextUtils.isEmpty(binding.edtQuantity.text.toString().trim { it <= ' ' })) {
            minteger = binding.edtQuantity.text.toString().trim { it <= ' ' }.toDouble()
            minteger += 1
            binding.edtQuantity.setText("" + decimalFormat!!.format(minteger))

            if (mFoodItem?.calorieGm != null) {
                val totalCalorie: Double = mFoodItem!!.calorieGm.toDouble() * minteger
                binding.tvCalorie.text =
                    "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.quantityPrimary.toDouble())
            }
            if (mFoodItem?.proteinGm != null) {
                val totalCalorie: Double = mFoodItem!!.proteinGm.toDouble() * minteger
                binding.tvTotalProtein.text =
                    "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.quantityPrimary.toDouble())
            }
            if (mFoodItem?.carbsGm != null) {
                val totalCalorie: Double = mFoodItem!!.carbsGm.toDouble() * minteger
                binding.tvTotalCarbs.text =
                    "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.quantityPrimary.toDouble())
            }
            if (mFoodItem?.fatsGm != null) {
                val totalCalorie: Double = mFoodItem!!.fatsGm.toDouble() * minteger
                binding.tvTotalFats.text =
                    "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.quantityPrimary.toDouble())
            }
            if (mFoodItem?.fiberGm != null) {
                val totalCalorie: Double = mFoodItem!!.fiberGm.toDouble() * minteger
                binding.tvTotalFiber.text =
                    "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.quantityPrimary.toDouble())
            }
        }
    }

    private fun decreaseInteger() {
        binding.edtQuantity.clearFocus()

        if (!TextUtils.isEmpty(binding.edtQuantity.text.toString().trim { it <= ' ' })) {
            minteger = binding.edtQuantity.text.toString().trim { it <= ' ' }.toDouble()
            minteger -= 1
            if (minteger > 0) {
                binding.edtQuantity.setText("" + decimalFormat!!.format(minteger))

                if (mFoodItem?.calorieGm != null) {
                    val totalCalorie: Double = mFoodItem!!.calorieGm.toDouble() * minteger
                    binding.tvCalorie.text =
                        "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.quantityPrimary.toDouble())
                }
                if (mFoodItem?.proteinGm != null) {
                    val totalCalorie: Double = mFoodItem!!.proteinGm.toDouble() * minteger
                    binding.tvTotalProtein.text =
                        "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.quantityPrimary.toDouble())
                }
                if (mFoodItem?.carbsGm != null) {
                    val totalCalorie: Double = mFoodItem!!.carbsGm.toDouble() * minteger
                    binding.tvTotalCarbs.text =
                        "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.quantityPrimary.toDouble())
                }
                if (mFoodItem?.fatsGm != null) {
                    val totalCalorie: Double = mFoodItem!!.fatsGm.toDouble() * minteger
                    binding.tvTotalFats.text =
                        "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.quantityPrimary.toDouble())
                }
                if (mFoodItem?.fiberGm != null) {
                    val totalCalorie: Double = mFoodItem!!.fiberGm.toDouble() * minteger
                    binding.tvTotalFiber.text =
                        "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.quantityPrimary.toDouble())
                }
            }
        }

    }

    private fun init() {
        binding.tvMonthYearPicker!!.text = sdf.format(cal.time)
        yyyy_MM_dd = sdf_yyyy_MM_dd.format(cal.time)

        binding.calendarView!!.setHasFixedSize(true)
        binding.calendarView!!.adapter = calendarAdapter
    }

    private fun retrieveTasks() {
        AppExecutors.instance!!.diskIO().execute {
            allFoodList = mDb!!.personDao().allFoodDiary as List<FoodDiary>?
            runOnUiThread {
                runOnUiThread {
                    if (allFoodList != null && allFoodList!!.isNotEmpty()) {
                        Log.e("Food item size:", "" + allFoodList!!.size)
                        val adapter = AutoCompleteFoodAdapter(this@AddDiaryActivity, allFoodList!!)
                        binding.autoCompleteTextView!!.setAdapter(adapter)
                        binding.autoCompleteTextView!!.onItemClickListener = onItemClickListener
                    }
                }
            }
        }
    }

    private val onItemClickListener = OnItemClickListener { adapterView, view, pos, l ->
        hideKeyboard(this@AddDiaryActivity)
        binding.autoCompleteTextView!!.setText("")
        binding.autoCompleteTextView!!.hint = "Search Food"
//        tvSelectFood!!.text = allFoodList!![pos].foodItemName
        mFoodItem = allFoodList!![pos]
        mSelectedFoodArr!!.add(mFoodItem)

        binding.llSearchView.visibility = View.GONE
        binding.llEditView.visibility = View.VISIBLE

        binding.tvFoodName.text = mFoodItem!!.foodItemName
        binding.edtQuantity.text =
            Editable.Factory.getInstance().newEditable(mFoodItem!!.quantityPrimary)

        binding.tvCalorie.text = mFoodItem!!.calorieGm
        binding.tvUnit.text =
            mFoodItem!!.quantityPrimary + mFoodItem!!.quantityUnitPrimary + " = " + mFoodItem!!.quantitySecondary + mFoodItem!!.quantityUnitSecondary
        binding.tvTotalProtein.text = mFoodItem!!.proteinGm
        binding.tvTotalFats.text = mFoodItem!!.fatsGm
        binding.tvTotalCarbs.text = mFoodItem!!.carbsGm
        binding.tvTotalFiber.text = mFoodItem!!.fiberGm


    }

    override fun onSelectFoodItemClick(newPosition: Int) {
        if (mSelectedFoodArr != null && mSelectedFoodArr.size > 0) {
            mSelectedFoodArr.removeAt(newPosition)
//            if (mSelectedFoodArr.size == 0) {
//                llNewlySearch!!.visibility = View.GONE
//            } else {
//                llNewlySearch!!.visibility = View.VISIBLE
//            }
        }
    }

    override fun onSelectMostSearchFoodItemClick(
        foodItem: GetMostSearchFoodItemResponse.Datum?,
        position: Int,
    ) {

        if (foodItem != null) {

            mFoodItem = FoodDiary(
                foodItem.id!!,
                foodItem.foodType ?: "",
                foodItem.foodItemName ?: "",
                foodItem.measurementUnit ?: "",
                foodItem.quantityPrimary ?: "",
                foodItem.quantityUnitPrimary ?: "",
                foodItem.quantitySecondary ?: "",
                foodItem.quantityUnitSecondary ?: "",
                foodItem.calorieGm ?: "",
                foodItem.proteinGm ?: "",
                foodItem.carbsGm ?: "",
                foodItem.fatsGm ?: "",
                foodItem.fiberGm ?: "",
                foodItem.mealEarlyMorning ?: "",
                foodItem.mealBreakfast ?: "",
                foodItem.mealMorningSnack ?: "",
                foodItem.mealLunch ?: "",
                foodItem.mealEveningSnack ?: "",
                foodItem.mealDinner ?: "",
                foodItem.mealBedTime ?: "",
                foodItem.cuisineType ?: ""
            )

            binding.llSearchView.visibility = View.GONE
            binding.llEditView.visibility = View.VISIBLE

            binding.tvFoodName.text = mFoodItem!!.foodItemName
            binding.edtQuantity.text =
                Editable.Factory.getInstance().newEditable(mFoodItem!!.quantityPrimary)
            binding.tvCalorie.text = mFoodItem!!.calorieGm
            binding.tvUnit.text =
                mFoodItem!!.quantityPrimary + mFoodItem!!.quantityUnitPrimary + " = " + mFoodItem!!.quantitySecondary + mFoodItem!!.quantityUnitSecondary
            binding.tvTotalProtein.text = mFoodItem!!.proteinGm
            binding.tvTotalFats.text = mFoodItem!!.fatsGm
            binding.tvTotalCarbs.text = mFoodItem!!.carbsGm
            binding.tvTotalFiber.text = mFoodItem!!.fiberGm
        }
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        if (type == "addFoodDiaryItem") {
            val response = data as Response<CommonResponse?>
            if (response?.body() != null && response.isSuccessful && response.body()!!.errorCode != null && response.body()!!.errorCode == "1") {
                val properties = Properties()
                properties.addAttribute("foodAdd", true)
                properties.addAttribute("foodSave", true)
                trackEvent(this, "AddFoodItem", properties)
                Toast.makeText(
                    this@AddDiaryActivity, response.body()!!.errorMsg, Toast.LENGTH_SHORT
                ).show()

                binding.llEditView.visibility = View.GONE
                binding.llSearchView.visibility = View.VISIBLE
//                selectedTime = ""

                if (mTimingArr != null && mTimingArr.size > 0) {
                    mTimingArr.clear()
                }

                for (i in addMedicineArr.indices) {
                    val model = MedicineTimingModel()
                    model.medicineTime = addMedicineArr[i]
                    model.select = addMedicineArr[i] == selectedTime
                    mTimingArr.add(model)
                }
                /*for (i in addMedicineArr.indices) {
                    val model = MedicineTimingModel()
                    model.medicineTime = addMedicineArr[i]
                    model.select = false
                    mTimingArr.add(model)
                }*/
                selectMedicineTimeAdapter.setData(mTimingArr)

                preferences!!.isFoodDiaryDialogPopup(true)
            }
        }
        if (type == "mostSearchFoodDiaryList") {
            val response = data as Response<GetMostSearchFoodItemResponse>
            if (response.isSuccessful && response.body()!!.errorCode == "1") {
                if (response.body()!!.data != null && response.body()!!.data!!.size > 0) {
                    if (mMostSearchFoodArr != null && mMostSearchFoodArr.size > 0) {
                        mMostSearchFoodArr.clear()
                    }
                    for (mData in response.body()!!.data!!) {
                        mData.select = false
                        mMostSearchFoodArr!!.add(mData)
                    }
                    if (mMostSearchFoodArr != null && mMostSearchFoodArr.size > 0) {
                        mMostSearchFoodItemAdapter.setData(mMostSearchFoodArr)
                    }
                }
            }
        }
    }

    override fun onFailure(data: Any) {}

    private fun saveFoodItem() {
        hideKeyboard(this@AddDiaryActivity)
        if (TextUtils.isEmpty(yyyy_MM_dd)) {
            Toast.makeText(this@AddDiaryActivity, "Please select date", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(selectedTime) || selectedTime.equals(
                "Select Time", ignoreCase = true
            )
        ) {
            Toast.makeText(this@AddDiaryActivity, "Please select food time", Toast.LENGTH_SHORT)
                .show()
        } else {
            saveAddFoodItem(
                preferences!!.getprofileid()!!,
                mFoodItem!!.id.toString(),
                mFoodItem!!.foodItemName,
                selectedTime!!.lowercase(Locale.getDefault()).replace(" ", "_"),
                selectDate!!,
                "Primary",
                binding.edtQuantity.text.toString().trim { it <= ' ' }
                    .lowercase(Locale.getDefault()).replace(",", ""),
                mFoodItem!!.quantityUnitPrimary,
                mFoodItem!!.foodType ?: "veg",
                binding.tvCalorie.text.toString().replace(",", ""),
                binding.tvTotalProtein.text.toString().replace(",", ""),
                binding.tvTotalCarbs.text.toString().replace(",", ""),
                binding.tvTotalFats.text.toString().replace(",", ""),
                binding.tvTotalFiber.text.toString().replace(",", "")
            )
        }
    }

    companion object {
        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun isConnection(ctx: Context?): Boolean {
            val connectivityManager =
                ctx!!.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val ni = connectivityManager.activeNetworkInfo
            return ni != null && ni.isAvailable && ni.isConnected
        }
    }

    override fun onSelectTimeClick(selectedItem: String?, position: Int) {
        /* mTimingArr[position].select = !mTimingArr[position].select
         selectMedicineTimeAdapter.notifyDataSetChanged()
         medicineListArr.add(selectedItem!!)
         hideKeyboard(this)*/

        selectedTime = selectedItem

        for (i in mTimingArr.indices) {
            mTimingArr[i].select = mTimingArr[i].medicineTime == selectedTime
        }
        selectMedicineTimeAdapter.notifyDataSetChanged()
        hideKeyboard(this)
    }

    override fun onSelect(calendarData: CalendarData, position: Int) {

        calendarList.forEachIndexed { index, calendarModel ->
            calendarModel.isSelected = index == position
        }

        binding.tvMonthYearPicker!!.text = sdf.format(calendarData.data)
        calendarAdapter.updateList(calendarList)
        yyyy_MM_dd = sdf_yyyy_MM_dd.format(calendarData.data)
        selectDate = yyyy_MM_dd
    }

    private fun setupDatePickerDueDate() {
        DatePickerDialog(
            this,
            dateListenerDate,  // set DatePickerDialog to point to today's date when it loads up
            cal[Calendar.YEAR],
            cal[Calendar.MONTH],
            cal[Calendar.DAY_OF_MONTH]
        ).show()
    }

    private val dateListenerDate =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal[Calendar.YEAR] = year
            cal[Calendar.MONTH] = monthOfYear
            cal[Calendar.DAY_OF_MONTH] = dayOfMonth
            updateDateFirstRegistration()
        }

    private fun updateDateFirstRegistration() {
        binding.tvMonthYearPicker!!.text = sdf.format(cal.time)
        yyyy_MM_dd = sdf_yyyy_MM_dd.format(cal.time)
        selectDate = yyyy_MM_dd
        getDates()
    }

    private fun getDates() {
        val dateList = ArrayList<CalendarData>() // For our Calendar Data Class
        val dates = ArrayList<Date>() // For Date

        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)

        while (dates.size < maxDaysInMonth) {

            dates.add(monthCalendar.time)
            if (monthCalendar.time == cal.time) {
                dateList.add(CalendarData(monthCalendar.time, true))
            } else {
                dateList.add(CalendarData(monthCalendar.time, false))
            }

            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)   // Increment Day By 1
        }

        calendarList.clear()
        calendarList.addAll(dateList)
        calendarAdapter.updateList(dateList)

        for (i in calendarList.indices) {
            if (calendarList[i].isSelected) {
                binding.calendarView!!.scrollToPosition(i)
            }
        }
    }

    private fun saveAddFoodItem(
        profileId: String,
        food_id: String,
        food_name: String,
        meal_time: String,
        meal_date: String,
        meal_quantity_type: String,
        meal_quantity: String,
        meal_quantity_unit: String,
        food_type: String,
        calorie_gm: String,
        protein_gm: String,
        carbs_gm: String,
        fats_gm: String,
        fiber_gm: String,
    ) {
        if (isConnection(this)) {
            ApiCall.instance.addFoodDiaryItem(
                profileId,
                food_id,
                food_name,
                meal_time,
                meal_date,
                meal_quantity_type,
                meal_quantity,
                meal_quantity_unit,
                food_type,
                calorie_gm,
                protein_gm,
                carbs_gm,
                fats_gm,
                fiber_gm,
                this
            )
        } else {
            Toast.makeText(this, "please check your internet connection", Toast.LENGTH_SHORT).show()
        }
    }
}