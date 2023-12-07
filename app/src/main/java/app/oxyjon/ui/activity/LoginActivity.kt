package app.oxyjon.ui.activity

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import app.oxyjon.BuildConfig
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.adapter.ViewAdapter
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.retrofit.response.SendOtpResponse
import app.oxyjon.retrofit.response.SendOtpResponse.MobileData
import app.oxyjon.ui.kotlin.activity.DashboardActivity
import app.oxyjon.ui.kotlin.activity.PlanDetailActivity
import app.oxyjon.utils.CheckConnection.isConnection
import app.oxyjon.utils.FunctionHelper.disable_user_Intration
import app.oxyjon.utils.FunctionHelper.enableUserIntraction
import app.oxyjon.utils.FunctionHelper.showSnackMessage
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnTouch
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper.setAlias
import com.moengage.core.analytics.MoEAnalyticsHelper.setLocation
import com.moengage.core.analytics.MoEAnalyticsHelper.setMobileNumber
import com.moengage.core.analytics.MoEAnalyticsHelper.setUniqueId
import com.moengage.core.analytics.MoEAnalyticsHelper.setUserAttribute
import com.moengage.core.analytics.MoEAnalyticsHelper.trackEvent
import com.moengage.firebase.MoEFireBaseHelper
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import com.truecaller.android.sdk.*
import ly.count.android.sdk.Countly
import retrofit2.Response
import java.io.IOException
import java.util.*

/**
 * @LoginActivity
 */
class LoginActivity : BaseActivity(), IApiCallback {
    var preferences: AppSharedPreferences? = null

    @JvmField
    @BindView(R.id.user_name_et)
    var userNameEt: EditText? = null

    @JvmField
    @BindView(R.id.check_box_iv)
    var remember: ImageView? = null

    @JvmField
    @BindView(R.id.llTruecaller)
    var llTrueCaller: LinearLayout? = null

    @JvmField
    @BindView(R.id.tvTermAndCondition)
    var tvTermAndCondition: TextView? = null

    @JvmField
    @BindView(R.id.view_pager)
    var viewPager: ViewPager? = null

    @JvmField
    @BindView(R.id.dots_indicator)
    var dotIndicator: DotsIndicator? = null
    var flag = 0
    var UserAddress: String? = null
    var manager: LocationManager? = null
    var logger: AppEventsLogger? = null
    var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var mylocation: Location? = null
    private var type: String? = ""
    private var id: String? = ""
    var adapter: ViewAdapter? = null
    var timer: Timer? = null
    var page = 0

    var mFusedLocationClient: FusedLocationProviderClient? = null
    private val permissionId = 2
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)
        ButterKnife.bind(this)
        Countly.onCreate(this)
        MainApplication.currentActivity = this
        val bundle = intent.extras
        if (bundle != null) {
            type = bundle.getString("type")
            id = bundle.getString("id") ?: ""
        }
        logger = AppEventsLogger.newLogger(this)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)



        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        manager = getSystemService(LOCATION_SERVICE) as LocationManager
        preferences = AppSharedPreferences.getInstance(this)
        userNameEt!!.requestFocus()
        if (AppSharedPreferences.getInstance(this)!!.rememberMe.equals("1")) {
            userNameEt!!.setText(AppSharedPreferences.getInstance(this)!!.userMobileNumber)
            remember!!.isSelected = true
            flag = 1
        } else {
            remember!!.isSelected = false
            userNameEt!!.setText("")
            flag = 0
        }
        val text = "By proceeding you agree to the Terms & Conditions and Privacy Policy."
        val spannableString = SpannableString(text)
        val foregroundColorSpanRed = ForegroundColorSpan(Color.RED)
        spannableString.setSpan(foregroundColorSpanRed, 31, 49, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(foregroundColorSpanRed, 54, 68, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val clickableTermsANdCondition: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                val intent = Intent(this@LoginActivity, WebViewActivity::class.java)
                intent.putExtra("navType", "termsAndCondition")
                intent.putExtra("docUrl", "https://oxyjon.com/terms-and-conditions")
                intent.putExtra("docName", "Terms and conditions")
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        val clickablePrivacyPolicy: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                val intent = Intent(this@LoginActivity, WebViewActivity::class.java)
                intent.putExtra("navType", "privacyPolicy")
                intent.putExtra("docUrl", "https://oxyjon.com/privacy-policy")
                intent.putExtra("docName", "Privacy Policy")
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }

        spannableString.setSpan(clickableTermsANdCondition, 31, 49, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(clickablePrivacyPolicy, 54, 68, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        //truecaller sdk
        @SuppressLint("WrongConstant") val trueScope = TruecallerSdkScope.Builder(this, sdkCallback)
            .consentMode(TruecallerSdkScope.CONSENT_MODE_BOTTOMSHEET)
            .loginTextPrefix(TruecallerSdkScope.LOGIN_TEXT_PREFIX_TO_GET_STARTED)
            .loginTextSuffix(TruecallerSdkScope.LOGIN_TEXT_SUFFIX_PLEASE_VERIFY_MOBILE_NO)
            .ctaTextPrefix(TruecallerSdkScope.CTA_TEXT_PREFIX_USE)
            .buttonShapeOptions(TruecallerSdkScope.BUTTON_SHAPE_ROUNDED)
            .privacyPolicyUrl("<<YOUR_PRIVACY_POLICY_LINK>>")
            .termsOfServiceUrl("<<YOUR_PRIVACY_POLICY_LINK>>")
            .footerType(TruecallerSdkScope.FOOTER_TYPE_NONE)
            .consentTitleOption(TruecallerSdkScope.SDK_CONSENT_TITLE_SIGN_IN)
            .sdkOptions(TruecallerSdkScope.SDK_OPTION_WITHOUT_OTP).build()
        TruecallerSDK.init(trueScope)

        adapter = ViewAdapter(supportFragmentManager)
        viewPager!!.adapter = adapter
        dotIndicator!!.setViewPager(viewPager)
        pageSwitcher(3)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            requestPermissionsAbove12()
        } else {
            requestPermissionsBelow12()
        }
    }

    override fun onDestroy() {
        if (timer != null) {
            timer!!.cancel()
        }
        super.onDestroy()
    }

    private val sdkCallback: ITrueCallback = object : ITrueCallback {
        override fun onSuccessProfileShared(trueProfile: TrueProfile) {
            Log.i("TAG", trueProfile.firstName + " " + trueProfile.lastName)
            launchHome(trueProfile)
        }

        override fun onFailureProfileShared(trueError: TrueError) {
//            Log.i("TAG", trueError.toString());
            Log.e("TrueCaller Fail", trueError.toString())
        }

        override fun onVerificationRequired(trueError: TrueError?) {
            Log.i("TAG", "onVerificationRequired")
        }
    }

    private fun launchHome(trueProfile: TrueProfile) {
        val androidVersion = Build.VERSION.RELEASE
        val androidModel = Build.MODEL
        val androidBrand = Build.BRAND
        val properties = Properties()
        properties.addAttribute("version", Build.VERSION.RELEASE)
        properties.addAttribute("model", Build.MODEL)
        properties.addAttribute("AppVersion", BuildConfig.VERSION_CODE)
        trackEvent(this, "AndroidVersion", properties)
        setUserAttribute(this@LoginActivity, "AppVersion", BuildConfig.VERSION_CODE)
        if (isConnection(this)) {
            disable_user_Intration(this, getString(R.string.loading))
            ApiCall.instance.userLoginWithTrueCaller(
                trueProfile.countryCode,
                trueProfile.firstName,
                trueProfile.gender,
                trueProfile.isAmbassador,
                trueProfile.isBusiness,
                trueProfile.isSimChanged,
                trueProfile.isTrueName,
                trueProfile.lastName,
                trueProfile.payload,
                trueProfile.phoneNumber,
                trueProfile.requestNonce,
                trueProfile.signature,
                trueProfile.signatureAlgorithm,
                trueProfile.userLocale.toString(),
                trueProfile.verificationTimestamp,
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

    override fun onStart() {
        super.onStart()
        Countly.sharedInstance().onStart(this)
        //        MoEInAppHelper.getInstance().showInApp(this);
    }

    public override fun onStop() {
        Countly.sharedInstance().onStop()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this
        if (!TextUtils.isEmpty(AppSharedPreferences.getInstance(this)!!.deviceToken)) {
            AppSharedPreferences.getInstance(this)!!.deviceToken = ""
        }
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String> ->
                if (!task.isSuccessful) {
                    AppSharedPreferences.getInstance(this)!!.deviceToken = ""
                    Log.d(
                        "TAG", "Fetching FCM registration token failed: ", task.exception
                    )
                    return@addOnCompleteListener
                }
                if (!TextUtils.isEmpty(AppSharedPreferences.getInstance(this)!!.deviceToken)) {
                    AppSharedPreferences.getInstance(this)!!.deviceToken = ""
                }
                Log.d("TAG", "Fetching FCM registration token:" + task.result)
                AppSharedPreferences.getInstance(this)!!.deviceToken = task.result
                MoEFireBaseHelper.getInstance().passPushToken(applicationContext, task.result)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Countly.sharedInstance().onConfigurationChanged(newConfig)
        //        MoEInAppHelper.getInstance().onConfigurationChanged();
    }

    /**
     * Log-In into the mobile number after that navigate to otp verify screen @OtpVerifyActivity
     */
    @OnClick(R.id.tvLogin)
    fun loginClicked() {
        if (isValid) {
            val androidVersion = Build.VERSION.RELEASE
            val androidModel = Build.MODEL
            val androidBrand = Build.BRAND
            val versionName = BuildConfig.VERSION_CODE
            val properties = Properties()
            properties.addAttribute("version", Build.VERSION.RELEASE)
            properties.addAttribute("model", Build.MODEL)
            properties.addAttribute("appVersion", versionName)
            trackEvent(this, "AndroidVersion", properties)
            setUserAttribute(this@LoginActivity, "AppVersion", BuildConfig.VERSION_CODE)
            if (isConnection(this)) {
                disable_user_Intration(this, getString(R.string.loading))
                ApiCall.instance.userLoginWithMobile(
                    userNameEt!!.text.toString(),
                    preferences!!.deviceToken,
                    androidVersion,
                    BuildConfig.VERSION_NAME,
                    "$androidBrand, $androidModel",
                    this
                )
            } else {
                Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    /**
     * Log-In into the true-caller after that navigate to relevant screens according to conditions in @onSuccess method
     */
    @OnClick(R.id.llTruecaller)
    fun loginWithTruecaller() {
        if (TruecallerSDK.getInstance().isUsable) {
            TruecallerSDK.getInstance().getUserProfile(this)
        } else {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Truecaller App not installed.")
            dialogBuilder.setPositiveButton("OK") { dialog: DialogInterface, which: Int ->
                Log.d("TAG", "onClick: Closing dialog")
                dialog.dismiss()
            }
            dialogBuilder.setIcon(R.mipmap.truecaller_logo)
            dialogBuilder.setTitle(" ")
            val alertDialog = dialogBuilder.create()
            alertDialog.show()
        }
    }

    @OnTouch(R.id.check_box_iv)
    fun setRemember(view: View): Boolean {
        if (flag == 0) {
            flag = 1
            view.isSelected = true
        } else if (flag == 1) {
            flag = 0
            view.isSelected = false
        }
        return false
    }

    private val isValid: Boolean
        private get() {
            if (TextUtils.isEmpty(userNameEt!!.text.toString())) {
                userNameEt!!.requestFocus()
                Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT).show()
                return false
            } else if (userNameEt!!.text.toString().length < 9) {
                userNameEt!!.requestFocus()
                Toast.makeText(this, "Please enter valid mobile number", Toast.LENGTH_SHORT).show()
                return false
            }
            return true
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TruecallerSDK.SHARE_PROFILE_REQUEST_CODE) {
            if (TruecallerSDK.getInstance().isUsable) {
                TruecallerSDK.getInstance()
                    .onActivityResultObtained(this, requestCode, resultCode, data)
            }
        } else if (requestCode == 101) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                requestPermissionsAbove12()
            } else {
                requestPermissionsBelow12()
            }
        }
    }

    /**
     * Navigate corresponding screen
     * if type equal truecallerLogin
     * if user is screen_quest1 equal true then navigate onBoard first screen @QuestionFirstActivity
     * if user is screen_quest2 equal true then navigate segregation screen @BenefitActivity
     * if user is userOnBoard equal 2 then navigate Dashboard screen @DashboardActivity
     */
    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        if (type == "updateUserLocation") {
        }
        if (type == "login") {
            enableUserIntraction()
            val response = data as Response<SendOtpResponse>
            Log.d("Login response: ", response.toString())
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {
                    Log.d("Login response:", "Login Success.")
                    logger!!.logEvent("login")
                    setAlias(this, userNameEt!!.text.toString().trim { it <= ' ' })
                    setUniqueId(this, userNameEt!!.text.toString().trim { it <= ' ' })
                    val properties = Properties()
                    properties.addAttribute(
                        "mobileNo",
                        userNameEt!!.text.toString().trim { it <= ' ' })
                    properties.addAttribute("isLogin", true)
                    setMobileNumber(this, userNameEt!!.text.toString().trim { it <= ' ' })
                    trackEvent(this, "Login", properties)
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "login")
                    mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                    setUserDataWithMobile(
                        response.body()!!.data,
                        userNameEt!!.text.toString().trim { it <= ' ' })
                    callIntentOtp(Intent(this, OtpVerifyActivity::class.java))
                } else {
                    val properties = Properties()
                    properties.addAttribute(
                        "mobileNo",
                        userNameEt!!.text.toString().trim { it <= ' ' })
                    properties.addAttribute("isLogin", false)
                    trackEvent(this, "Login", properties)
                    Log.d("Login response:", "Login Fail.")
                    if (response.body()!!!=null && response.body()!!.errorMsg!=null) {

                        Toast.makeText(this, response.body()!!.errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Log.d("Login response:", response.message())
                try {
//                    showSnackMessage(findViewById(android.R.id.content), response.message())
                } catch (e: Exception) {
                    e.printStackTrace()
//                    showSnackMessage(findViewById(android.R.id.content), getString(R.string.some_error_occurred))
                }
            }
        } else if (type == "truecallerLogin") {
            enableUserIntraction()
            val response = data as Response<SendOtpResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {
                    val info = AppSharedPreferences.getInstance(this)
                    info!!.userId = response.body()!!.data!!.customerId
                    info.setprofileid(response.body()!!.data!!.profileId)
                    info.token = response.body()!!.data!!.token
                    info.userMobileNumber = response.body()!!.data!!.mobileNo

//                    info.userMobileNumber = userNameEt!!.text.toString().trim { it <= ' ' }

                    info.userLoggedIn = "1"

                    val properties = Properties()
                    properties.addAttribute("mobileNo", response.body()!!.data!!.mobileNo)
                    properties.addAttribute("isLogin", true)
                    trackEvent(this, "LoginTrueCaller", properties)
                    setMobileNumber(this, response.body()!!.data!!.mobileNo!!)
                    callTrackScreenAPi(
                        response.body()!!.data!!.customerId,
                        response.body()!!.data!!.profileId,
                        "userLogin",
                        "0",
                        "0"
                    )
                    if (this.type != null && !TextUtils.isEmpty(this.type)) {
                        when (this.type) {
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

                            "onboardingFirst" -> {
                                run { callIntent(Intent(this, QuestionFirstActivity::class.java)) }
                                run { callIntent(Intent(this, BenefitActivity::class.java)) }
                                run { callIntent(Intent(this, BenefitActivity::class.java)) }
                            }

                            "onboardingSecond" -> {
                                run { callIntent(Intent(this, BenefitActivity::class.java)) }
                                run { callIntent(Intent(this, BenefitActivity::class.java)) }
                            }

                            else -> {
                                callIntent(Intent(this, BenefitActivity::class.java))
                            }
                        }
                    } else if (!response.body()!!.data!!.screen_quest1!! && !response.body()!!.data!!.screen_quest2!!) {

                        if (response.body()!!.data?.profile_type == "B2B") {
                            info.userOnBoard = "2"
                            callIntent(Intent(this, DashboardActivity::class.java))
                        } else {
                            info.userOnBoard = "1"
                            callIntent(Intent(this, BenefitActivity::class.java))
                        }
                    } else if (response.body()!!.data!!.screen_quest1!!) {
                        callIntent(Intent(this, QuestionFirstActivity::class.java))
                    } else if (response.body()!!.data!!.screen_quest2!!) {

                        if (response.body()!!.data?.profile_type == "B2B") {
                            info.userOnBoard = "3"
                            callIntent(Intent(this, QuestionSecondActivity::class.java))
                        } else {
                            info.userOnBoard = "1"
                            callIntent(Intent(this, BenefitActivity::class.java))
                        }
                    } else {
                        info.userOnBoard = "2"
                        callIntent(Intent(this, DashboardActivity::class.java))
                    }
                } else {
                    if (response.body()!!!=null && response.body()!!.errorMsg!=null) {

                        Toast.makeText(this, response.body()!!.errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                showSnackMessage(
                    findViewById(android.R.id.content), getString(R.string.some_error_occurred)
                )
            }
        }
    }

    override fun onFailure(data: Any) {
        enableUserIntraction()
        val error = data as String
        if (error.contains("No address associated with hostname")) {
//            showSnackMessage(findViewById(android.R.id.content), "please check your internet connection")
        } else if (error.contains("java.net.SocketTimeoutException")) {
//            showSnackMessage(findViewById(android.R.id.content), "please check your internet connection")
        } else {
//            showSnackMessage(findViewById(android.R.id.content), getString(R.string.some_error_occurred))
        }
    }

    private fun setUserDataWithMobile(data: MobileData?, mobileNo: String?) {
        val info = AppSharedPreferences.getInstance(this)
        info!!.userId = data!!.customerId
        info.newUser = data.newUser
        info.setprofileid(data.profileId)
        info.userMobileNumber = mobileNo
        if (flag == 1) {
            info.rememberMe = "1"
        } else info.rememberMe = "0"
        callTrackScreenAPi(data.customerId, data.profileId, "userLogin", "0", "0")

        if (mylocation != null) {
            try {
                getCurrentAddressFromLocation(mylocation!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun callIntent(intent: Intent) {
        intent.putExtra("type", type)
        intent.putExtra("id", id)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    fun callIntentOtp(intent: Intent) {
        intent.putExtra("type", type)
        intent.putExtra("id", id)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra("mobileNumber", userNameEt!!.text.toString().trim { it <= ' ' })
        startActivity(intent)
    }

    private fun getCurrentAddressFromLocation(mLocation: Location?) {
        val geocoder = Geocoder(this, Locale.ENGLISH)
        try {
            val addresses = geocoder.getFromLocation(mLocation!!.latitude, mLocation!!.longitude, 1)
            if (addresses!=null && addresses!!.size > 0) {
                val fetchedAddress = addresses[0]
                UserAddress = fetchedAddress.locality
                userLocationUpdate(fetchedAddress)
            } else {
//                Toast.makeText(this, "Address is not available, Please select city", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
//            Toast.makeText(this, "Address is not available, Please select city", Toast.LENGTH_SHORT).show()
        }
    }

    private fun userLocationUpdate(fetchedAddress: Address) {
        setLocation(this, fetchedAddress.latitude, fetchedAddress.longitude)
        if (fetchedAddress.locality != null) {
            setUserAttribute(this, "city", fetchedAddress.locality)
        }
        if (fetchedAddress.adminArea != null) {
            setUserAttribute(this, "state", fetchedAddress.adminArea)
        }
        ApiCall.instance.updateUserLocation(
            preferences!!.getprofileid(),
            fetchedAddress.latitude.toString(),
            fetchedAddress.longitude.toString(),
            if (fetchedAddress.postalCode != null) fetchedAddress.postalCode else "",
            if (fetchedAddress.locality != null) fetchedAddress.locality else "",
            if (fetchedAddress.adminArea != null) fetchedAddress.adminArea else "",
            if (fetchedAddress.getAddressLine(0) != null) fetchedAddress.getAddressLine(0) else "",
            this
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun callTrackScreenAPi(
        customerId: String?,
        profileId: String?,
        eventName: String,
        actionId: String,
        actionHeading: String,
    ) {
        if (isConnection(this)) {
            ApiCall.instance.trackScreen(
                customerId, profileId, eventName, actionId, actionHeading, this
            )
        } else {
            Toast.makeText(this, "please check your internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    fun pageSwitcher(seconds: Int) {
        timer = Timer() // At this line a new Thread will be created
        timer!!.scheduleAtFixedRate(RemindTask(), 0, (seconds * 1000).toLong()) // delay
    }

    internal inner class RemindTask : TimerTask() {
        override fun run() {
            runOnUiThread {
                if (page > 2) { // In my case the number of pages are 5
                    page = 0
                } else {
                    viewPager!!.currentItem = page++
                }
            }
        }
    }

    private fun requestDeviceLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            interval = 3000
            fastestInterval = 3000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            val state = locationSettingsResponse.locationSettingsStates
            /*val label =
                "GPS >> (Present: ${state.isGpsPresent}  | Usable: ${state.isGpsUsable} ) \n\n" +
                        "Network >> ( Present: ${state.isNetworkLocationPresent} | Usable: ${state.isNetworkLocationUsable} ) \n\n" +
                        "Location >> ( Present: ${state.isLocationPresent} | Usable: ${state.isLocationUsable} )"
            showToast(label)*/
            getLocation()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(this, 100)
                } catch (sendEx: SendIntentException) {
                    showToast(sendEx.message.toString())
                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this, permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    /*private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                permission.ACCESS_COARSE_LOCATION,
                permission.ACCESS_FINE_LOCATION
            ), permissionId
        )
    }*/

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                requestPermissions()
            }
        }
    }

    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this, permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                mFusedLocationClient?.lastLocation?.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {

                        mylocation = location

                        /*val geocoder = Geocoder(this, Locale.getDefault())
                        val list: MutableList<Address>? =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)

                        latitude = list!![0].latitude
                        longitude = list!![0].longitude
                        countryName = list!![0].countryName
                        locality = list!![0].locality
                        address = list!![0].getAddressLine(0)*/

                    } else {
                        getLocation()
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
//            requestPermissions()
        }
    }

    private fun requestPermissionsAbove12() {
        Dexter.withActivity(this) // below line is use to request the number of permissions which are required in our app.
            .withPermissions(
                permission.ACCESS_COARSE_LOCATION,
                permission.ACCESS_FINE_LOCATION, permission.POST_NOTIFICATIONS
            ) // after adding permissions we are calling an with listener method.
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    // this method is called when all permissions are granted
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        // do you work now
                        requestDeviceLocationSettings()

                    }
                    // check for permanent denial of any permission
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        // permission is denied permanently, we will show user a dialog message.
                        if (isLocationEnabled()) {
                            showSettingsDialog()
                        } else {
                            Toast.makeText(
                                this@LoginActivity, "Please turn on location", Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivity(intent)
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest?>?,
                    permissionToken: PermissionToken,
                ) {
                    // this method is called when user grants some permission and denies some of them.
                    permissionToken.continuePermissionRequest()
                }
            }).withErrorListener { error: DexterError? ->
                // we are displaying a toast message for error message.
                Toast.makeText(applicationContext, "Error occurred! ", Toast.LENGTH_SHORT).show()
            } // below line is use to run the permissions on same thread and to check the permissions
            .onSameThread().check()
    }

    private fun requestPermissionsBelow12() {
        Dexter.withActivity(this) // below line is use to request the number of permissions which are required in our app.
            .withPermissions(
                permission.ACCESS_COARSE_LOCATION,
                permission.ACCESS_FINE_LOCATION
            ) // after adding permissions we are calling an with listener method.
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    // this method is called when all permissions are granted
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        // do you work now
                        requestDeviceLocationSettings()

                    }
                    // check for permanent denial of any permission
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        // permission is denied permanently, we will show user a dialog message.
                        if (isLocationEnabled()) {
                            showSettingsDialog()
                        } else {
                            Toast.makeText(
                                this@LoginActivity, "Please turn on location", Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivity(intent)
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest?>?,
                    permissionToken: PermissionToken,
                ) {
                    // this method is called when user grants some permission and denies some of them.
                    permissionToken.continuePermissionRequest()
                }
            }).withErrorListener { error: DexterError? ->
                // we are displaying a toast message for error message.
                Toast.makeText(applicationContext, "Error occurred! ", Toast.LENGTH_SHORT).show()
            } // below line is use to run the permissions on same thread and to check the permissions
            .onSameThread().check()
    }

    private fun showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        val builder = android.app.AlertDialog.Builder(MainApplication.currentActivity)

        // below line is the title
        // for our alert dialog.
        builder.setTitle("Need Permission")

        // below line is our message for our dialog
        builder.setMessage("You need to give permission for Location. Please go to settings-> Permission-> Location and click allow.")
        builder.setPositiveButton(
            "GOTO SETTINGS"
        ) { dialog, which -> // this method is called on click on positive
            // button and on clicking shit button we
            // are redirecting our user from our app to the
            // settings page of our app.
            dialog.cancel()
            // below is the intent from which we
            // are redirecting our user.
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", MainApplication.currentActivity!!.packageName, null)
            intent.data = uri
            startActivityForResult(intent, 101)
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> // this method is called when
            // user click on negative button.
            dialog.cancel()
        }
        // below line is used
        // to display our dialog
        try {
            if (builder != null) {
                builder.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}