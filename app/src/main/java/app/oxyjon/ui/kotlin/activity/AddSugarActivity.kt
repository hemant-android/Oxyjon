package app.oxyjon.ui.kotlin.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.adapter.SelectMedicineTimeAdapter
import app.oxyjon.bean.MedicineTimingModel
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityAddSugarBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.retrofit.response.UserDataResponse
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.ui.kotlin.activity.adapter.CalendarAdapter
import app.oxyjon.utils.CalendarData
import app.oxyjon.utils.CheckConnection
import app.oxyjon.utils.FunctionHelper
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddSugarActivity : BaseActivity(), CalendarAdapter.CalendarInterface, IApiCallback,
    SelectMedicineTimeAdapter.ClickListener {

    private var selectTime: String? = ""
    private var yyyy_MM_dd: String? = ""
    var preferences: AppSharedPreferences? = null

    lateinit var binding: ActivityAddSugarBinding

    private val sdf = SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH)
    private val sdf_yyyy_MM_dd = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)


    private val cal = Calendar.getInstance(Locale.ENGLISH)
    private val calendarAdapter = CalendarAdapter(this, arrayListOf())

    private val selectMedicineTimeAdapter = SelectMedicineTimeAdapter(this)

    private val calendarList = ArrayList<CalendarData>()
    private val addMedicineArr = ArrayList<String>()
    private val mTimingArr = ArrayList<MedicineTimingModel>()

    private var profileId = ""
    private var sugar = ""

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSugarBinding.inflate(layoutInflater)
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

        binding.rvTime.apply {
            layoutManager = FlexboxLayoutManager(this@AddSugarActivity).apply {
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.FLEX_START
            }
            binding.rvTime.adapter = selectMedicineTimeAdapter
            selectMedicineTimeAdapter.setClickListener(this@AddSugarActivity)
        }

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.rvTime.adapter = selectMedicineTimeAdapter

        selectMedicineTimeAdapter.setData(mTimingArr)

        binding.tvSave.setOnClickListener {

            if (TextUtils.isEmpty(yyyy_MM_dd)) {
                Toast.makeText(this, "Please select date", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(binding.edtSugarValue.text.toString().trim())) {
                Toast.makeText(this, "Please enter sugar value", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(selectTime)) {
                Toast.makeText(this, "Please select time", Toast.LENGTH_SHORT).show()
            } else {
                val array = JSONArray()
                val jsonObject = JSONObject()
                jsonObject.put("date", yyyy_MM_dd)
                jsonObject.put("sugarLevel", binding.edtSugarValue.text.toString().trim())
                jsonObject.put("type", selectTime)
                sugar = array.put(jsonObject).toString()
                saveSugarValue(sugar)

                val properties = Properties()
                properties.addAttribute("date", yyyy_MM_dd)
                properties.addAttribute("sugarLevel", binding.edtSugarValue.text.toString().trim())
                properties.addAttribute("type", selectTime)
                MoEAnalyticsHelper.trackEvent(this, "SugarForm", properties)
            }


        }
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

            calendarView.setHasFixedSize(true)
            calendarView.adapter = calendarAdapter

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


        //  dates = 0 < MAX DAYS = 28 (For FEB)
        //  dates = 1 < MAX DAYS = 28 (For FEB)
        //  dates = 2 < MAX DAYS = 28 (For FEB)
        //  .....

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

    private fun saveSugarValue(sugar: String) {
        if (CheckConnection.isConnection(this)) {
            FunctionHelper.disable_user_Intration(this, resources.getString(R.string.loading))
            ApiCall.instance.saveSugarData(profileId, sugar, this)
        } else {
            Toast.makeText(this, "please check your internet connection", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "addSugarData") {
            val response = data as Response<UserDataResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {

                    val properties = Properties()
                    properties.addAttribute("sugarValueSave", true)
                    MoEAnalyticsHelper.trackEvent(this, "SugarForm", properties)

                    preferences!!.isSugarDialogPopup(true)
                    val intent = Intent(this@AddSugarActivity, SugarRecordedActivity::class.java)
                    intent.putExtra("message",response.body()!!.analyticMessage)
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

    override fun onSelectTimeClick(selectedItem: String?, position: Int) {

        when (selectedItem) {
            "Fasting" -> {
                selectTime = "Fasting"
            }

            "Post- Breakfast" -> {
                selectTime = "PP"
            }

            "Pre- Lunch" -> {
                selectTime = "BeforeLunch"
            }

            "Post Lunch" -> {
                selectTime = "AfterLunch"
            }

            "Pre Dinner" -> {
                selectTime = "BeforeDinner"
            }

            "Post Dinner" -> {
                selectTime = "AfterDinner"
            }

            "3 AM" -> {
                selectTime = "Midnight"
            }

            "Random" -> {
                selectTime = "Random"
            }
        }

        for (i in mTimingArr.indices) {
            mTimingArr[i].select = mTimingArr[i].medicineTime == selectedItem
        }
        selectMedicineTimeAdapter.notifyDataSetChanged()
    }

}