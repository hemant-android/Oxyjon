package app.oxyjon.ui.kotlin.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.adapter.BenefitWhatSeeOfferAdapter
import app.oxyjon.bean.MyCareTeamResponse
import app.oxyjon.bean.PremiumResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityMyCarePlanBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.utils.CheckConnection
import app.oxyjon.utils.FunctionHelper
import com.bumptech.glide.Glide
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper
import retrofit2.Response
import java.sql.Date
import java.text.SimpleDateFormat

class MyCarePlanActivity : BaseActivity(), IApiCallback {
    private var planId: String? = ""
    lateinit var binding: ActivityMyCarePlanBinding
    private var healthPlanIdDoctor: String? = ""
    private var isOnBoarded: Boolean? = false
    var preferences: AppSharedPreferences? = null

    private val adapter: BenefitWhatSeeOfferAdapter by lazy { BenefitWhatSeeOfferAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyCarePlanBinding.inflate(layoutInflater)
        setContentView(binding.root)


        preferences = AppSharedPreferences.getInstance(this)

        MainApplication.currentActivity = this

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.rvPlanBenefit.adapter = adapter

        binding.llDoctor.setOnClickListener {
            if (!isOnBoarded!!) {
                val intent = Intent(this, DoctorConsultationActivity::class.java)
                intent.putExtra("planId", healthPlanIdDoctor!!.toInt())
                intent.putExtra("planName", "Doctor consultation")
                startActivity(intent)

                val properties = Properties()
                properties.addAttribute("isClick", true)
                MoEAnalyticsHelper.trackEvent(this, "ClickDoctorPlan", properties)
            }
        }

        binding.llPlanExpire.setOnClickListener {
            val intent = Intent(this, PlanDetailActivity::class.java)
            intent.putExtra("planId", planId!!.toInt())
            intent.putExtra("planName", "Health plan")
            startActivity(intent)

            val properties = Properties()
            properties.addAttribute("isClick", true)
            MoEAnalyticsHelper.trackEvent(this, "ClickPlanExpire", properties)
        }

        binding.tvMessage.setOnClickListener {
            val intent = Intent(this, PlanDetailActivity::class.java)
            intent.putExtra("planId", planId!!.toInt())
            intent.putExtra("planName", "Health plan")
            startActivity(intent)

            val properties = Properties()
            properties.addAttribute("isClick", true)
            MoEAnalyticsHelper.trackEvent(this, "ClickOrangeBarPlan", properties)
        }

    }

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this

        if (CheckConnection.isConnection(this)) {
            FunctionHelper.disable_user_Intration(
                this,
                resources.getString(R.string.loading)
            )
            ApiCall.instance.getMyCarePlan(this)
        } else {
            Toast.makeText(
                this,
                "please check your internet connection",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "myCarePlan") {
            val response = data as Response<MyCareTeamResponse>
            if (response.isSuccessful) {
                if (response.isSuccessful && response.body()!!.errorCode == "1") {

                    isOnBoarded = response.body()!!.is_on_boarded

                    binding.llReferenceBanner.visibility = View.GONE
                    if (response.body()!!.no_of_educator_call_left != null) {
                        binding.tvEducatorCallLeft.text =
                            "" + response.body()!!.no_of_educator_call_left + " Call left"
                    }

                    if (response.body()!!.no_of_doc_call_left != null) {
                        binding.tvDrCallLeft.text =
                            "" + response.body()!!.no_of_doc_call_left + " Call left"
                    }

                    if (response.body()!!.educator?.size!! > 0) {
                        binding.tvEducatorName.text =
                            response.body()!!.educator[0].eductaor_name
                        Glide.with(this)
                            .load(response.body()!!.educator[0].profile_url)
                            .placeholder(R.drawable.progress_animation)
                            .into(binding.imgEducator)
                    }

                    if (response.body()!!.mydoctor?.size!! > 0) {
                        healthPlanIdDoctor = response.body()!!.mydoctor[0].health_plan_id
                        binding.tvDrName.text = response.body()!!.mydoctor[0].name
                        Glide.with(this).load(response.body()!!.mydoctor[0].profile_url)
                            .placeholder(R.drawable.progress_animation).into(binding.imgDoctor)
                    }


                    if (!TextUtils.isEmpty(response.body()!!.subscription_status) && response.body()!!.subscription_status == "Free") {
                        if (response.body()!!.profile_type != null && response.body()!!.profile_type == "B2B") {
                            binding.llReferenceBanner.visibility = View.VISIBLE

                            if (response.body()!!.b2b_health_plan.health_plan_banner != null && !TextUtils.isEmpty(
                                    response.body()!!.b2b_health_plan.health_plan_banner
                                )
                            ) {
                                try {
                                    if (!isFinishing) {
                                        Glide.with(applicationContext)
                                            .load(response.body()!!.b2b_health_plan.health_plan_banner)
                                            .placeholder(R.drawable.progress_animation).into(
                                                (binding.imgTopBanner)!!
                                            )
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }


                            if (response.body()!!.b2b_health_plan?.health_plan_detail?.size!! > 0) {
                                adapter.setData(response.body()!!.b2b_health_plan?.health_plan_detail!!)
                            }

                            if (response.body()!!.b2b_health_plan?.other_details!=null && !TextUtils.isEmpty(response.body()!!.b2b_health_plan?.other_details))
                            {
                                binding.tvOtherDetails.visibility = View.VISIBLE
                                binding.tvOtherDetails.text = response.body()!!.b2b_health_plan?.other_details
                            }else{
                                binding.tvOtherDetails.visibility = View.GONE
                            }


                        } else {
                            binding.llReferenceBanner.visibility = View.GONE
                            if (response.body()!!.on_board_status == "Onboarded") {
                                binding.tvMessage.visibility = View.VISIBLE

                                binding.rlPremium.visibility = View.GONE
                                binding.rlPremiumBlur.visibility = View.VISIBLE
                                binding.tvPlanName.visibility = View.VISIBLE
                            } else {
                                binding.tvMessage.visibility = View.GONE
                                binding.rlPremium.visibility = View.GONE
                                binding.rlPremiumBlur.visibility = View.GONE
                                binding.tvPlanName.visibility = View.GONE
                            }
                            binding.llEducator.visibility = View.GONE
                            binding.tvEducatorTxt.visibility = View.GONE
                            binding.llDoctor.visibility = View.GONE
                            binding.llProgress.visibility = View.GONE
                            binding.llPlanExpire.visibility = View.VISIBLE

                            binding.tvMessage.text = response.body()!!.premium_message

                            if (response.body()!!.healthplan_data?.size!! > 0) {

                                planId = response.body()!!.healthplan_data[0].health_plan_id
                                binding.tvPlanName.text =
                                    response.body()!!.healthplan_data[0].health_plan_name
                                Glide.with(this)
                                    .load(response.body()!!.healthplan_data[0].health_plan_banner)
                                    .placeholder(R.drawable.progress_animation)
                                    .into(binding.imgPlanExpired)
                            } else {
                                planId = response.body()!!.health_plan_id
                                Glide.with(this)
                                    .load(response.body()!!.banner_no_onboarding)
                                    .placeholder(R.drawable.progress_animation)
                                    .into(binding.imgPlanExpired)
                            }
                        }
                    } else {

                        if (response.body()!!.profile_type != null && response.body()!!.profile_type == "B2B") {

                            binding.llReferenceBanner.visibility = View.VISIBLE
                            binding.rvPlanBenefit.visibility = View.GONE
                            binding.tvOtherDetails.visibility = View.GONE
                            if (response.body()!!.b2b_health_plan.health_plan_banner != null && !TextUtils.isEmpty(
                                    response.body()!!.b2b_health_plan.health_plan_banner
                                )
                            ) {
                                try {
                                    if (!isFinishing) {
                                        Glide.with(applicationContext)
                                            .load(response.body()!!.b2b_health_plan.health_plan_banner)
                                            .placeholder(R.drawable.progress_animation).into(
                                                (binding.imgTopBanner)!!
                                            )
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }else{
                            binding.llReferenceBanner.visibility = View.GONE
                            binding.rvPlanBenefit.visibility = View.GONE
                            binding.tvOtherDetails.visibility = View.GONE
                        }

                        if (!TextUtils.isEmpty(response.body()!!.is_premium_member) && response.body()!!.is_premium_member == "Yes") {
                            binding.llEducator.visibility = View.VISIBLE
                            binding.tvEducatorTxt.visibility = View.VISIBLE
                            binding.llDoctor.visibility = View.VISIBLE
                            binding.tvPlanName.visibility = View.VISIBLE

                            binding.llProgress.visibility = View.VISIBLE
                            binding.llPlanExpire.visibility = View.GONE

                            binding.rlPremium.visibility = View.VISIBLE
                            binding.rlPremiumBlur.visibility = View.GONE

                            binding.tvMessage.visibility = View.GONE

                            if (response.body()!!.healthplan_data?.size!! > 0) {

                                planId = response.body()!!.healthplan_data[0].health_plan_id

                                binding.tvPlanName.text =
                                    response.body()!!.healthplan_data[0].health_plan_name

                                var currentDate = getCurrentDate()
                                var daysDifferent = FunctionHelper.daysCalculate(
                                    response.body()!!.healthplan_data[0].plan_start_date,
                                    response.body()!!.healthplan_data[0].plan_end_date
                                )
                                var currentProgress = FunctionHelper.daysCalculate(
                                    response.body()!!.healthplan_data[0].plan_start_date,
                                    currentDate
                                )

                                binding.progressPlan.max =
                                    response.body()!!.healthplan_data[0].healthplan_active_no_of_days.toInt()
                                binding.progressPlan.progress = currentProgress.toInt()

                                binding.tvPlanStartDate.text =
                                    "Start- " + response.body()!!.healthplan_data[0].plan_start_date
                                binding.tvPlanRemainingDays.text = "$daysDifferent days Left"

                            }

                        } else if (!TextUtils.isEmpty(response.body()!!.is_premium_member) && response.body()!!.is_premium_member == "Extended") {
                            binding.llEducator.visibility = View.VISIBLE
                            binding.tvEducatorTxt.visibility = View.VISIBLE
                            binding.llDoctor.visibility = View.VISIBLE
                            binding.tvPlanName.visibility = View.VISIBLE

                            binding.llProgress.visibility = View.VISIBLE
                            binding.llPlanExpire.visibility = View.GONE

                            binding.tvMessage.visibility = View.VISIBLE
                            binding.tvMessage.text = response.body()!!.premium_message

                            binding.rlPremium.visibility = View.GONE
                            binding.rlPremiumBlur.visibility = View.VISIBLE

                            if (response.body()!!.healthplan_data?.size!! > 0) {

                                planId = response.body()!!.healthplan_data[0].health_plan_id

                                binding.tvPlanName.text =
                                    response.body()!!.healthplan_data[0].health_plan_name

                                var currentDate = getCurrentDate()
                                var daysDifferent = FunctionHelper.daysCalculate(
                                    response.body()!!.healthplan_data[0].plan_start_date,
                                    response.body()!!.healthplan_data[0].plan_end_date
                                )
                                var currentProgress = FunctionHelper.daysCalculate(
                                    response.body()!!.healthplan_data[0].plan_start_date,
                                    currentDate
                                )

                                binding.progressPlan.max =
                                    response.body()!!.healthplan_data[0].healthplan_active_no_of_days.toInt()
                                binding.progressPlan.progress = currentProgress.toInt()

                                binding.tvPlanStartDate.text =
                                    "Start- " + response.body()!!.healthplan_data[0].plan_start_date
                                binding.tvPlanRemainingDays.text = "$daysDifferent days Left"

                            }

                        } else {
                            binding.llEducator.visibility = View.GONE
                            binding.tvEducatorTxt.visibility = View.GONE
                            binding.llDoctor.visibility = View.GONE

                            binding.llProgress.visibility = View.VISIBLE
                            binding.llPlanExpire.visibility = View.VISIBLE
                            binding.tvPlanName.visibility = View.VISIBLE

                            binding.tvMessage.visibility = View.VISIBLE
                            binding.tvMessage.text = response.body()!!.premium_message


                            binding.rlPremium.visibility = View.GONE
                            binding.rlPremiumBlur.visibility = View.VISIBLE

                            if (response.body()!!.healthplan_data?.size!! > 0) {

                                planId = response.body()!!.healthplan_data[0].health_plan_id
                                binding.tvPlanName.text =
                                    response.body()!!.healthplan_data[0].health_plan_name

                                Glide.with(this)
                                    .load(response.body()!!.healthplan_data[0].health_plan_banner)
                                    .placeholder(R.drawable.progress_animation)
                                    .into(binding.imgPlanExpired)

                                binding.progressPlan.max =
                                    response.body()!!.healthplan_data[0].healthplan_active_no_of_days.toInt()
                                binding.progressPlan.progress =
                                    response.body()!!.healthplan_data[0].healthplan_active_no_of_days.toInt()

                                binding.tvPlanStartDate.text = ""
                                binding.tvPlanRemainingDays.text = "Expired"

                            } else {
                                planId = response.body()!!.health_plan_id
                                Glide.with(this)
                                    .load(response.body()!!.banner_no_onboarding)
                                    .placeholder(R.drawable.progress_animation)
                                    .into(binding.imgPlanExpired)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }

    private fun getCurrentDate(): String? {
        val yearFormat = SimpleDateFormat("yyyy-MM-dd")
        val d = Date(System.currentTimeMillis())
        return yearFormat.format(d)
    }
}