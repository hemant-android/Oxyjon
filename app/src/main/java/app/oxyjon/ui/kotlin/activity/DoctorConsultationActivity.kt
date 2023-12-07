package app.oxyjon.ui.kotlin.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.adapter.ReviewDoctorAdapter
import app.oxyjon.bean.BuyPlanResponse
import app.oxyjon.bean.DoctorConsultationResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityDoctorConsultScreenBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.retrofit.response.CommonResponse
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.utils.CheckConnection.isConnection
import app.oxyjon.utils.FunctionHelper
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper
import com.moengage.core.analytics.MoEAnalyticsHelper.setUserAttribute
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject
import retrofit2.Response

class DoctorConsultationActivity : BaseActivity(), IApiCallback, PaymentResultWithDataListener {
    lateinit var binding: ActivityDoctorConsultScreenBinding

    var preferences: AppSharedPreferences? = null
    var mFirebaseAnalytics: FirebaseAnalytics? = null

    var mReviewAdapter: ReviewDoctorAdapter? = null
    private var reviewList: ArrayList<DoctorConsultationResponse.Data.Review>? = arrayListOf()

    private var planPrice: String? = ""
    private var planDetail: String? = ""
    private var planTitle: String? = ""
    private var planId: String? = ""

    private var healthPlanRequestId = ""
    private var orderId = ""
    private var paymentStatus = ""
    private var planName: String? = ""

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDoctorConsultScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppSharedPreferences.getInstance(this)

        MainApplication.currentActivity = this

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)


        /*
         To ensure faster loading of the Checkout form,
          call this method as early as possible in your checkout flow.
         */
        Checkout.preload(applicationContext)

        val bundle = intent.extras
        if (bundle != null) {
            planName = bundle.getString("planName") ?: ""
        }

        binding.imgBack.setOnClickListener {
            finish()
        }

        if (isConnection(this)) {
            FunctionHelper.disable_user_Intration(this, resources.getString(R.string.loading))
            ApiCall.instance.getDoctorConsultation(preferences!!.getprofileid(), this)
        } else {
            Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show()
        }

        binding.tvMakePayment.setOnClickListener {
            if (!TextUtils.isEmpty(planPrice)) {
                buyPlan(preferences!!.getprofileid(), planId, planTitle, planPrice)
            }
        }

        binding.rlConsulate.setOnClickListener {
            if (!TextUtils.isEmpty(planPrice)) {
                buyPlan(preferences!!.getprofileid(), planId, planTitle, planPrice)
            }
        }

        mReviewAdapter = ReviewDoctorAdapter(this)
        binding.rvReview.adapter = mReviewAdapter
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
        MoEAnalyticsHelper.trackEvent(this, "BuyDoctorConsultationPlan", properties)

        setUserAttribute(
            this,
            "subscription_doctor_consultation_plan_name",
            planTitle!!
        )
        setUserAttribute(
            this,
            "subscription_doctor_consultation_plan_price",
            planPrice!!
        )

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
                    MoEAnalyticsHelper.trackEvent(this, "BuyDoctorConsultationPlan", properties)

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

        if (type == "doctorconsultation") {
            val response = data as Response<DoctorConsultationResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {

                    if (reviewList != null && reviewList!!.size > 0) {
                        reviewList!!.clear()
                    }
                    if (response.body()!!.data != null) {

                        binding.rlMain.visibility = View.VISIBLE

                        planId = response.body()!!.data.id
                        planTitle = response.body()!!.data.plan_title
                        planDetail = response.body()!!.data.doctor_name
                        planPrice = response.body()!!.data.price_online_consultation

                        if (response.body()!!.data.about_doctor != null && !TextUtils.isEmpty(
                                response.body()!!.data.about_doctor
                            )
                        ) {

                            binding.tvDrName.text = response.body()!!.data.doctor_name
                            binding.tvDrSpecialist.text = response.body()!!.data.education
                            binding.ratingBar.rating = response.body()!!.data.ratings.toFloat()

                            binding.tvAboutDoctor.text =
                                Html.fromHtml(response.body()!!.data.about_doctor)
                            binding.tvPlanPrice.text =
                                "₹" + response.body()!!.data.price_online_consultation
                        }

                        if (response.body()!!.data.consultation_type != null && !TextUtils.isEmpty(
                                response.body()!!.data.consultation_type
                            )
                        ) {
                            binding.rlConsulate.visibility = View.VISIBLE
                            if (response.body()!!.data.consultation_type == "offline") {
                                binding.imgVideoAudioConsult.setImageResource(R.drawable.ic_clinic)
                                binding.tvMeetTime.text = "In Clinic"
                            } else {
                                binding.imgVideoAudioConsult.setImageResource(R.drawable.ic_video_consult)
                                binding.tvMeetTime.text = "Video"
                            }
                        } else {
                            binding.rlConsulate.visibility = View.GONE
                        }

                        if (response.body()!!.data.profile_image != null && !TextUtils.isEmpty(
                                response.body()!!.data.profile_image
                            )
                        ) {
                            Glide.with(this@DoctorConsultationActivity)
                                .load(response.body()!!.data.profile_image)
                                .placeholder(R.drawable.progress_animation)
                                .into(binding.imgDoctor)
                        }

                        if (response.body()!!.data.review != null && response.body()!!.data.review.size > 0) {
                            reviewList = response.body()!!.data.review
                            mReviewAdapter!!.setData(reviewList)
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
                    MoEAnalyticsHelper.trackEvent(this, "BuyDoctorConsultationPlan", properties)
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
                    MoEAnalyticsHelper.trackEvent(this, "BuyDoctorConsultationPlan", properties)
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
                    MoEAnalyticsHelper.trackEvent(this, "BuyDoctorConsultationPlan", properties)
                    if (response.body() != null && response.body()!!.errorMsg != null) {
                        properties.addAttribute("reason", response.body()!!.errorMsg)
                        Toast.makeText(this, response.body()!!.errorMsg, Toast.LENGTH_SHORT).show()
                    }
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
                    MoEAnalyticsHelper.trackEvent(this, "BuyDoctorConsultationPlan", properties)
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
        if (isConnection(this)) {
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

    @SuppressLint("Range")
    override fun onPaymentSuccess(razorpayPaymentID: String?, paymentData: PaymentData?) {
        try {
            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            Log.e("", "Exception in onPaymentSuccess", e)
        }

        FunctionHelper.enableUserIntraction()

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Success")
        mFirebaseAnalytics!!.logEvent("BuyDoctorConsultationPlan", bundle)
        mFirebaseAnalytics!!.setDefaultEventParameters(bundle)

        mFirebaseAnalytics!!.setUserProperty("BuyDoctorConsultationPlan", "Success")

        setUserAttribute(this, "subscription_buy_doctor_consultation_plan", "Success")

        if (isConnection(this)) {
            FunctionHelper.disable_user_Intration(this, resources.getString(R.string.loading))
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

        setUserAttribute(this, "subscription_buy_doctor_consultation_plan", "Fail")
        if (isConnection(this)) {
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