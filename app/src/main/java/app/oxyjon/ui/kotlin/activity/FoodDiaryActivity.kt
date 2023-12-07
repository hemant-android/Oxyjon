package app.oxyjon.ui.kotlin.activity

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.adapter.MyFoodDiaryListAdapter
import app.oxyjon.bean.GetMyFoodDiaryResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityViewFoodDiaryBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.ui.activity.QuestionSecondActivity
import app.oxyjon.ui.kotlin.activity.adapter.CalendarAdapter
import app.oxyjon.ui.kotlin.activity.adapter.CalendarAdapter.CalendarInterface
import app.oxyjon.utils.CalendarData
import app.oxyjon.utils.FunctionHelper
import com.moengage.inapp.MoEInAppHelper
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class FoodDiaryActivity : BaseActivity(), IApiCallback, MyFoodDiaryListAdapter.ClickListener,
    CalendarInterface {
    private val myFoodDiaryListAdapter = MyFoodDiaryListAdapter(this)
    var preferences: AppSharedPreferences? = null

    lateinit var binding: ActivityViewFoodDiaryBinding

    private var myFoodDiaryList: ArrayList<GetMyFoodDiaryResponse.Datum>? = ArrayList()
    private var navigationType: String? = ""
    private var yyyy_MM_dd = ""
    var c = Calendar.getInstance()
    var sdf = SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH)
    var sdf_yyyy_MM_dd = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
    var cal = Calendar.getInstance(Locale.ENGLISH)
    var calendarAdapter = CalendarAdapter(this, ArrayList())
    var calendarList = ArrayList<CalendarData>()

    override fun onStart() {
        super.onStart()
        MoEInAppHelper.getInstance().showInApp(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        MoEInAppHelper.getInstance().onConfigurationChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewFoodDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MainApplication.currentActivity = this
        preferences = AppSharedPreferences.getInstance(this)
        binding.rvFoodList!!.adapter = myFoodDiaryListAdapter
        myFoodDiaryListAdapter.setClickListener(this)

        val bundle = intent.extras
        if (bundle != null) {
            navigationType = bundle.getString("navigationType")
        }
        init()
        getDates()
        binding.tvMonthYearPicker!!.setOnClickListener { setupDatePickerDueDate() }


        binding.imgBack!!.setOnClickListener { finish() }

        binding.llNoCal.setOnClickListener {
            val intent = Intent(this, QuestionSecondActivity::class.java)
            intent.putExtra("navigationType", "foodDiary")
            startActivity(intent)
        }

    }

    private fun init() {
        binding.tvMonthYearPicker!!.text = sdf.format(cal.time)
        yyyy_MM_dd = sdf_yyyy_MM_dd.format(cal.time)
        binding.calendarView!!.setHasFixedSize(true)
        binding.calendarView!!.adapter = calendarAdapter
    }

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this
        if (!TextUtils.isEmpty(navigationType) && navigationType.equals("addFoodDiary",
                ignoreCase = true)
        ) {
            navigationType = ""
            val intent = Intent(this, AddDiaryActivity::class.java)
            intent.putExtra("navigationType", "addFoodDiary")
            intent.putExtra("selectDate", yyyy_MM_dd)
            startActivity(intent)
        } else {
            if (isConnection(this@FoodDiaryActivity)) {
                FunctionHelper.disable_user_Intration(this, resources.getString(R.string.loading))
                ApiCall.instance
                    .getMyFoodDiaryList(preferences!!.getprofileid(), yyyy_MM_dd, this)
            } else {
                Toast.makeText(this@FoodDiaryActivity,
                    "please check your internet connection",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun callAPiDateWise(currentDate: String?) {
        if (isConnection(this@FoodDiaryActivity)) {
            ApiCall.instance
                .getMyFoodDiaryList(preferences!!.getprofileid(), currentDate, this)
        } else {
            Toast.makeText(this@FoodDiaryActivity,
                "please check your internet connection",
                Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSelectFoodItemClick(foodId: Int) {
        if (isConnection(this@FoodDiaryActivity)) {
            ApiCall.instance.removeMyFoodDiaryList(foodId.toString(), this)
        } else {
            Toast.makeText(this@FoodDiaryActivity,
                "please check your internet connection",
                Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "myFoodDiaryList") {
            val response = data as Response<GetMyFoodDiaryResponse>
            if (response.isSuccessful && response.body()!!.errorCode == "1") {
                if (response.body()!!.data != null && response.body()!!.data!!.size > 0) {
                    binding.tvNoRecordFound!!.visibility = View.GONE
                    binding.llFoodProtein!!.visibility = View.VISIBLE
                    if (myFoodDiaryList != null && myFoodDiaryList!!.size > 0) {
                        myFoodDiaryList!!.clear()
                    }

                    if (response.body()!!.foodDiaryInfo != null) {
                        binding.tvTitle.text = response.body()!!.foodDiaryInfo!!.blockTitle
                        binding.tvDes.text = response.body()!!.foodDiaryInfo!!.blockDetails
                        binding.tvCalTaken.text = response.body()!!.foodDiaryInfo!!.actionLink


                        binding.tvType.text = response.body()!!.foodDiaryInfo!!.weightType
                        if (response.body()!!.foodDiaryInfo!!.weightType == "Normal")
                        {
                            binding.tvType.setTextColor(ContextCompat.getColor(this, R.color.green))
                        }else{
                            binding.tvType.setTextColor(ContextCompat.getColor(this, R.color.orange))
                        }

                        binding.tvWeightTypeMsg.text = response.body()!!.foodDiaryInfo!!.weightTypeMessage

                        if (response.body()!!.foodDiaryInfo!!.dailyCalories != null && !TextUtils.isEmpty(
                                response.body()!!.foodDiaryInfo!!.dailyCalories) && response.body()!!.foodDiaryInfo!!.dailyCalories!!.toInt() > 0
                        ) {
                            binding.llNoCal.visibility = View.GONE
                            binding.llCal.visibility = View.VISIBLE
                            binding.circularProgressBar.maximum =
                                response.body()!!.foodDiaryInfo!!.dailyCalories!!.toFloat()

                            if (response.body()!!.foodDiaryInfo!!.dailyCaloriesTaken != null && !TextUtils.isEmpty(
                                    response.body()!!.foodDiaryInfo!!.dailyCaloriesTaken)
                            ) {
                                binding.circularProgressBar.progress =
                                    response.body()!!.foodDiaryInfo!!.dailyCaloriesTaken!!.toFloat()
                            } else {
                                binding.circularProgressBar.progress = 0f
                            }

                        } else {
                            binding.circularProgressBar.maximum = 0f

                            binding.llNoCal.visibility = View.VISIBLE
                            binding.llCal.visibility = View.GONE
                        }

                    }

                    myFoodDiaryList = response.body()!!.data

                    myFoodDiaryListAdapter.setData(myFoodDiaryList, yyyy_MM_dd)

                    if (response.body()!!.totalDataCounts != null) {
                        if (response.body()!!.totalDataCounts!!.totalProtein != null) {
                            binding.tvTotalProtein!!.visibility = View.VISIBLE
                            val df = DecimalFormat()
                            df.maximumFractionDigits = 1
                            val totalCalorie =
                                response.body()!!.totalDataCounts!!.totalProtein!!.toDouble()
                            binding.tvTotalProtein!!.text = "" + df.format(totalCalorie)
                        } else {
                            binding.tvTotalProtein!!.visibility = View.GONE
                        }
                        if (response.body()!!.totalDataCounts!!.totalCarbs != null) {
                            binding.tvTotalCarbs!!.visibility = View.VISIBLE
                            val df = DecimalFormat()
                            df.maximumFractionDigits = 1
                            val totalCalorie =
                                response.body()!!.totalDataCounts!!.totalCarbs!!.toDouble()
                            binding.tvTotalCarbs!!.text = "" + df.format(totalCalorie)
                        } else {
                            binding.tvTotalCarbs!!.visibility = View.GONE
                        }
                        if (response.body()!!.totalDataCounts!!.totalFats != null) {
                            binding.tvTotalFats!!.visibility = View.VISIBLE
                            val df = DecimalFormat()
                            df.maximumFractionDigits = 1
                            val totalCalorie =
                                response.body()!!.totalDataCounts!!.totalFats!!.toDouble()
                            binding.tvTotalFats!!.text = "" + df.format(totalCalorie)
                        } else {
                            binding.tvTotalFats!!.visibility = View.GONE
                        }
                        if (response.body()!!.totalDataCounts!!.totalFiber != null) {
                            binding.tvTotalFiber!!.visibility = View.VISIBLE
                            val df = DecimalFormat()
                            df.maximumFractionDigits = 1
                            val totalCalorie =
                                response.body()!!.totalDataCounts!!.totalFiber!!.toDouble()
                            binding.tvTotalFiber!!.text = "" + df.format(totalCalorie)
                        } else {
                            binding.tvTotalFiber!!.visibility = View.GONE
                        }
                    }
                }
            } else {
                binding.llFoodProtein!!.visibility = View.GONE
                binding.tvNoRecordFound!!.visibility = View.VISIBLE
                if (myFoodDiaryList != null && myFoodDiaryList!!.size > 0) {
                    myFoodDiaryList!!.clear()
                }
                myFoodDiaryListAdapter.setData(myFoodDiaryList, yyyy_MM_dd)
            }
        } else if (type == "removeFoodDiary") {
            if (isConnection(this@FoodDiaryActivity)) {
                ApiCall.instance.getMyFoodDiaryList(preferences!!.getprofileid(),
                    yyyy_MM_dd, this)
            } else {
                Toast.makeText(this@FoodDiaryActivity,
                    "please check your internet connection",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }

    fun callIntent(intent: Intent, type: String?) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("navigationType", type)
        startActivity(intent)
    }

    override fun onSelect(calendarData: CalendarData, position: Int) {

        calendarList.forEachIndexed { index, calendarModel ->
            calendarModel.isSelected = index == position
        }

        binding.tvMonthYearPicker!!.text = sdf.format(calendarData.data)
        yyyy_MM_dd = sdf_yyyy_MM_dd.format(calendarData.data)

        calendarAdapter.updateList(calendarList)

        callAPiDateWise(yyyy_MM_dd)
    }

    private fun setupDatePickerDueDate() {
        DatePickerDialog(this,
            dateListenerDate,  // set DatePickerDialog to point to today's date when it loads up
            cal[Calendar.YEAR],
            cal[Calendar.MONTH],
            cal[Calendar.DAY_OF_MONTH]).show()
    }

    private val dateListenerDate =
        OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal[Calendar.YEAR] = year
            cal[Calendar.MONTH] = monthOfYear
            cal[Calendar.DAY_OF_MONTH] = dayOfMonth
            updateDateFirstRegistration()
        }

    private fun updateDateFirstRegistration() {
        binding.tvMonthYearPicker!!.text = sdf.format(cal.time)
        yyyy_MM_dd = sdf_yyyy_MM_dd.format(cal.time)
        getDates()

        callAPiDateWise(yyyy_MM_dd)
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

    companion object {
        fun isConnection(ctx: Context): Boolean {
            val connectivityManager =
                ctx.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val ni = connectivityManager.activeNetworkInfo
            return ni != null && ni.isAvailable && ni.isConnected
        }
    }
}