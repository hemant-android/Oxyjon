package app.oxyjon.ui.kotlin.activity

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import app.oxyjon.R
import app.oxyjon.bean.BuyPlanResponse
import app.oxyjon.bean.TestBookDetailResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityTestBookScreenBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.retrofit.response.CommonResponse
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.ui.kotlin.activity.adapter.TestBookBenefitAdapter
import app.oxyjon.ui.kotlin.activity.adapter.TestNameAdapter
import app.oxyjon.utils.CheckConnection.isConnection
import app.oxyjon.utils.FunctionHelper
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.firebase.analytics.FirebaseAnalytics
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper.setUserAttribute
import com.moengage.core.analytics.MoEAnalyticsHelper.trackEvent
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject
import retrofit2.Response

class TestBookActivity : BaseActivity(), IApiCallback, PaymentResultWithDataListener {

    lateinit var binding: ActivityTestBookScreenBinding
    var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var planId = ""
    private var planPrice: String? = ""
    private var planDetail: String? = ""
    private var planTitle: String? = ""
    private var healthPlanRequestId = ""
    private var orderId = ""
    private var paymentStatus = ""

    var preferences: AppSharedPreferences? = null

    private val mBenefitAdapter: TestBookBenefitAdapter by lazy { TestBookBenefitAdapter(this) }
    private val mTestNameAdapter: TestNameAdapter by lazy { TestNameAdapter(this) }
    private var benefitList: ArrayList<TestBookDetailResponse.BloodtestDetails.Benefit>? = arrayListOf()
    private var testsList: ArrayList<TestBookDetailResponse.BloodtestDetails.Test>? = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTestBookScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppSharedPreferences.getInstance(this)

        /*
         To ensure faster loading of the Checkout form,
          call this method as early as possible in your checkout flow.
         */
        Checkout.preload(applicationContext)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val bundle = intent.extras
        if (bundle != null) {
            planId = bundle.getInt("planId").toString()
        }

        binding.imgBack.setOnClickListener {
            finish()
        }

        if (isConnection(this)) {
            FunctionHelper.disable_user_Intration(this,resources.getString(R.string.loading))
            ApiCall.instance.getTestBookPlanDetail(preferences!!.getprofileid(), planId, this)
        } else {
            Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show()
        }

        binding.rvTestName.apply {
            layoutManager = FlexboxLayoutManager(this@TestBookActivity).apply {
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.FLEX_START
            }
            binding.rvTestName.adapter = mTestNameAdapter
        }


        binding.rvBenefits.adapter = mBenefitAdapter


        binding.tvMakePayment.setOnClickListener {

            if (!TextUtils.isEmpty(planPrice)) {
                buyPlan(preferences!!.getprofileid(), planId, planTitle, planPrice)
            }
        }
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
        trackEvent(this, "BuyTestBookPlan", properties)

        setUserAttribute(this, "subscription_test_book_name", planTitle!!)
        setUserAttribute(this, "subscription_test_book_price", planPrice!!)

        ApiCall.instance.buyPlanDetail(profileId,
            planId,
            planTitle,
            planPrice,
            this)
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
                    trackEvent(this, "BuyTestBookPlan", properties)

                    healthPlanRequestId = response.body()!!.data.id
                    paymentStatus = response.body()!!.data.health_plan_payment_status
                    orderId = response.body()!!.data.payment_order_id

                    callTrackScreenAPi(preferences!!.userId!!,
                        preferences!!.getprofileid()!!,
                        "buyHealthPlan",
                        response.body()!!.data.id,
                        response.body()!!.data.health_plan_name)

                    startPayment()
                }
            }
        }
        if (type == "testBookDetail") {
            val response = data as Response<TestBookDetailResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {

                    binding.rlMain.visibility = View.VISIBLE

                    if (response.body()!!.bloodtest_details != null) {

                        if (benefitList != null && benefitList!!.size > 0) {
                            benefitList!!.clear()
                        }

                        if (testsList != null && testsList!!.size > 0) {
                            testsList!!.clear()
                        }

                        binding.tvPlanName.text = response.body()!!.bloodtest_details.plan_title
                        binding.tvPlanDetail.text =
                            response.body()!!.bloodtest_details.plan_details

                        planId = response.body()!!.bloodtest_details.id.toString()
                        planTitle = response.body()!!.bloodtest_details.plan_title
                        planDetail = response.body()!!.bloodtest_details.plan_details
                        planPrice = response.body()!!.bloodtest_details.price

                        binding.tvPlanPrice.text = "₹" + response.body()!!.bloodtest_details.price

                        if (response.body()!!.bloodtest_details.benefits != null && response.body()!!.bloodtest_details.benefits.size > 0) {
                            benefitList = response.body()!!.bloodtest_details.benefits
                            mBenefitAdapter!!.setData(benefitList!!)
                        }

                        if (response.body()!!.bloodtest_details.tests != null && response.body()!!.bloodtest_details.tests.size > 0) {
                            testsList = response.body()!!.bloodtest_details.tests
                            mTestNameAdapter!!.setData(testsList!!)
                        }

                        if (response.body()!!.bloodtest_details.banner != null && !TextUtils.isEmpty(
                                response.body()!!.bloodtest_details.banner)
                        ) {
                            binding.imgMedicine.visibility = View.VISIBLE
                            try {
                                Glide.with(this@TestBookActivity).load(response.body()!!.bloodtest_details.banner).placeholder(R.drawable.progress_animation)
                                    .into(binding.imgMedicine!!)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            binding.imgMedicine.visibility = View.GONE
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
                    trackEvent(this, "BuyTestBookPlan", properties)
                    callTrackScreenAPi(preferences!!.userId!!,
                        preferences!!.getprofileid()!!,
                        "HealthPlanSuccess",
                        "0",
                        "0")
                } else {
                    val properties = Properties()
                    properties.addAttribute("buyNow", true)
                    properties.addAttribute("paymentConfirm", "fail")
                    if (response.body() != null && response.body()!!.errorMsg != null) {
                        properties.addAttribute("reason", response.body()!!.errorMsg)
                        Toast.makeText(this, response.body()!!.errorMsg, Toast.LENGTH_SHORT).show()
                    }
                    trackEvent(this, "BuyTestBookPlan", properties)
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
                    trackEvent(this, "BuyTestBookPlan", properties)
                    callTrackScreenAPi(preferences!!.userId!!,
                        preferences!!.getprofileid()!!,
                        "HealthPlanSuccess",
                        "0",
                        "0")
                } else {
                    val properties = Properties()
                    properties.addAttribute("buyNow", true)
                    properties.addAttribute("paymentConfirm", "fail")
                    if (response.body() != null && response.body()!!.errorMsg != null) {
                        properties.addAttribute("reason", response.body()!!.errorMsg)
                        Toast.makeText(this, response.body()!!.errorMsg, Toast.LENGTH_SHORT).show()
                    }
                    trackEvent(this, "BuyTestBookPlan", properties)
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
            options.put("prefill.email",
                if (preferences!!.emailId != null) preferences!!.emailId else "")
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
        mFirebaseAnalytics!!.logEvent("BuyTestBookPlan", bundle)
        mFirebaseAnalytics!!.setDefaultEventParameters(bundle)

        mFirebaseAnalytics!!.setUserProperty("BuyTestBookPlan", "Success")

        setUserAttribute(this, "subscription_buy_buy_test_book_status", "Success")

        if (isConnection(this)) {
            FunctionHelper.disable_user_Intration(this,
                resources.getString(R.string.loading))
            ApiCall.instance.paymentConfirm(preferences!!.getprofileid(),
                healthPlanRequestId,
                "Success",
                paymentData!!.data.toString(),
                this)
        } else {
            Toast.makeText(this,
                "please check your internet connection",
                Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPaymentError(code: Int, response: String?, paymentData: PaymentData?) {
        try {
            Toast.makeText(this, "Payment Cancel", Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            Log.e("", "Exception in onPaymentFailed", e)
        }

        FunctionHelper.enableUserIntraction()

        setUserAttribute(this, "subscription_buy_test_book_status", "Fail")

        if (isConnection(this)) {
            FunctionHelper.disable_user_Intration(this, resources.getString(R.string.loading))
            ApiCall.instance.paymentConfirmFail(preferences!!.getprofileid(),
                healthPlanRequestId,
                "Failed",
                paymentData!!.data.toString(),
                this)
        } else {
            Toast.makeText(this,
                "please check your internet connection",
                Toast.LENGTH_SHORT).show()
        }
    }
}