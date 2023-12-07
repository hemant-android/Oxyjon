package app.oxyjon.ui.activity

//import android.icu.text.SimpleDateFormat
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresApi
import app.oxyjon.BuildConfig
import app.oxyjon.R
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.retrofit.response.SendOtpResponse
import app.oxyjon.ui.kotlin.activity.DashboardActivity
import app.oxyjon.utils.CheckConnection
import app.oxyjon.utils.FunctionHelper
import app.oxyjon.utils.SmsBroadcastReceiver
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.firebase.analytics.FirebaseAnalytics
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper.trackEvent
import com.mukesh.OtpView
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * @OtpVerifyActivity
 */
class OtpVerifyActivity : BaseActivity(), IApiCallback {
    var preferences: AppSharedPreferences? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.textViewVerify)
    var textViewVerify: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.otpView)
    var otp_View: OtpView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tvResendOtp)
    var tvResendOtp: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tvResendOpen)
    var tvResendOpen: TextView? = null
    var smsBroadcastReceiver: SmsBroadcastReceiver? = null
    var logger: AppEventsLogger? = null
    var mFirebaseAnalytics: FirebaseAnalytics? = null
    var handler: Handler? = Handler()
    var mobileNumber: String? = ""
    var type: String? = ""
    var id: String? = ""
    private var createWishListResultLauncher: ActivityResultLauncher<Intent?>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verify)
        ButterKnife.bind(this)
        preferences = AppSharedPreferences.Companion.getInstance(this)
        logger = AppEventsLogger.newLogger(this)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        startSmsUserConsent()
        val bundle = intent.extras
        if (bundle != null) {
            mobileNumber = bundle.getString("mobileNumber")
            type = bundle.getString("type")
            id = bundle.getString("id") ?: ""
        }
        handler!!.postDelayed({ // Actions to do after 60 seconds
            tvResendOtp!!.visibility = View.VISIBLE
        }, 60000)
        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvResendOpen!!.visibility = View.VISIBLE
                tvResendOpen!!.text = "Resend OTP in " + millisUntilFinished / 1000 + " Seconds"
            }

            override fun onFinish() {
                tvResendOpen!!.visibility = View.GONE
            }
        }.start()
        createWishListResultLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                if (data != null) {
                    // Apply your logic here
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    getOtpFromMessage(message)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        registerBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsBroadcastReceiver)
        if (handler != null) {
            handler!!.removeCallbacksAndMessages(null)
        }
    }

    /**
     * Resend otp
     */
    @OnClick(R.id.tvResendOtp)
    fun reSendOtp() {
        val androidVersion = Build.VERSION.RELEASE
        val androidModel = Build.MODEL
        val androidBrand = Build.BRAND
        if (CheckConnection.isConnection(this)) {
            FunctionHelper.disable_user_Intration(this, getString(R.string.loading))
            ApiCall.instance.userLoginWithMobile(
                mobileNumber,
                preferences!!.deviceToken,
                androidVersion,
                BuildConfig.VERSION_NAME,
                "$androidBrand, $androidModel",
                this
            )
        } else {
            Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (handler != null) {
            handler!!.removeCallbacksAndMessages(null)
        }
    }

    /**
     * Verify otp after that navigate to relevant screen.
     */
    @OnClick(R.id.textViewVerify)
    fun onOtpVerify() {
        if (isValid) {
            if (CheckConnection.isConnection(this)) {
                FunctionHelper.disable_user_Intration(this, getString(R.string.loading))
                FunctionHelper.hideKeyBoard(this, otp_View)
                ApiCall.instance.verifyOtp(
                    otp_View!!.text.toString(),
                    preferences!!.userId,
                    "phone", this
                )
            } else {
                Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun callTrackScreenAPi(
        customerId: String?,
        profileId: String?,
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

    /**
     * Navigate corresponding screen
     * if type equal login that manage resend option after 60 second.
     * if type equal verifyOtp that manage blow conditions
     * if user is screen_quest1 equal true then navigate onBoard first screen @QuestionFirstActivity
     * if user is screen_quest2 equal true then navigate segregation screen @BenefitActivity
     * if user is userOnBoard equal 2 then navigate Dashboard screen @DashboardActivity
     */
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "login") {
            val response = data as Response<SendOtpResponse>
            if (response.body()!!.errorCode == "0") {
                callTrackScreenAPi(
                    response.body()!!.data!!.customerId,
                    response.body()!!.data!!.profileId,
                    "userResendOtp",
                    "0",
                    "0"
                )
                tvResendOtp!!.visibility = View.GONE
                object : CountDownTimer(60000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        tvResendOpen!!.visibility = View.VISIBLE
                        tvResendOpen!!.text =
                            "Resend OTP in " + millisUntilFinished / 1000 + " Seconds"
                    }

                    override fun onFinish() {
                        tvResendOpen!!.visibility = View.GONE
                    }
                }.start()
                handler!!.postDelayed({ // Actions to do after 10 seconds
                    tvResendOtp!!.visibility = View.VISIBLE
                }, (1000 * 60).toLong())
            } else {
                Toast.makeText(this, response.body()!!.errorMsg, Toast.LENGTH_SHORT).show()
            }
        } else if (type == "verifyOtp") {
            val response = data as Response<SendOtpResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {

                    setUserDataWithMobile(response.body()!!.data)

                    logger!!.logEvent("otpVerify")
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "otpVerify")
                    mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
                    val properties = Properties()
                    properties.addAttribute("isOtpVerify", true)
                    trackEvent(this, "OtpVerify", properties)


                    if (AppSharedPreferences.getInstance(this)!!.userOnBoard.equals(
                            "2",
                            ignoreCase = true
                        )
                    ) {
                        val info: AppSharedPreferences = AppSharedPreferences.getInstance(this)!!
                        info.userOnBoard = "2"
                        callIntent(Intent(this, DashboardActivity::class.java))
                    } else {
                        if (this.type.equals("onboardingFirst", ignoreCase = true)) {
                            callIntent(Intent(this, QuestionFirstActivity::class.java))
                        } else if (this.type.equals("onboardingSecond", ignoreCase = true)) {
                            callIntent(Intent(this, BenefitActivity::class.java))
                        } else if (!response.body()!!.data!!.screen_quest1!! && !response.body()!!.data!!.screen_quest2!!) {
                            if (response.body()!!.data?.profile_type == "B2B") {
                                val info: AppSharedPreferences =
                                    AppSharedPreferences.getInstance(this)!!
                                info.userOnBoard = "2"
                                callIntent(Intent(this, DashboardActivity::class.java))
                            } else {
                                val info: AppSharedPreferences =
                                    AppSharedPreferences.getInstance(this)!!
                                info.userOnBoard = "1"
                                callIntent(Intent(this, BenefitActivity::class.java))
                            }

                        } else if (response.body()!!.data!!.screen_quest1!!) {
                            callIntent(Intent(this, QuestionFirstActivity::class.java))
                        } else if (response.body()!!.data!!.screen_quest2!!) {
                            if (response.body()!!.data?.profile_type == "B2B") {
                                val info: AppSharedPreferences =
                                    AppSharedPreferences.getInstance(this)!!
                                info.userOnBoard = "3"
                                callIntent(Intent(this, QuestionSecondActivity::class.java))
                            } else {
                                val info: AppSharedPreferences =
                                    AppSharedPreferences.getInstance(this)!!
                                info.userOnBoard = "1"
                                callIntent(Intent(this, BenefitActivity::class.java))
                            }
                        } else {
                            val info: AppSharedPreferences =
                                AppSharedPreferences.getInstance(this)!!
                            info.userOnBoard = "2"
                            callIntent(Intent(this, DashboardActivity::class.java))
                        }

                    }

                } else {
                    val properties = Properties()
                    properties.addAttribute("isOtpVerify", false)
                    trackEvent(this, "OtpVerify", properties)
                    Toast.makeText(this, response.body()!!.errorMsg, Toast.LENGTH_SHORT).show()
                }
            } else {
                FunctionHelper.showSnackMessage(
                    findViewById(android.R.id.content),
                    getString(R.string.some_error_occurred)
                )
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
        val error = data as String
        if (error.contains("No address associated with hostname")) {
            FunctionHelper.showSnackMessage(
                findViewById(android.R.id.content),
                "please check your internet connection"
            )
        } else if (error.contains("java.net.SocketTimeoutException")) {
            FunctionHelper.showSnackMessage(
                findViewById(android.R.id.content),
                "please check your internet connection"
            )
        } else {
            FunctionHelper.showSnackMessage(
                findViewById(android.R.id.content),
                getString(R.string.some_error_occurred)
            )
        }
    }

    private val isValid: Boolean
        private get() {
            if (TextUtils.isEmpty(otp_View!!.text.toString())) {
                otp_View!!.requestFocus()
                Toast.makeText(this, "Please enter valid otp", Toast.LENGTH_SHORT).show()
                return false
            } else if (otp_View!!.text.toString().length < 3) {
                otp_View!!.requestFocus()
                Toast.makeText(this, "Please enter valid otp", Toast.LENGTH_SHORT).show()
                return false
            }
            return true
        }

    @RequiresApi(Build.VERSION_CODES.N)
    fun setUserDataWithMobile(data: SendOtpResponse.MobileData?) {
        val info: AppSharedPreferences = AppSharedPreferences.getInstance(this)!!
        info.userId = data!!.customerId
        info.userMobileNumber = data.mobileNo
        info.fullName = data.fullName
        info.image = data.profileImage
        info.currentDate = currentDate
        info.paymentStatus = data.payment_status
        info.token = data.token

        if (null != data.profileId && !TextUtils.isEmpty(data.profileId)) {
            info.setprofileid(data.profileId)
        }
        info.userLoggedIn = "1"
        callTrackScreenAPi(data.customerId, data.profileId, "userVerifyOtp", "0", "0")
    }

    private fun callIntent(intent: Intent) {
        intent.putExtra("type", type)
        intent.putExtra("id", id)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun startSmsUserConsent() {
        val client = SmsRetriever.getClient(this)
        //We can add sender phone number or leave it blank
        // I'm adding null here
        client.startSmsUserConsent(null).addOnSuccessListener {
            //                Toast.makeText(getApplicationContext(), "On Success", Toast.LENGTH_LONG).show();
        }.addOnFailureListener {
//            Toast.makeText(applicationContext,"On OnFailure",Toast.LENGTH_LONG).show()
        }
    }

    private fun registerBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver!!.smsBroadcastReceiverListener =
            object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
                override fun onSuccess(intent: Intent?) {
                    createWishListResultLauncher!!.launch(intent)
                }

                override fun onFailure() {}
            }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsBroadcastReceiver, intentFilter)
    }

    private fun getOtpFromMessage(message: String?) {
        // This will match any 6 digit number in the message
        val pattern = Pattern.compile("(|^)\\d{4}")
        val matcher = pattern.matcher(message)
        if (matcher.find()) {
            otp_View!!.setText(matcher.group(0))
            if (isValid) {
                if (CheckConnection.isConnection(this)) {
                    FunctionHelper.disable_user_Intration(this, getString(R.string.loading))
                    FunctionHelper.hideKeyBoard(this, otp_View)
                    ApiCall.instance.verifyOtp(
                        otp_View!!.text.toString(),
                        preferences!!.userId,
                        "phone", this
                    )
                } else {
                    Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    companion object {
        private const val REQ_USER_CONSENT = 200
        val currentDate: String
            @RequiresApi(Build.VERSION_CODES.N)
            get() {
                val yearFormat = SimpleDateFormat("yyyy-MM-dd")
                val d = Date(System.currentTimeMillis())
                return yearFormat.format(d)
            }
    }
}