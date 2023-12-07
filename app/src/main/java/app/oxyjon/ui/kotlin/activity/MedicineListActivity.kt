package app.oxyjon.ui.kotlin.activity

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import app.oxyjon.R
import app.oxyjon.bean.MyMedicineResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityMedicineListBinding
import app.oxyjon.receiver.AlarmBroadcastReceiver
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.ui.activity.AddMedicineActivity
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.ui.kotlin.activity.adapter.MedicineListAdapter
import app.oxyjon.ui.kotlin.activity.adapter.MedicineListViewPagerAdapter
import app.oxyjon.utils.FunctionHelper
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper
import com.moengage.inapp.MoEInAppHelper
import retrofit2.Response
import java.util.*

class MedicineListActivity : BaseActivity(), IApiCallback, MedicineListAdapter.ClickListener {

    lateinit var binding: ActivityMedicineListBinding
    var preferences: AppSharedPreferences? = null


    private var navigationType: String? = ""
    private var myMedicineList: ArrayList<MyMedicineResponse.Data>? = ArrayList()
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
        binding = ActivityMedicineListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = AppSharedPreferences.getInstance(this)

        val bundle = intent.extras
        if (bundle != null) {
            navigationType = bundle.getString("navigationType")
        }

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.tvAddMedicine!!.setOnClickListener {

            val properties = Properties()
            properties.addAttribute("isClick", true)
            MoEAnalyticsHelper.trackEvent(this, "ClickAddMedicineButton", properties)
            callIntent(Intent(this@MedicineListActivity, AddMedicineActivity::class.java),
                "medicines")
        }

        binding.tvSaveAlarm.setOnClickListener {
            if (myMedicineList != null) {
                for (names in myMedicineList!!) {
                    if (names.medicineitems != null && names.medicineitems.size > 0) {
                        when (names.name) {
                            "Before Breakfast" -> {
                                setAlarm(7, 15, 1, "Before breakfast")
                            }
                            "After Breakfast" -> {
                                setAlarm(8, 30, 2, "After breakfast")
                            }
                            "Before Lunch" -> {
                                setAlarm(12, 30, 3, "Before lunch")
                            }
                            "After Lunch" -> {
                                setAlarm(14, 1, 4, "After lunch")
                            }
                            "Before Dinner" -> {
                                setAlarm(19, 30, 5, "Before dinner")
                            }
                            "After Dinner" -> {
                                setAlarm(21, 15, 6, "After dinner")
                            }else ->{

                            }
                        }
                    }
                }
            }
            val properties = Properties()
            properties.addAttribute("isClick", true)
            MoEAnalyticsHelper.trackEvent(this, "ClickAlarmButton", properties)
            callIntent(Intent(this@MedicineListActivity, SaveAlarmActivity::class.java),
                "medicines")
        }
    }

    override fun onResume() {
        super.onResume()
        if (!TextUtils.isEmpty(navigationType) && navigationType.equals("medicines",
                ignoreCase = true)
        ) {
            navigationType = ""
            callIntent(Intent(this@MedicineListActivity, AddMedicineActivity::class.java),
                "medicines")
        } else {
            if (isConnection(this@MedicineListActivity)) {
                FunctionHelper.disable_user_Intration(this, resources.getString(R.string.loading))
                ApiCall.instance.getMyMedicineList(preferences!!.getprofileid(), this)
            } else {
                Toast.makeText(this@MedicineListActivity,
                    "please check your internet connection",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSelectMedicineClick(position: Int) {
        if (isConnection(this@MedicineListActivity)) {
//            ApiCall.getInstance().removeMedicineList(myMedicineList!![position].id.toString(), this)
        } else {
            Toast.makeText(this@MedicineListActivity,
                "please check your internet connection",
                Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "myMedicine") {
            val response = data as Response<MyMedicineResponse>
            if (response.isSuccessful && response.body()!!.errorCode == "1") {
                if (response.body()!!.data?.size!! > 0) {
                    if (myMedicineList != null && myMedicineList!!.size > 0) {
                        myMedicineList!!.clear()
                    }
                    myMedicineList = response.body()!!.data

                    val adapter =
                        MedicineListViewPagerAdapter(supportFragmentManager, myMedicineList!!)
                    binding.viewPager.adapter = adapter
                    binding.tbLayout.setupWithViewPager(binding.viewPager)

                } else {
                }
            } else {
                if (myMedicineList != null && myMedicineList!!.size > 0) {
                    myMedicineList!!.clear()
                }
            }
        } else if (type == "removeMedicine") {
            if (isConnection(this@MedicineListActivity)) {
                ApiCall.instance.getMedicineList(preferences!!.getprofileid(), this)
            } else {
                Toast.makeText(this@MedicineListActivity,
                    "please check your internet connection",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }

    fun callIntent(intent: Intent, type: String?) {
        intent.putExtra("navigationType", type)
        startForResult.launch(intent)
    }

    fun removeMedicine(posDeleteItem: Int) {
        var selectedTab = binding.tbLayout.selectedTabPosition

        if (myMedicineList?.size!! > 0) {
            myMedicineList!![selectedTab].medicineitems.removeAt(posDeleteItem)
        }
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                binding.rlSaveAlarm.visibility = View.VISIBLE
            } else {
                binding.rlSaveAlarm.visibility = View.VISIBLE
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

    private fun setAlarm(
        hour: Int,
        minit: Int,
        pos: Int,
        medicineTime: String,
    ) {
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = minit
        calendar[Calendar.SECOND] = 0

        Log.e("Hours $hour", "minit $minit")
        val alertIntent = Intent(this, AlarmBroadcastReceiver::class.java)
        alertIntent.putExtra("alarmValue", pos)
        alertIntent.putExtra("medicineName", "")
        alertIntent.putExtra("medicineTime", medicineTime)

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(this.applicationContext,pos,alertIntent,PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(this.applicationContext,pos,alertIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        }
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Objects.requireNonNull(alarmManager).setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,pendingIntent)
//            Objects.requireNonNull(alarmManager).setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,AlarmManager.INTERVAL_DAY,pendingIntent)
        } else {
            Objects.requireNonNull(alarmManager)[AlarmManager.RTC_WAKEUP, calendar.timeInMillis] =pendingIntent
        }
    }
}