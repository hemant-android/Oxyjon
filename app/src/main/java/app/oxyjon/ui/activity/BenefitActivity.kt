package app.oxyjon.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.adapter.BenefitFirstAdapter
import app.oxyjon.bean.BenefitResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.ui.kotlin.activity.BlogDetailActivity
import app.oxyjon.ui.kotlin.activity.DashboardActivity
import app.oxyjon.ui.kotlin.activity.DoctorConsultationActivity
import app.oxyjon.ui.kotlin.activity.PlanDetailActivity
import app.oxyjon.utils.CheckConnection
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper.trackEvent
import retrofit2.Response


/**
 * @BenefitActivity
 */
class BenefitActivity : BaseActivity(), IApiCallback, BenefitFirstAdapter.OnClickListener {
    var preferences: AppSharedPreferences? = null

    @JvmField
    @BindView(R.id.tvTitle)
    var tvTitle: TextView? = null

    @JvmField
    @BindView(R.id.tvDesc)
    var tvDesc: TextView? = null

    @JvmField
    @BindView(R.id.imgTopBanner)
    var imgTopBanner: ImageView? = null

    @JvmField
    @BindView(R.id.tvPlanForUserName)
    var tvPlanForUserName: TextView? = null

    @JvmField
    @BindView(R.id.tvSkip)
    var tvSkip: TextView? = null

    @JvmField
    @BindView(R.id.rvPlan)
    var rvPlan: RecyclerView? = null

    var mBenefitFirstAdapter: BenefitFirstAdapter? = null
    var screenOnBoard = true

    private var benefitList: ArrayList<BenefitResponse.Data.Benefit>? = ArrayList()
    private var planList: ArrayList<BenefitResponse.Data.Plan>? = ArrayList()
    private var type: String? = ""
    private var id: String? = ""

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_benifit_screen_new)
        ButterKnife.bind(this)
        preferences = AppSharedPreferences.getInstance(this)

        MainApplication.currentActivity = this

        val bundle = intent.extras
        if (bundle != null) {
            type = bundle.getString("type")
            id = bundle.getString("id") ?: ""
        }
        if (CheckConnection.isConnection(this)) {
            ApiCall.instance.getAllBenefit(preferences!!.getprofileid(), this)
        } else {
            Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show()
        }

        mBenefitFirstAdapter = BenefitFirstAdapter(this)
        rvPlan!!.adapter = mBenefitFirstAdapter
        mBenefitFirstAdapter!!.setClickListener(this)

        val properties = Properties()
        properties.addAttribute("opened", true)
        trackEvent(this, "journeySegregationScreen", properties)
        if (type != null && !TextUtils.isEmpty(type)) {
            when (type) {
                "DietPlan12Month" -> {
                    val intent = Intent(this, PlanDetailActivity::class.java)
                    intent.putExtra("planId", 6)
                    startActivity(intent)
                }

                "DietPlan6Month" -> {
                    val intent = Intent(this, PlanDetailActivity::class.java)
                    intent.putExtra("planId", 5)
                    startActivity(intent)
                }

                "DietPlan3Month" -> {
                    val intent = Intent(this, PlanDetailActivity::class.java)
                    intent.putExtra("planId", 4)
                    startActivity(intent)
                }

                "DietPlan1Month" -> {
                    val intent = Intent(this, PlanDetailActivity::class.java)
                    intent.putExtra("planId", 3)
                    startActivity(intent)
                }

                "onboardingSecond" -> {
                    val intent = Intent(this, QuestionSecondActivity::class.java)
                    intent.putExtra("navigationType", "Benefit")
                    startActivity(intent)
                }

                "Blog" -> {
                    val intent = Intent(this, BlogDetailActivity::class.java)
                    intent.putExtra("navType", "deepLink")
                    intent.putExtra("blogId", id)
                    startActivity(intent)
                }
            }
        }

        tvSkip!!.setOnClickListener {
            if (screenOnBoard) {
                val intent = Intent(this@BenefitActivity, QuestionSecondActivity::class.java)
                intent.putExtra("navigationType", "Benefit")
                startActivity(intent)
            } else {
                val intent = Intent(this, DashboardActivity::class.java)
                val info: AppSharedPreferences = AppSharedPreferences.getInstance(this@BenefitActivity)!!
                info.userOnBoard = "2"
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    /**
     * Navigate corresponding screen
     * if type equal verifyOtp that manage blow conditions
     * if user is screen_quest1 equal true then navigate onBoard first screen @QuestionFirstActivity
     * if user is screen_quest2 equal true then navigate segregation screen @BenefitActivity
     * if user is userOnBoard equal 2 then navigate Dashboard screen @DashboardActivity
     */
    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        val response = data as Response<BenefitResponse>
        if (response.isSuccessful) {
            if (response.body()!!.errorCode == "0") {

                screenOnBoard = response.body()!!.data.screen_quest2

                if (response.body()!!.data?.payment_status == "Paid" || response.body()!!.data?.payment_status == "Expired" || response.body()!!.data?.payment_status == "Extended") {
                    if (screenOnBoard) {
                        val intent = Intent(this@BenefitActivity, QuestionSecondActivity::class.java)
                        intent.putExtra("navigationType", "Benefit")
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, DashboardActivity::class.java)
                        val info: AppSharedPreferences =
                            AppSharedPreferences.getInstance(this@BenefitActivity)!!
                        info.userOnBoard = "2"
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
                else if (response.body()!!.data?.payment_status == "Free" && response.body()!!.data?.on_board_status == "Onboarded") {
                    if (screenOnBoard) {
                        val intent = Intent(this@BenefitActivity, QuestionSecondActivity::class.java)
                        intent.putExtra("navigationType", "Benefit")
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, DashboardActivity::class.java)
                        val info: AppSharedPreferences =
                            AppSharedPreferences.getInstance(this@BenefitActivity)!!
                        info.userOnBoard = "2"
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
                else{
                    if (benefitList != null && benefitList!!.size > 0) {
                        benefitList!!.clear()
                    }
                    if (planList != null && planList!!.size > 0) {
                        planList!!.clear()
                    }

                    tvPlanForUserName!!.text = response.body()!!.data.health_plan_heading

                    if (response.body()!!.data.top_banner_url != null && !TextUtils.isEmpty(response.body()!!.data.top_banner_url)) {
                        try {
                            if (!isFinishing) {
                                Glide.with(applicationContext).load(response.body()!!.data.top_banner_url)
                                    .placeholder(R.drawable.progress_animation).into(
                                        (imgTopBanner)!!
                                    )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }

                    if (response.body()!!.data != null && response.body()!!.data.benefit != null && response.body()!!.data.benefit.size > 0) {
                        benefitList = response.body()!!.data.benefit

                        tvTitle!!.text = benefitList!![0].title
                        tvDesc!!.text = benefitList!![0].details
                    }

                    if (response.body()!!.data?.is_hp_select_ui_active!=null && !TextUtils.isEmpty(response.body()!!.data?.is_hp_select_ui_active))
                    {
                        if (response.body()!!.data?.is_hp_select_ui_active == "non_skip_ui") {
                            tvSkip!!.visibility = View.GONE
                            if (response.body()!!.data != null && response.body()!!.data.plan_list != null && response.body()!!.data.plan_list.size > 0) {
                                planList = response.body()!!.data.plan_list
                                mBenefitFirstAdapter!!.setData(planList)
                            }
                        }
                        else if (response.body()!!.data?.is_hp_select_ui_active == "skip_ui") {
                            tvSkip!!.visibility = View.VISIBLE
                            if (response.body()!!.data != null && response.body()!!.data.plan_list_ui2 != null && response.body()!!.data.plan_list_ui2.size > 0) {
                                planList = response.body()!!.data.plan_list_ui2
                                mBenefitFirstAdapter!!.setData(planList)
                            }
                        }
                        else if (response.body()!!.data?.is_hp_select_ui_active == "No_ui") {
                            tvSkip!!.visibility = View.GONE
                            if (screenOnBoard) {
                                val intent = Intent(this@BenefitActivity, QuestionSecondActivity::class.java)
                                intent.putExtra("navigationType", "Benefit")
                                startActivity(intent)
                            } else {
                                val intent = Intent(this, DashboardActivity::class.java)
                                val info: AppSharedPreferences =
                                    AppSharedPreferences.getInstance(this@BenefitActivity)!!
                                info.userOnBoard = "2"
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                        }
                    }else{
                        if (response.body()!!.data != null && response.body()!!.data.plan_list != null && response.body()!!.data.plan_list.size > 0) {
                            planList = response.body()!!.data.plan_list
                            mBenefitFirstAdapter!!.setData(planList)
                        }
                    }
                }
            }
        }
    }

    override fun onFailure(data: Any) {
        Log.e("data","Failure")
    }
    override fun onPlanClick(planType: String, planId: String) {
        if (!TextUtils.isEmpty(planType) && planType.equals("health_plans", ignoreCase = true)) {
            val intent = Intent(this@BenefitActivity, PlanDetailActivity::class.java)
            intent.putExtra("planId", planId.toInt())
            intent.putExtra("planName", "Health plan")
            startActivity(intent)
            val properties = Properties()
            properties.addAttribute("planType", planType)
            properties.addAttribute("planId", planId)
            trackEvent(this, "OnSegregationSugarReductionPlan", properties)
        } else if (!TextUtils.isEmpty(planType) && planType.equals(
                "doctor_consultation",
                ignoreCase = true
            )
        ) {
            val intent = Intent(this@BenefitActivity, DoctorConsultationActivity::class.java)
            intent.putExtra("planName", "Doctor consultation")
            startActivity(intent)
            val properties = Properties()
            properties.addAttribute("planType", planType)
            properties.addAttribute("planId", planId)
            trackEvent(this, "OnSegregationDoctorConsultationPlan", properties)
        } else {
            val properties = Properties()
            properties.addAttribute("planType", planType)
            trackEvent(this, "OnSegregationFree", properties)
            if (screenOnBoard) {
                val intent = Intent(this@BenefitActivity, QuestionSecondActivity::class.java)
                intent.putExtra("navigationType", "Benefit")
                startActivity(intent)
            } else {
                val intent = Intent(this, DashboardActivity::class.java)
                val info: AppSharedPreferences =
                    AppSharedPreferences.getInstance(this@BenefitActivity)!!
                info.userOnBoard = "2"
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }
}