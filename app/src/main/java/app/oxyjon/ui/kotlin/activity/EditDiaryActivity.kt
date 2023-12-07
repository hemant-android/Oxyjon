package app.oxyjon.ui.kotlin.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import app.oxyjon.MainApplication
import app.oxyjon.adapter.SelectMedicineTimeAdapter
import app.oxyjon.bean.GetMyFoodDiaryResponse.Datum.FoodItem
import app.oxyjon.bean.MedicineTimingModel
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityEditDiaryBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.retrofit.response.CommonResponse
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.ui.kotlin.activity.adapter.CalendarAdapter
import app.oxyjon.utils.CalendarData
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class EditDiaryActivity : BaseActivity(), IApiCallback, SelectMedicineTimeAdapter.ClickListener,
    CalendarAdapter.CalendarInterface {
    lateinit var binding: ActivityEditDiaryBinding
    var preferences: AppSharedPreferences? = null

    private var selectDate: String? = ""
    private var selectedTime: String? = ""

    //    private var mFoodItem: FoodDiary? = null
    private val addMedicineTimeArr = ArrayList<String>()
    private val mTimingArr = ArrayList<MedicineTimingModel>()
    var mFoodItem: FoodItem? = null
    var minteger = 0.0

    private var yyyy_MM_dd = ""
    var c = Calendar.getInstance()
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
        binding = ActivityEditDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MainApplication.currentActivity = this
        preferences = AppSharedPreferences.getInstance(this)

        val bundle = intent.extras

        if (bundle != null) {
            mFoodItem = bundle.getSerializable("foodItem") as FoodItem?
            selectDate = mFoodItem!!.mealDate
        }

        decimalFormat.maximumFractionDigits = 1

        addMedicineTimeArr.add("Early Morning")
        addMedicineTimeArr.add("Breakfast")
        addMedicineTimeArr.add("Morning Snack")
        addMedicineTimeArr.add("Lunch")
        addMedicineTimeArr.add("Evening Snack")
        addMedicineTimeArr.add("Dinner")
        addMedicineTimeArr.add("Bed Time")

        if (mTimingArr != null && mTimingArr.size > 0) {
            mTimingArr.clear()
        }
        for (i in addMedicineTimeArr.indices) {
            val model = MedicineTimingModel()
            model.medicineTime = addMedicineTimeArr[i]
            when (mFoodItem!!.mealTime) {
                "early_morning" -> {
                    model.select = addMedicineTimeArr[i] == "Early Morning"
                    selectedTime = "Early Morning"
                }
                "breakfast" -> {
                    model.select = addMedicineTimeArr[i] == "Breakfast"
                    selectedTime = "Breakfast"
                }
                "morning_snack" -> {
                    model.select = addMedicineTimeArr[i] == "Morning Snack"
                    selectedTime = "Morning Snack"
                }
                "lunch" -> {
                    model.select = addMedicineTimeArr[i] == "Lunch"
                    selectedTime = "Lunch"
                }
                "evening_snack" -> {
                    model.select = addMedicineTimeArr[i] == "Evening Snack"
                    selectedTime = "Evening Snack"
                }
                "dinner" -> {
                    model.select = addMedicineTimeArr[i] == "Dinner"
                    selectedTime = "Dinner"
                }
                "bed_time" -> {
                    model.select = addMedicineTimeArr[i] == "Bed Time"
                    selectedTime = "Bed Time"
                }
            }
            mTimingArr.add(model)
        }
        selectMedicineTimeAdapter.setData(mTimingArr)

        preferences!!.isFoodDiaryDialogPopup(true)

        init()
        getDates()

        binding.tvMonthYearPicker!!.setOnClickListener { setupDatePickerDueDate() }

        binding.rvSelectTime.apply {
            layoutManager = FlexboxLayoutManager(this@EditDiaryActivity).apply {
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.FLEX_START
            }
            binding.rvSelectTime.adapter = selectMedicineTimeAdapter
            selectMedicineTimeAdapter.setClickListener(this@EditDiaryActivity)
        }


        if (mFoodItem != null) {
            binding.tvFoodName.text = mFoodItem!!.foodItemName
            binding.edtQuantity.text =Editable.Factory.getInstance().newEditable(mFoodItem!!.mealQuantity)

            binding.tvCalorie.text = mFoodItem!!.calorieGm
            binding.tvTotalProtein.text = mFoodItem!!.proteinGm
            binding.tvTotalFats.text = mFoodItem!!.fatsGm
            binding.tvTotalCarbs.text = mFoodItem!!.carbsGm
            binding.tvTotalFiber.text = mFoodItem!!.fiberGm
        }


        binding.imgBack!!.setOnClickListener {
            finish()
            hideKeyboard(this@EditDiaryActivity)
        }


        binding.edtQuantity.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(str: Editable) {
                val focussed: Boolean = binding.edtQuantity.hasFocus()
                if (focussed) {
                    if (str != null && !TextUtils.isEmpty(str)) {
                        minteger = binding.edtQuantity.text.toString().trim { it <= ' ' }.toDouble()

                        if (mFoodItem?.calorieGm != null) {
                            val totalCalorie: Double = mFoodItem!!.calorieGm!!.toDouble() * minteger
                            binding.tvCalorie.text =
                                "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.mealQuantity!!.toDouble())
                        }
                        if (mFoodItem?.proteinGm != null) {
                            val totalCalorie: Double = mFoodItem!!.proteinGm!!.toDouble() * minteger
                            binding.tvTotalProtein.text =
                                "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.mealQuantity!!.toDouble())
                        }
                        if (mFoodItem?.carbsGm != null) {
                            val totalCalorie: Double = mFoodItem!!.carbsGm!!.toDouble() * minteger
                            binding.tvTotalCarbs.text =
                                "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.mealQuantity!!.toDouble())
                        }
                        if (mFoodItem?.fatsGm != null) {
                            val totalCalorie: Double = mFoodItem!!.fatsGm!!.toDouble() * minteger
                            binding.tvTotalFats.text =
                                "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.mealQuantity!!.toDouble())
                        }
                        if (mFoodItem?.fiberGm != null) {
                            val totalCalorie: Double = mFoodItem!!.fiberGm!!.toDouble() * minteger
                            binding.tvTotalFiber.text =
                                "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.mealQuantity!!.toDouble())
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
                val totalCalorie: Double = mFoodItem!!.calorieGm!!.toDouble() * minteger
                binding.tvCalorie.text =
                    "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.mealQuantity!!.toDouble())
            }
            if (mFoodItem?.proteinGm != null) {
                val totalCalorie: Double = mFoodItem!!.proteinGm!!.toDouble() * minteger
                binding.tvTotalProtein.text =
                    "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.mealQuantity!!.toDouble())
            }
            if (mFoodItem?.carbsGm != null) {
                val totalCalorie: Double = mFoodItem!!.carbsGm!!.toDouble() * minteger
                binding.tvTotalCarbs.text =
                    "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.mealQuantity!!.toDouble())
            }
            if (mFoodItem?.fatsGm != null) {
                val totalCalorie: Double = mFoodItem!!.fatsGm!!.toDouble() * minteger
                binding.tvTotalFats.text =
                    "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.mealQuantity!!.toDouble())
            }
            if (mFoodItem?.fiberGm != null) {
                val totalCalorie: Double = mFoodItem!!.fiberGm!!.toDouble() * minteger
                binding.tvTotalFiber.text =
                    "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.mealQuantity!!.toDouble())
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
                    val totalCalorie: Double = mFoodItem!!.calorieGm!!.toDouble() * minteger
                    binding.tvCalorie.text =
                        "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.mealQuantity!!.toDouble())
                }
                if (mFoodItem?.proteinGm != null) {
                    val totalCalorie: Double = mFoodItem!!.proteinGm!!.toDouble() * minteger
                    binding.tvTotalProtein.text =
                        "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.mealQuantity!!.toDouble())
                }
                if (mFoodItem?.carbsGm != null) {
                    val totalCalorie: Double = mFoodItem!!.carbsGm!!.toDouble() * minteger
                    binding.tvTotalCarbs.text =
                        "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.mealQuantity!!.toDouble())
                }
                if (mFoodItem?.fatsGm != null) {
                    val totalCalorie: Double = mFoodItem!!.fatsGm!!.toDouble() * minteger
                    binding.tvTotalFats.text =
                        "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.mealQuantity!!.toDouble())
                }
                if (mFoodItem?.fiberGm != null) {
                    val totalCalorie: Double = mFoodItem!!.fiberGm!!.toDouble() * minteger
                    binding.tvTotalFiber.text =
                        "" + decimalFormat!!.format(totalCalorie / mFoodItem!!.mealQuantity!!.toDouble())
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

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        if (type == "updateFoodDiary") {
            val response = data as Response<CommonResponse?>
            if (response?.body() != null && response.isSuccessful && response.body()!!.errorCode != null && response.body()!!.errorCode == "1") {
                Toast.makeText(this@EditDiaryActivity,
                    response.body()!!.errorMsg,
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onFailure(data: Any) {}

    private fun saveFoodItem() {
        hideKeyboard(this)
        if (TextUtils.isEmpty(selectedTime) || selectedTime.equals("Select Time",
                ignoreCase = true)
        ) {
            Toast.makeText(this, "Please select food time", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(binding.edtQuantity.text.toString().trim())) {
            Toast.makeText(this, "Please enter quantity.", Toast.LENGTH_SHORT).show()
        } else {
            saveAddFoodItem(mFoodItem!!.id.toString(), preferences!!.getprofileid()!!,
                mFoodItem!!.foodId.toString(),
                mFoodItem!!.foodItemName!!,
                selectedTime!!.lowercase(Locale.getDefault()).replace(" ", "_"),
                selectDate!!, mFoodItem!!.mealQuantityType!!,
                binding.edtQuantity.text.toString().trim { it <= ' ' }
                    .lowercase(Locale.getDefault()).replace(",", ""),
                mFoodItem!!.mealQuantityUnit!!, mFoodItem!!.foodType ?: "veg",
                binding.tvCalorie.text.toString().replace(",", ""),
                binding.tvTotalProtein.text.toString().replace(",", ""),
                binding.tvTotalCarbs.text.toString().replace(",", ""),
                binding.tvTotalFats.text.toString().replace(",", ""),
                binding.tvTotalFiber.text.toString().replace(",", ""))
        }
    }

    override fun onSelectTimeClick(selectedItem: String?, position: Int) {
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
        DatePickerDialog(this,
            dateListenerDate,  // set DatePickerDialog to point to today's date when it loads up
            cal[Calendar.YEAR],
            cal[Calendar.MONTH],
            cal[Calendar.DAY_OF_MONTH]).show()
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
        id: String,
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
            ApiCall.instance.updateFoodDiaryItem(id, profileId,
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
                this)
        } else {
            Toast.makeText(this, "please check your internet connection", Toast.LENGTH_SHORT).show()
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
}