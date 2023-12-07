package app.oxyjon.ui.kotlin.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.bean.MedicineTimingModel
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityAddBpBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.retrofit.response.UserDataResponse
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.ui.kotlin.activity.adapter.CalendarAdapter
import app.oxyjon.utils.CalendarData
import app.oxyjon.utils.CheckConnection
import app.oxyjon.utils.FunctionHelper
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddBloodPressureActivity : BaseActivity(), CalendarAdapter.CalendarInterface, IApiCallback {

    private var yyyy_MM_dd: String? = ""
    var preferences: AppSharedPreferences? = null

    lateinit var binding: ActivityAddBpBinding

    private val sdf = SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH)
    private val sdf_yyyy_MM_dd = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    private val sdf_HR_MM_ = SimpleDateFormat("KK mm aaa", Locale.ENGLISH)

    private val cal = Calendar.getInstance(Locale.ENGLISH)
    private val calendarAdapter = CalendarAdapter(this, arrayListOf())

    private val calendarList = ArrayList<CalendarData>()
    private val addMedicineArr = ArrayList<String>()
    private val mTimingArr = ArrayList<MedicineTimingModel>()

    private var profileId = ""
    private var bloodPressure = ""

    private var checkedItemHr = -1
    private var checkedItemMin = -1
    private var checkedItemAmPm = -1

    private var amPm = arrayOf("PM", "AM")
    private var hr = arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "00")
    private var min = arrayOf(
        "01",
        "02",
        "03",
        "04",
        "05",
        "06",
        "07",
        "08",
        "09",
        "10",
        "11",
        "12",
        "13",
        "14",
        "15",
        "16",
        "17",
        "18",
        "19",
        "20",
        "21",
        "22",
        "23",
        "24",
        "25",
        "26",
        "27",
        "28",
        "29",
        "30",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppSharedPreferences.getInstance(this)

        MainApplication.currentActivity = this

        profileId = preferences!!.getprofileid()!!

        addMedicineArr.add("Fasting")
        addMedicineArr.add("Post- Breakfast")
        addMedicineArr.add("Pre- Lunch")
        addMedicineArr.add("Post Lunch")
        addMedicineArr.add("Pre Dinner")
        addMedicineArr.add("Post Dinner")
        addMedicineArr.add("3 AM")
        addMedicineArr.add("Random")

        for (i in addMedicineArr.indices) {
            val model = MedicineTimingModel()
            model.medicineTime = addMedicineArr[i]
            model.select = false
            mTimingArr.add(model)
        }

        init()
        clickListener()
        getDates()


        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.tvHr.setOnClickListener {
            selectHr()
        }

        binding.tvMin.setOnClickListener {
            selectMin()
        }
        binding.tvAMPM.setOnClickListener {
            selectAmPm()
        }


        binding.tvSave.setOnClickListener {

            var strHr = binding.tvHr.text.toString().trim()
            var strMin = binding.tvMin.text.toString().trim()
            var strAmPm = binding.tvAMPM.text.toString().trim()

            if (TextUtils.isEmpty(yyyy_MM_dd)) {
                Toast.makeText(this, "Please select date", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(binding.edtSystolicValue.text.toString().trim())) {
                Toast.makeText(this, "Please enter Systolic value", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(binding.edtDiastolicValue.text.toString().trim())) {
                Toast.makeText(this, "Please enter diastolic value", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(strHr)) {
                Toast.makeText(this, "Please select hours", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(strMin)) {
                Toast.makeText(this, "Please select minutes", Toast.LENGTH_SHORT).show()
            } else {

                val array = JSONArray()
                val jsonObject = JSONObject()
                jsonObject.put("date", yyyy_MM_dd)
                jsonObject.put("time", strHr)
                jsonObject.put("minute", strMin)
                jsonObject.put("ampm", strAmPm)
                jsonObject.put("systolic", binding.edtSystolicValue.text.toString().trim())
                jsonObject.put("diastolic", binding.edtDiastolicValue.text.toString().trim())
                jsonObject.put("pulse", binding.edtPluseValue.text.toString().trim())
                bloodPressure = array.put(jsonObject).toString()

                saveBloodPressureValue(bloodPressure)

                val properties = Properties()
                properties.addAttribute("date", yyyy_MM_dd)
                properties.addAttribute("time", strHr)
                properties.addAttribute("minute", strMin)
                properties.addAttribute("ampm", strAmPm)
                properties.addAttribute("systolic", binding.edtSystolicValue.text.toString().trim())
                properties.addAttribute("diastolic",binding.edtDiastolicValue.text.toString().trim())
                properties.addAttribute("pulse",binding.edtPluseValue.text.toString().trim())
                MoEAnalyticsHelper.trackEvent(this, "BloodPressureForm", properties)
            }


        }
    }

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this
    }

    override fun onSelect(calendarData: CalendarData, position: Int) {

        // You can get Selected date here....
        binding.tvMonthYearPicker!!.text = sdf.format(calendarData.data)
        calendarList.forEachIndexed { index, calendarModel ->
            calendarModel.isSelected = index == position
        }

        calendarAdapter.updateList(calendarList)
        yyyy_MM_dd = sdf_yyyy_MM_dd.format(calendarData.data)
    }

    private fun init() {
        binding.apply {

            tvMonthYearPicker.text = sdf.format(cal.time)

            yyyy_MM_dd = sdf_yyyy_MM_dd.format(cal.time)

            var strHr = sdf_HR_MM_.format(cal.time).split(" ")[0]
            var strMin = sdf_HR_MM_.format(cal.time).split(" ")[1]
            var strAmPm = sdf_HR_MM_.format(cal.time).split(" ")[2]

            binding.tvHr.text = strHr
            binding.tvMin.text = strMin
            binding.tvAMPM.text = strAmPm

            calendarView.setHasFixedSize(true)
            calendarView.adapter = calendarAdapter

            for (i in hr.indices) {
                if (hr[i] == strHr) {
                    checkedItemHr = i
                }
            }

            for (i in min.indices) {
                if (min[i] == strMin) {
                    checkedItemMin = i
                }
            }
            for (i in amPm.indices) {
                if (amPm[i] == strAmPm) {
                    checkedItemAmPm = i
                }
            }

        }
    }

    private fun clickListener() {
        binding.tvMonthYearPicker.setOnClickListener {
//            displayDatePicker()
            setupDatePickerDueDate()
        }
    }

    private fun setupDatePickerDueDate() {
        DatePickerDialog(
            this, dateListenerFirstRegistrationDate,
            // set DatePickerDialog to point to today's date when it loads up
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).show()

    }

    private val dateListenerFirstRegistrationDate =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateFirstRegistration()
        }

    private fun updateDateFirstRegistration() {
        val myFormat = "dd-MM-yyyy" // mention the format you need
//        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.tvMonthYearPicker.text = sdf.format(cal.time)
        yyyy_MM_dd = sdf_yyyy_MM_dd.format(cal.time)

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
//            dateList.add(CalendarData(monthCalendar.time))

            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)   // Increment Day By 1
        }

        calendarList.clear()
        calendarList.addAll(dateList)
        calendarAdapter.updateList(dateList)

        for (i in calendarList.indices) {
            if (calendarList[i].isSelected) {
                binding.calendarView.scrollToPosition(i)
            }
        }
    }

    private fun saveBloodPressureValue(bpData: String) {
        if (CheckConnection.isConnection(this)) {
            FunctionHelper.disable_user_Intration(this, resources.getString(R.string.loading))
            ApiCall.instance.saveBloodPresureData(profileId, bpData, this)
        } else {
            Toast.makeText(this, "please check your internet connection", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "addBPData") {
            val response = data as Response<UserDataResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {

                    val properties = Properties()
                    properties.addAttribute("bloodPressureSave", true)
                    MoEAnalyticsHelper.trackEvent(this, "BloodPressureForm", properties)
                    val intent =
                        Intent(this@AddBloodPressureActivity, AddBPRecordedActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, response.body()!!.errorMsg, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }

    private fun selectHr() {
        val alertDialog = AlertDialog.Builder(this@AddBloodPressureActivity)
        alertDialog.setTitle("Choose an Hr")
        alertDialog.setSingleChoiceItems(hr,
            checkedItemHr,
            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                checkedItemHr = which
                binding.tvHr.text = hr[which]
                dialog.dismiss()
            })
        alertDialog.setNegativeButton(
            "Cancel"
        ) { dialog: DialogInterface?, which: Int -> }
        val customAlertDialog = alertDialog.create()
        customAlertDialog.show()
    }

    private fun selectMin() {
        val alertDialog = AlertDialog.Builder(this@AddBloodPressureActivity)
        alertDialog.setTitle("Choose an Min")
        alertDialog.setSingleChoiceItems(min,
            checkedItemMin,
            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                checkedItemMin = which
                binding.tvMin.text = min[which]
                dialog.dismiss()
            })
        alertDialog.setNegativeButton(
            "Cancel"
        ) { dialog: DialogInterface?, which: Int -> }
        val customAlertDialog = alertDialog.create()
        customAlertDialog.show()
    }

    private fun selectAmPm() {
        val alertDialog = AlertDialog.Builder(this@AddBloodPressureActivity)
        alertDialog.setTitle("Choose an AM PM")
        alertDialog.setSingleChoiceItems(amPm,
            checkedItemAmPm,
            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                checkedItemAmPm = which
                binding.tvAMPM.text = amPm[which]
                dialog.dismiss()
            })
        alertDialog.setNegativeButton(
            "Cancel"
        ) { dialog: DialogInterface?, which: Int -> }
        val customAlertDialog = alertDialog.create()
        customAlertDialog.show()
    }


}