package app.oxyjon.ui.kotlin.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import app.oxyjon.R
import app.oxyjon.bean.BPResponse
import app.oxyjon.bean.DietPlanHomeResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityDietChartBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.ui.kotlin.activity.adapter.GoalAdapter
import app.oxyjon.ui.kotlin.activity.adapter.MeasurementAdapter
import app.oxyjon.utils.CheckConnection.isConnection
import app.oxyjon.utils.FunctionHelper
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper
import retrofit2.Response

class DietChartActivity : BaseActivity(), IApiCallback {
    lateinit var binding: ActivityDietChartBinding
    var preferences: AppSharedPreferences? = null

    private val mAdapter: GoalAdapter by lazy { GoalAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDietChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppSharedPreferences.getInstance(this)

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.rvGoal.adapter = mAdapter

        binding.rlDietPlan.setOnClickListener {
            val properties = Properties()
            properties.addAttribute("isCLick", true)
            MoEAnalyticsHelper.trackEvent(this, "clickMyDietPlan", properties)

            val intent = Intent(this, DietChartDetailActivity::class.java)
            startActivity(intent)
        }

        binding.rlStressManagement.setOnClickListener {
            val properties = Properties()
            properties.addAttribute("isCLick", true)
            MoEAnalyticsHelper.trackEvent(this, "clickStressManagement", properties)

            val intent = Intent(this, StressManagementActivity::class.java)
            startActivity(intent)
        }
        binding.rlThingsDoAvoid.setOnClickListener {
            val properties = Properties()
            properties.addAttribute("isCLick", true)
            MoEAnalyticsHelper.trackEvent(this, "clickThingsDoAvoid", properties)

            val intent = Intent(this, ThingsAvoidActivity::class.java)
            startActivity(intent)
        }

        binding.rlExercisePlan.setOnClickListener {
            val properties = Properties()
            properties.addAttribute("isCLick", true)
            MoEAnalyticsHelper.trackEvent(this, "clickExercisePlan", properties)

            val intent = Intent(this, PhysicalActivity::class.java)
            startActivity(intent)
        }

        if (isConnection(this)) {
            FunctionHelper.disable_user_Intration(this, resources.getString(R.string.loading))
            ApiCall.instance.geDietChartDetailHome(this)
        } else {
            Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "DietChartHomeResponse") {
            val response = data as Response<DietPlanHomeResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {
                    if (response.body()!!.is_plan_active == "true") {
                        binding.llMain.visibility = View.VISIBLE
                        binding.tvNoRecordFound.visibility = View.GONE
                    } else {
                        binding.llMain.visibility = View.GONE
                        binding.tvNoRecordFound.visibility = View.VISIBLE
                        binding.tvNoRecordFound.text = response.body()!!.plan_active_message
                    }

                    if (response.body()!!.data != null && response.body()!!.data.daily_target?.size!! > 0) {
                        binding.tvTotalCal.text =
                            response.body()!!.data.daily_target[0].total_calories + "/Day"
                        binding.tvCarbohydrate.text = response.body()!!.data.daily_target[0].carb
                        binding.tvProtein.text = response.body()!!.data.daily_target[0].protein
                        binding.tvFat.text = response.body()!!.data.daily_target[0].fat
                        binding.tvFibre.text = response.body()!!.data.daily_target[0].fibers
                    }

                    if (response.body()!!.data != null && response.body()!!.data.goals?.size!! > 0) {
                        binding.llGoal.visibility = View.VISIBLE
                        mAdapter.setData(response.body()!!.data.goals)
                    }else{
                        binding.llGoal.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }
}