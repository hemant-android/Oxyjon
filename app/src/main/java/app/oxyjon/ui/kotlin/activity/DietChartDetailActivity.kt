package app.oxyjon.ui.kotlin.activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.R
import app.oxyjon.bean.MyDietPlanResponse
import app.oxyjon.bean.MyMeasurementResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityDietChartDetailBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.ui.kotlin.activity.adapter.MeasurementAdapter
import app.oxyjon.ui.kotlin.activity.adapter.ViewPagerDietAdapter
import app.oxyjon.utils.CheckConnection.isConnection
import app.oxyjon.utils.FunctionHelper
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Response

class DietChartDetailActivity : BaseActivity(), IApiCallback {
    lateinit var binding: ActivityDietChartDetailBinding
    var preferences: AppSharedPreferences? = null

    private val mAdapter: MeasurementAdapter by lazy { MeasurementAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDietChartDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppSharedPreferences.getInstance(this)

        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.llMeasurement.setOnClickListener {
            dialogMeasurement()
        }

        if (isConnection(this)) {
            FunctionHelper.disable_user_Intration(this, resources.getString(R.string.loading))
            ApiCall.instance.geMyDietChartDetail(this)
        } else {
            Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show()
        }

        if (isConnection(this)) {
            FunctionHelper.disable_user_Intration(this, resources.getString(R.string.loading))
            ApiCall.instance.geMeasurementsChartDetail(this)
        } else {
            Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "MeasurementResponse") {
            val response = data as Response<MyMeasurementResponse>
            if (response.isSuccessful && response.body()!!.errorCode == "0") {
                if (response.body()!!.data?.size!! > 0) {
                    mAdapter.setData(response.body()!!.data)
                }
            }
        }
        if (type == "MyDietPlanResponse") {
            val response = data as Response<MyDietPlanResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {
                    if (response.body()!!.data != null && response.body()!!.data?.size!! > 0) {

                        val adapter = ViewPagerDietAdapter(
                            supportFragmentManager,
                            lifecycle,
                            response.body()!!.data
                        )
                        binding.viewPager.adapter = adapter

                        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                            when (response.body()!!.data[position].meal_name) {
                                "early_morning" -> {
                                    tab.text = "Early Morning"
                                }

                                "breakfast" -> {
                                    tab.text = "Breakfast"
                                }

                                "morning_snack" -> {
                                    tab.text = "Morning Snack"
                                }

                                "lunch" -> {
                                    tab.text = "Lunch"
                                }

                                "evening_snack" -> {
                                    tab.text = "Evening Snack"
                                }

                                "dinner" -> {
                                    tab.text = "Dinner"
                                }

                                "bed_time" -> {
                                    tab.text = "Bed Time"
                                }

                                else -> {
                                    tab.text = response.body()!!.data[position].meal_name
                                }
                            }
                        }.attach()

                    }
                }
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }

    private fun dialogMeasurement() {
        var dialog = Dialog(this, R.style.DialogSlideAnim)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)

        val window: Window? = dialog.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.BOTTOM
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        window.attributes = wlp

        dialog.setContentView(R.layout.dialog_measurement)

        val rvMeasurement = dialog.findViewById(R.id.rvMeasurement) as RecyclerView
        val llOk = dialog.findViewById(R.id.llOk) as LinearLayout

        rvMeasurement.adapter = mAdapter

        llOk.setOnClickListener {
            if (dialog != null && dialog.isShowing) {
                dialog.dismiss()
            }
        }

        if (dialog != null && dialog.isShowing) {
            dialog.dismiss()
        } else {
            dialog.show()
        }
    }
}