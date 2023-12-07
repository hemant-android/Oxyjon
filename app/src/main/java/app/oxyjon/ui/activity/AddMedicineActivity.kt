package app.oxyjon.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.R
import app.oxyjon.adapter.AutoCompletePlaceAdapter
import app.oxyjon.adapter.SelectMedicineTimeAdapter
import app.oxyjon.bean.MedicineTimingModel
import app.oxyjon.bean.SaveMedicineResponse
import app.oxyjon.database.AppDatabase
import app.oxyjon.database.AppExecutors
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.database.Medicine
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper.setUserAttribute
import com.moengage.core.analytics.MoEAnalyticsHelper.trackEvent
import retrofit2.Response
import java.util.*


class AddMedicineActivity : BaseActivity(), IApiCallback, SelectMedicineTimeAdapter.ClickListener {
    private val selectMedicineTimeAdapter = SelectMedicineTimeAdapter(this)
    var preferences: AppSharedPreferences? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.imgBack)
    var imgBack: ImageView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.rvSelectTime)
    var rvSelectTime: RecyclerView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tvSave)
    var tvSave: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tvSelectMedicine)
    var tvSelectMedicine: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.autoCompleteTextView)
    var autoCompleteTextView: AutoCompleteTextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.llOption)
    var llOption: LinearLayout? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.llQuantityTime)
    var llQuantityTime: LinearLayout? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.llViewMedicine)
    var llViewMedicine: LinearLayout? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tvMedicineName)
    var tvMedicineName: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tvMedicineDose)
    var tvMedicineDose: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tvMedicineTimeSlot)
    var tvMedicineTimeSlot: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.spinner)
    var spinner: Spinner? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.edtQuantity)
    var edtQuantity: EditText? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tvViewList)
    var tvViewList: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.imgPlus)
    var imgPlus: ImageView? = null

    @JvmField
    @BindView(R.id.imgMinius)
    var imgMinius: ImageView? = null

    @JvmField
    @BindView(R.id.btnSwitch)
    var btnSwitch: SwitchCompat? = null


    private val addMedicineArr = ArrayList<String>()
    private val mTimingArr: ArrayList<MedicineTimingModel>? = ArrayList()
    private val medicineListArr = ArrayList<String?>()
    private var mDb: AppDatabase? = null
    private var allMedicineList: List<Medicine>? = ArrayList()
    private var selectedMedicineId = ""
    private val isFoodTimeSelect = false
    private var selectedTimeSlot = ""
    private var medicineName: String? = ""
    private var selectedDose = ""
    private var quantity = 0.0
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicine)
        ButterKnife.bind(this)
        preferences = AppSharedPreferences.getInstance(this)
        mDb = AppDatabase.getInstance(this)
        retrieveTasks()
        addMedicineArr.add("Before Breakfast")
        addMedicineArr.add("After Breakfast")
        addMedicineArr.add("Before Lunch")
        addMedicineArr.add("After Lunch")
        addMedicineArr.add("Before Dinner")
        addMedicineArr.add("After Dinner")
        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.justifyContent = JustifyContent.FLEX_START
        rvSelectTime!!.layoutManager = layoutManager
        rvSelectTime!!.adapter = selectMedicineTimeAdapter
        selectMedicineTimeAdapter.setClickListener(this)
        val adapter = ArrayAdapter.createFromResource(this,
            R.array.dose,
            android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinner!!.adapter = adapter
        selectedDose = spinner!!.selectedItem.toString()
        imgBack!!.setOnClickListener {
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
            hideKeyboard(this@AddMedicineActivity)
        }
        tvViewList!!.setOnClickListener {
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        }
        spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long,
            ) {
                selectedDose = parent.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        imgPlus!!.setOnClickListener {
            if (!TextUtils.isEmpty(edtQuantity!!.text.toString().trim { it <= ' ' })) {
                quantity = edtQuantity!!.text.toString().trim { it <= ' ' }.toDouble()
                quantity += 1
                edtQuantity!!.setText("" + quantity)
            }
        }
        imgMinius!!.setOnClickListener {
            if (!TextUtils.isEmpty(edtQuantity!!.text.toString().trim { it <= ' ' })) {
                quantity = edtQuantity!!.text.toString().trim { it <= ' ' }.toDouble()
                quantity -= 1
                if (quantity > 0) {
                    edtQuantity!!.setText("" + quantity)
                }
            }
        }
        tvSave!!.setOnClickListener {
            for (model in mTimingArr!!) {
                if (model.select) {
                    if (model.medicineTime.equals("Before Breakfast", ignoreCase = true)) {
                        selectedTimeSlot += "before_breakfast,"
                    } else if (model.medicineTime.equals("After Breakfast", ignoreCase = true)) {
                        selectedTimeSlot += "after_breakfast,"
                    } else if (model.medicineTime.equals("Before Lunch", ignoreCase = true)) {
                        selectedTimeSlot += "before_lunch,"
                    } else if (model.medicineTime.equals("After Lunch", ignoreCase = true)) {
                        selectedTimeSlot += "after_lunch,"
                    } else if (model.medicineTime.equals("Before Dinner", ignoreCase = true)) {
                        selectedTimeSlot += "before_dinner,"
                    } else if (model.medicineTime.equals("After Dinner", ignoreCase = true)) {
                        selectedTimeSlot += "after_dinner,"
                    }
                }
            }
            if (TextUtils.isEmpty(medicineName)) {
                Toast.makeText(this@AddMedicineActivity,
                    "Please select medicine name",
                    Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(edtQuantity!!.text.toString().trim { it <= ' ' })) {
                Toast.makeText(this@AddMedicineActivity,
                    "Please enter medicine quantity",
                    Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(selectedTimeSlot)) {
                Toast.makeText(this@AddMedicineActivity,
                    "Please select medicine time",
                    Toast.LENGTH_SHORT).show()
            } else if (!TextUtils.isEmpty(selectedTimeSlot)) {
                if (selectedTimeSlot.endsWith(",")) {
                    selectedTimeSlot = selectedTimeSlot.substring(0, selectedTimeSlot.length - 1)
                }
                hideKeyboard(this@AddMedicineActivity)
                edtQuantity!!.clearFocus()
                if (isConnection(this@AddMedicineActivity)) {

                    if (btnSwitch!!.isChecked){

                    }


                    ApiCall.instance.addNewMedicine(preferences!!.getprofileid(),
                        selectedMedicineId,
                        medicineName,
                        selectedTimeSlot,
                        edtQuantity!!.text.toString().trim { it <= ' ' },
                        currentDate,
                        this@AddMedicineActivity)
                    selectedTimeSlot = ""
                } else {
                    Toast.makeText(this@AddMedicineActivity,
                        "please check your internet connection",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, pos, l ->
        hideKeyboard(this@AddMedicineActivity)
        llQuantityTime!!.visibility = View.VISIBLE
        llViewMedicine!!.visibility = View.GONE

//            selectedMedicineId = "";
//            autoCompleteTextView.setText("");
//            autoCompleteTextView.setHint("Search medicine");
        selectedMedicineId = allMedicineList!![pos]!!.id.toString()
        tvSelectMedicine!!.text = allMedicineList!![pos]!!.medicineName
        medicineName = allMedicineList!![pos]!!.medicineName

        if (mTimingArr != null && mTimingArr.size > 0) {
            mTimingArr.clear()
        }
        for (i in addMedicineArr.indices) {
            val model = MedicineTimingModel()
            model.medicineTime = addMedicineArr[i]
            model.select = false
            mTimingArr!!.add(model)
        }
        selectMedicineTimeAdapter.setData(mTimingArr)
    }

    override fun onSelectTimeClick(selectedItem: String?, position: Int) {
        mTimingArr!![position].select = !mTimingArr!![position].select
        selectMedicineTimeAdapter.notifyDataSetChanged()
        medicineListArr.add(selectedItem)
        hideKeyboard(this@AddMedicineActivity)
    }

    private fun retrieveTasks() {
        AppExecutors.instance!!.diskIO().execute(Runnable {
            allMedicineList = mDb!!.personDao().allMedicine as List<Medicine>?
            Log.e("Page size is: ", allMedicineList!!.size.toString())
            runOnUiThread {
                runOnUiThread {
                    if (allMedicineList != null && allMedicineList!!.isNotEmpty()) {
                        val adapter =
                            AutoCompletePlaceAdapter(this@AddMedicineActivity, allMedicineList!!)
                        autoCompleteTextView!!.setAdapter(adapter)
                        autoCompleteTextView!!.onItemClickListener = onItemClickListener
                    }
                }
            }
        })
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        if (type == "addNewMedicine") {
            val response = data as Response<SaveMedicineResponse>
            if (response.isSuccessful && response.body()!!.errorCode == "1") {
                if (response.body()!!.data != null) {
                    llQuantityTime!!.visibility = View.GONE
                    llViewMedicine!!.visibility = View.VISIBLE
                    edtQuantity!!.setText("")
                    edtQuantity!!.hint = resources.getString(R.string.enter_quantity)
                    selectedTimeSlot = ""
                    medicineName = ""
                    selectedMedicineId = ""
                    autoCompleteTextView!!.setText("")
                    autoCompleteTextView!!.hint = "Search medicine"
                    tvMedicineName!!.text = response.body()!!.data.medicine
                    tvMedicineDose!!.text = response.body()!!.data.dose
                    val replaceString = response.body()!!.data.time_slot.replace(",", ", ")
                    tvMedicineTimeSlot!!.text = replaceString
                    setUserAttribute(this, "SaveMedicine", true)
                    val properties = Properties()
                    properties.addAttribute("medicineAdd", true)
                    properties.addAttribute("medicineName", response.body()!!.data.medicine)
                    properties.addAttribute("medicineDose", response.body()!!.data.dose)
                    properties.addAttribute("medicineTimeSlot",
                        response.body()!!.data.time_slot.replace(",", ", "))
                    trackEvent(this, "AddMedicine", properties)
                    if (mTimingArr != null && mTimingArr.size > 0) {
                        mTimingArr.clear()
                    }
                    for (i in addMedicineArr.indices) {
                        val model = MedicineTimingModel()
                        model.medicineTime = addMedicineArr[i]
                        model.select = false
                        mTimingArr!!.add(model)
                    }
                    selectMedicineTimeAdapter.setData(mTimingArr)
                    preferences!!.isMedicineDialogPopup(true)

                } else {
                    llViewMedicine!!.visibility = View.GONE
                    edtQuantity!!.setText("")
                    edtQuantity!!.hint = resources.getString(R.string.enter_quantity)
                    selectedTimeSlot = ""
                }
            } else {
                val properties = Properties()
                properties.addAttribute("medicineAdd", false)
                trackEvent(this, "AddMedicine", properties)
            }
        }
    }

    override fun onFailure(data: Any) {}


    val currentDate: String
        @RequiresApi(Build.VERSION_CODES.N)
        get() {
            val yearFormat = SimpleDateFormat("yyyy-MM-dd")
            val d = Date(System.currentTimeMillis())
            return yearFormat.format(d)
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

        fun isConnection(ctx: Context): Boolean {
            val connectivityManager =
                ctx.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val ni = connectivityManager.activeNetworkInfo
            return ni != null && ni.isAvailable && ni.isConnected
        }
    }
}