package app.oxyjon.ui.kotlin.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.bean.BuyPlanResponse
import app.oxyjon.bean.HealthPlanDetailResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityDietPlanDetailScreenBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.retrofit.response.CommonResponse
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.ui.kotlin.activity.adapter.HealthDietPlanDetailCareTeamAdapter
import app.oxyjon.ui.kotlin.activity.adapter.HealthPlanDetailBenefitAdapter
import app.oxyjon.ui.kotlin.activity.adapter.HealthPlanDetailConsultationAdapter
import app.oxyjon.ui.kotlin.activity.adapter.HealthPlanDetailReviewAdapter
import app.oxyjon.utils.CheckConnection
import app.oxyjon.utils.FunctionHelper
import com.google.firebase.analytics.FirebaseAnalytics
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper.setUserAttribute
import com.moengage.core.analytics.MoEAnalyticsHelper.trackEvent
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject
import retrofit2.Response

class DietPlanDetailActivity : BaseActivity(), IApiCallback, PaymentResultWithDataListener {

    lateinit var binding: ActivityDietPlanDetailScreenBinding
    private var planId = ""
    private var planPrice: String? = ""
    private var planDetail: String? = ""
    private var planTitle: String? = ""
    private var healthPlanRequestId = ""
    private var orderId = ""
    private var paymentStatus = ""
    private var planName: String? = ""

    var preferences: AppSharedPreferences? = null
    var mFirebaseAnalytics: FirebaseAnalytics? = null

    private val mReviewAdapter: HealthPlanDetailReviewAdapter by lazy {
        HealthPlanDetailReviewAdapter(this)
    }
    private val mBenefitAdapter: HealthPlanDetailBenefitAdapter by lazy {
        HealthPlanDetailBenefitAdapter(this)
    }

    private val mCareTeamAdapter: HealthDietPlanDetailCareTeamAdapter by lazy {
        HealthDietPlanDetailCareTeamAdapter(this)
    }

    private val mConsultationAdapter: HealthPlanDetailConsultationAdapter by lazy {
        HealthPlanDetailConsultationAdapter(this)
    }

    private var reviewList: ArrayList<HealthPlanDetailResponse.HealthplanDetails.Review>? =
        arrayListOf()
    private var benefitList: ArrayList<HealthPlanDetailResponse.HealthplanDetails.Benefit>? =
        arrayListOf()

    private var careTeamList: ArrayList<HealthPlanDetailResponse.HealthplanDetails.Careteam>? =
        arrayListOf()

    private var consultationList: ArrayList<HealthPlanDetailResponse.HealthplanDetails.Consultation>? =
        arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDietPlanDetailScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppSharedPreferences.getInstance(this)

        MainApplication.currentActivity = this

        Checkout.preload(applicationContext)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val bundle = intent.extras
        if (bundle != null) {
            planId = bundle.getInt("planId").toString()
            planName = bundle.getString("planName") ?: ""
        }

        binding.imgBack.setOnClickListener {
            finish()
        }

        if (CheckConnection.isConnection(this)) {
            FunctionHelper.disable_user_Intration(this, resources.getString(R.string.loading))
            ApiCall.instance.getHealthPlanDetail(preferences!!.getprofileid(), planId, this)
        } else {
            Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show()
        }

        binding.rvUserReview.adapter = mReviewAdapter
        binding.rvBenefits.adapter = mBenefitAdapter
        binding.rvCareTeam.adapter = mCareTeamAdapter
        binding.rvConsultation.adapter = mConsultationAdapter

        binding.tvMakePayment.setOnClickListener {

            if (!TextUtils.isEmpty(planPrice)) {
                buyPlan(preferences!!.getprofileid(), planId, planTitle, planPrice)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this
    }

    private fun buyPlan(
        profileId: String?,
        planId: String?,
        planTitle: String?,
        planPrice: String?,
    ) {

        val properties = Properties()
        properties.addAttribute("buyNow", true)
        properties.addAttribute("isClick", true)
        properties.addAttribute("planPrice", planPrice)
        properties.addAttribute("planName", planTitle)
        trackEvent(this, "BuyDietPlan", properties)

        setUserAttribute(this, "subscription_diet_plan_name", planTitle!!)
        setUserAttribute(this, "subscription_diet_plan_price", planPrice!!)

        ApiCall.instance.buyPlanDetail(
            profileId,
            planId,
            planTitle,
            planPrice,
            this
        )
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "BuyPlan") {
            val response = data as Response<BuyPlanResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {
                    val properties = Properties()
                    properties.addAttribute("buyNow", true)
                    properties.addAttribute("status", true)
                    trackEvent(this, "BuyDietPlan", properties)

                    healthPlanRequestId = response.body()!!.data.id
                    paymentStatus = response.body()!!.data.health_plan_payment_status
                    orderId = response.body()!!.data.payment_order_id

                    callTrackScreenAPi(
                        preferences!!.userId!!,
                        preferences!!.getprofileid()!!,
                        "buyHealthPlan",
                        response.body()!!.data.id,
                        response.body()!!.data.health_plan_name
                    )

                    startPayment()
                }
            }
        }
        if (type == "healthPlanDetail") {
            val response = data as Response<HealthPlanDetailResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {

                    binding.rlMain.visibility = View.VISIBLE

                    if (response.body()!!.healthplan_details != null) {

                        if (reviewList != null && reviewList!!.size > 0) {
                            reviewList!!.clear()
                        }

                        if (benefitList != null && benefitList!!.size > 0) {
                            benefitList!!.clear()
                        }

                        binding.tvPlanName.text = response.body()!!.healthplan_details.plan_title
                        binding.tvPlanDetail.text =
                            response.body()!!.healthplan_details.plan_details
                        binding.tvPlanPrice.text = "₹" + response.body()!!.healthplan_details.price

                        planId = response.body()!!.healthplan_details.id.toString()
                        planTitle = response.body()!!.healthplan_details.plan_title
                        planDetail = response.body()!!.healthplan_details.plan_details
                        planPrice = response.body()!!.healthplan_details.price

                        if (response.body()!!.healthplan_details.plan_title == "Doctor consultation" || response.body()!!.healthplan_details.plan_title == "Diet planning" || response.body()!!.healthplan_details.plan_title == "Diabetes Educator Plan") {
                            binding.tvDuration.visibility = View.GONE
                            binding.tvPlanValid.visibility = View.GONE

                        } else {
                            binding.tvDuration.visibility = View.GONE
                            binding.tvPlanValid.visibility = View.GONE
                            binding.tvPlanValid.text =
                                "" + (response.body()!!.healthplan_details.plan_duration / 30) + " Month"
                            binding.tvDuration.text =
                                "" + (response.body()!!.healthplan_details.plan_duration / 30) + " Month"
                        }

                        if (response.body()!!.healthplan_details.review != null && response.body()!!.healthplan_details.review.size > 0) {
                            reviewList = response.body()!!.healthplan_details.review
                            mReviewAdapter!!.setData(reviewList!!)
                        }
                        if (response.body()!!.healthplan_details.benefits != null && response.body()!!.healthplan_details.benefits.size > 0) {
                            benefitList = response.body()!!.healthplan_details.benefits
                            mBenefitAdapter!!.setData(benefitList!!)
                        }
                        if (response.body()!!.healthplan_details.careteam != null && response.body()!!.healthplan_details.careteam.size > 0) {
                            careTeamList = response.body()!!.healthplan_details.careteam
                            mCareTeamAdapter!!.setData(careTeamList!!)
                        }

                        if (response.body()!!.healthplan_details.consultation != null && response.body()!!.healthplan_details.consultation.size > 0) {
                            consultationList = response.body()!!.healthplan_details.consultation
                            mConsultationAdapter!!.setData(consultationList!!)
                        }

                    }
                }
            }
        }
        if (type == "paymentConfirm") {
            val response = data as Response<CommonResponse?>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {
                    val properties = Properties()
                    properties.addAttribute("buyNow", true)
                    properties.addAttribute("paymentConfirm", "success")
                    trackEvent(this, "BuyDietPlan", properties)
                    callTrackScreenAPi(
                        preferences!!.userId!!,
                        preferences!!.getprofileid()!!,
                        "HealthPlanSuccess",
                        "0",
                        "0"
                    )

                    var message = "Congratulations! you have successfully bought the $planName"

                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                        .setMessage(message)
                    builder.setCancelable(false)
                    builder.setPositiveButton("Ok") { dialog, id ->
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                        dialog.dismiss()
                    }
                    val alert11 = builder.create()
                    alert11.setOnShowListener {
                        alert11.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(ContextCompat.getColor(this, R.color.black))

                    }
                    alert11.show()
                } else {
                    val properties = Properties()
                    properties.addAttribute("buyNow", true)
                    properties.addAttribute("paymentConfirm", "fail")
                    if (response.body() != null && response.body()!!.errorMsg != null) {
                        properties.addAttribute("reason", response.body()!!.errorMsg)
                        Toast.makeText(this, response.body()!!.errorMsg, Toast.LENGTH_SHORT).show()
                    }
                    trackEvent(this, "BuyHealthPlan", properties)
                }
            }
        }

        if (type == "paymentConfirmFail") {
            val response = data as Response<CommonResponse?>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {
                    val properties = Properties()
                    properties.addAttribute("buyNow", true)
                    properties.addAttribute("paymentConfirm", "fail")
                    if (response.body() != null && response.body()!!.errorMsg != null) {
                        properties.addAttribute("reason", response.body()!!.errorMsg)
                        Toast.makeText(this, response.body()!!.errorMsg, Toast.LENGTH_SHORT).show()
                    }
                    trackEvent(this, "BuyDietPlan", properties)
                    callTrackScreenAPi(
                        preferences!!.userId!!,
                        preferences!!.getprofileid()!!,
                        "HealthPlanSuccess",
                        "0",
                        "0"
                    )
                } else {
                    val properties = Properties()
                    properties.addAttribute("buyNow", true)
                    properties.addAttribute("paymentConfirm", "fail")
                    if (response.body() != null && response.body()!!.errorMsg != null) {
                        properties.addAttribute("reason", response.body()!!.errorMsg)
                        Toast.makeText(this, response.body()!!.errorMsg, Toast.LENGTH_SHORT).show()
                    }
                    trackEvent(this, "BuyHealthPlan", properties)
                }
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }

    private fun callTrackScreenAPi(
        customerId: String,
        profileId: String,
        eventName: String,
        actionId: String,
        actionHeading: String,
    ) {
        if (CheckConnection.isConnection(this)) {
            ApiCall.instance
                .trackScreen(customerId, profileId, eventName, actionId, actionHeading, this)
        } else {
            Toast.makeText(this, "please check your internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    fun startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        val activity: Activity = this
        val co = Checkout()
        try {
            val options = JSONObject()
            options.put("name", planTitle)
            options.put("description", planDetail)
            options.put("order_id", orderId)
            options.put("currency", "INR")
            options.put("amount", planPrice!!.toInt() * 100)
            options.put(
                "prefill.email",
                if (preferences!!.emailId != null) preferences!!.emailId else ""
            )
            options.put("prefill.contact", preferences!!.userMobileNumber)
            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_SHORT)
                .show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?, paymentData: PaymentData?) {
        try {
            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            Log.e("", "Exception in onPaymentSuccess", e)
        }
        FunctionHelper.enableUserIntraction()

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Success")
        mFirebaseAnalytics!!.logEvent("BuyDietPlan", bundle)
        mFirebaseAnalytics!!.setDefaultEventParameters(bundle)

        mFirebaseAnalytics!!.setUserProperty("BuyHealthPlan", "Success")

        setUserAttribute(this, "subscription_diet_plan_status", "Success")

        if (CheckConnection.isConnection(this)) {
            FunctionHelper.disable_user_Intration(
                this,
                resources.getString(R.string.loading)
            )
            ApiCall.instance.paymentConfirm(
                preferences!!.getprofileid(),
                healthPlanRequestId,
                "Success",
                paymentData!!.data.toString(),
                this
            )
        } else {
            Toast.makeText(
                this,
                "please check your internet connection",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onPaymentError(code: Int, response: String?, paymentData: PaymentData?) {
        try {
            Toast.makeText(this, "Payment Cancel", Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            Log.e("", "Exception in onPaymentFailed", e)
        }
        FunctionHelper.enableUserIntraction()

        setUserAttribute(this, "subscription_diet_plan_status", "Fail")

        if (CheckConnection.isConnection(this)) {

            FunctionHelper.disable_user_Intration(this, resources.getString(R.string.loading))

            ApiCall.instance.paymentConfirmFail(
                preferences!!.getprofileid(),
                healthPlanRequestId,
                "Failed",
                paymentData!!.data.toString(),
                this
            )
        } else {
            Toast.makeText(
                this,
                "please check your internet connection",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}