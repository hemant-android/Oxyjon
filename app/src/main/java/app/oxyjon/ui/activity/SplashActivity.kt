package app.oxyjon.ui.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.bean.FoodDiaryListResponse
import app.oxyjon.bean.PreLoginResponse
import app.oxyjon.database.AppDatabase
import app.oxyjon.database.AppExecutors
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.database.FoodDiary
import app.oxyjon.databinding.ActivitySplashBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.retrofit.response.SendOtpResponse
import app.oxyjon.ui.kotlin.activity.DashboardActivity
import app.oxyjon.utils.FunctionHelper
import butterknife.ButterKnife
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.moengage.firebase.MoEFireBaseHelper.Companion.getInstance
import io.branch.referral.Branch
import retrofit2.Response
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


/**
 * SplashActivity
 */
class SplashActivity : BaseActivity(), IApiCallback {
    private var handler: Handler? = null

    lateinit var binding: ActivitySplashBinding
    private var mFoodList: ArrayList<FoodDiaryListResponse.Fooditemlist.Data>? = ArrayList()
    private var allFoodList: List<FoodDiary?>? = ArrayList()
    private var mDb: AppDatabase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ButterKnife.bind(this)
        MainApplication.currentActivity = this
        MainApplication.clickFood = false
        MainApplication.clickSugar = false
        MainApplication.clickMedicine = false
        MainApplication.clickStepCounter = false

        mDb = AppDatabase.getInstance(MainApplication.currentActivity)

        generateKeyHash()

    }

    private var runnable = Runnable {
        val intent = intent
        if (intent != null) {
            val appLinkData = intent.data
            if (appLinkData != null) {
                var queryParameter = appLinkData!!.getQueryParameter("type")

                if (queryParameter != null && !TextUtils.isEmpty(queryParameter)) {
                    if (queryParameter == "Blog") {
                        callActivity(queryParameter, appLinkData!!.getQueryParameter("id"))
                    } else {
                        callActivity(queryParameter, appLinkData!!.getQueryParameter("type"))
                    }

                } else {
                    Firebase.dynamicLinks
                        .getDynamicLink(intent)
                        .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                            // Get deep link from result (may be null if no link is found)
                            var deepLink: Uri? = null
                            if (pendingDynamicLinkData != null) {
                                deepLink = pendingDynamicLinkData.link

                                callActivity(deepLink!!.getQueryParameter("type"), "")
                            } else {
                                callActivity("", "")
                            }
                        }
                        .addOnFailureListener(this)
                        { e ->
                            Log.w("SplashActivity", "getDynamicLink:onFailure", e)
                        }
                }

            } else {
                callActivity("", "")
            }
        } else {
            callActivity("", "")
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent != null &&
            intent.hasExtra("branch_force_new_session") &&
            intent.getBooleanExtra("branch_force_new_session", false)
        ) {
            Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit()
        }
    }

    override fun onStart() {
        super.onStart()
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener)
            .withData(if (intent != null) intent.data else null).init()
    }

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this
        if (handler == null) {
            handler = Handler()
            handler!!.postDelayed(runnable, 2000)
        }

        try {
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task: Task<String> ->
                    if (!task.isSuccessful) {
                        AppSharedPreferences.getInstance(this)!!.deviceToken = ""
                        Log.d("TAG", "Fetching FCM registration token failed:", task.exception)
                        return@addOnCompleteListener
                    }
                    if (!TextUtils.isEmpty(AppSharedPreferences.getInstance(this)!!.deviceToken)
                    ) {
                        AppSharedPreferences.getInstance(this)!!.deviceToken = ""
                    }
                    Log.d("TAG", "Fetching FCM registration token:" + task.result)
                    AppSharedPreferences.getInstance(this)!!.deviceToken = task.result
                    getInstance().passPushToken(applicationContext, task.result)

                    if (AppSharedPreferences.getInstance(this)!!.userMobileNumber!! != null && AppSharedPreferences.getInstance(
                            this
                        )!!.userMobileNumber!!.isNotEmpty()
                    ) {
                        callPreLoginAPi(
                            task.result,
                            AppSharedPreferences.getInstance(this)!!.userMobileNumber!!
                        )
                    } else {
                        callPreLoginAPi(task.result, "0")
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Navigate corresponding screen
     * if user is not log-in then navigate login screen @LoginActivity
     * if user is userOnBoard equal empty then navigate onBoard first screen @QuestionFirstActivity
     * if user is userOnBoard equal 1 then navigate segregation screen @BenefitActivity
     * if user is userOnBoard equal 2 then navigate Dashboard screen @DashboardActivity
     */
    private fun callActivity(type: String?, id: String?) {
        if (AppSharedPreferences.getInstance(this)!!.userLoggedIn!!.isEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("type", type)
            intent.putExtra("id", id)
            startActivity(intent)
            finish()
        } else {
            if (type.equals("onboardingFirst", ignoreCase = true)) {
                val intent = Intent(this, QuestionFirstActivity::class.java)
                intent.putExtra("type", type)
                intent.putExtra("id", id)
                startActivity(intent)
                finish()
            } else if (type.equals("onboardingSecond", ignoreCase = true)) {
                val intent = Intent(this, BenefitActivity::class.java)
                intent.putExtra("type", type)
                intent.putExtra("id", id)
                startActivity(intent)
                finish()
            } else if (AppSharedPreferences.getInstance(this)!!.userOnBoard!!.isEmpty()) {
                val intent = Intent(this, QuestionFirstActivity::class.java)
                intent.putExtra("type", type)
                intent.putExtra("id", id)
                startActivity(intent)
                finish()
            }
            else if (AppSharedPreferences.getInstance(this)!!.userOnBoard
                    .equals("1", ignoreCase = true)
            ) {
                val intent = Intent(this, BenefitActivity::class.java)
                intent.putExtra("type", type)
                intent.putExtra("id", id)
                startActivity(intent)
                finish()
            }
            else if (AppSharedPreferences.getInstance(this)!!.userOnBoard
                    .equals("3", ignoreCase = true)
            ) {
                val intent = Intent(this, QuestionSecondActivity::class.java)
                intent.putExtra("type", type)
                intent.putExtra("id", id)
                startActivity(intent)
                finish()
            }
            else if (AppSharedPreferences.getInstance(this)!!.userOnBoard
                    .equals("2", ignoreCase = true)
            ) {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("type", type)
                intent.putExtra("id", id)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("type", type)
                intent.putExtra("id", id)
                startActivity(intent)
                finish()
            }
        }
    }
    override fun onStop() {
        super.onStop()
        if (handler != null) handler!!.removeCallbacks(runnable)
        handler = null
    }

    /**
     * Generate hash key for facebook integration
     */
    private fun generateKeyHash() {
        try {
            val info = packageManager.getPackageInfo(
                "app.oxyjon",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }
    }

    /**
     * for token
     */
    private fun callPreLoginAPi(token: String, mobileNo: String) {
        if (isConnection(this)) {
            ApiCall.instance.getPreLogin(token, mobileNo, this)
        } else {
            Toast.makeText(
                this@SplashActivity,
                "please check your internet connection",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    /**
     * APi response
     */
    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        /**
         * type equal preLogin, check the token
         */
        if (type == "preLogin") {
            val response = data as Response<PreLoginResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {
                    if (response.body()!!.data != null) {
                        val info: AppSharedPreferences = AppSharedPreferences.getInstance(this)!!
                        info.token = response.body()!!.data?.auth_token
                    }
                }
            }
            if (isConnection(MainApplication.currentActivity)) {
                ApiCall.instance
                    .getFoodListMaster(
                        AppSharedPreferences.getInstance(this)
                        !!.deviceToken, 1, this
                    )
            } else {
                Toast.makeText(
                    MainApplication.currentActivity,
                    "please check your internet connection",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        if (type == "foodListMaster") {
            val response = data as Response<FoodDiaryListResponse?>
            if (response?.body() != null && response.isSuccessful && response.body()!!.errorCode == "0") {
                if (response.body()!!.foodupdate_action != null && response.body()!!.foodupdate_action.update_data.equals(
                        "True",
                        ignoreCase = true
                    )
                ) {
                    if (response.body()!!.fooditemlist != null) {
                        val currentPage = response.body()!!.fooditemlist.current_page
                        val lastPage = response.body()!!.fooditemlist.last_page
                        if (mFoodList != null && mFoodList!!.size > 0) {
                            mFoodList!!.clear()
                        }
                        mFoodList = response.body()!!.fooditemlist.data
                        Log.e("Food Page size is: ", mFoodList!!.size.toString())
                        Log.e("Food Current Page is: ", currentPage.toString())
                        AppExecutors.instance!!.diskIO().execute(Runnable {
                            MainApplication.currentActivity!!.runOnUiThread(Runnable { //                                        mDb.personDao().deleteFoodDiary();
                                for ((calorie_gm, carbs_gm, cuisine_type, fats_gm, fiber_gm, food_item_name, food_type, id, meal_bed_time, meal_breakfast, meal_dinner, meal_early_morning, meal_evening_snack, meal_lunch, meal_morning_snack, measurement_unit, protein_gm, quantity_primary, quantity_unit_primary, quantity_secondary, quantity_unit_secondary) in mFoodList!!) {
                                    val foodDiary = FoodDiary(
                                        id,
                                        food_type,
                                        food_item_name,
                                        measurement_unit,
                                        quantity_primary,
                                        quantity_unit_primary ?: "",
                                        quantity_secondary,
                                        quantity_unit_secondary,
                                        calorie_gm,
                                        protein_gm,
                                        carbs_gm,
                                        fats_gm,
                                        fiber_gm,
                                        meal_early_morning,
                                        meal_breakfast,
                                        meal_morning_snack,
                                        meal_lunch,
                                        meal_evening_snack,
                                        meal_dinner,
                                        meal_bed_time?.toString() ?: "",
                                        cuisine_type?.toString() ?: ""
                                    )
                                    mDb!!.personDao().insertFood(foodDiary)
                                }
                            })
                        })
                        if (currentPage != lastPage) {
                            if (isConnection(MainApplication.currentActivity)) {
                                ApiCall.instance
                                    .getFoodListMaster(
                                        AppSharedPreferences.getInstance(
                                            this
                                        )!!.deviceToken, currentPage + 1, this
                                    )
                            } else {
                                Toast.makeText(
                                    MainApplication.currentActivity,
                                    "please check your internet connection",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } /*else if (currentPage == lastPage) {
                            if (isConnection(MainApplication.mActivity)) {
                                ApiCall.getInstance().foodSyncComplete(customerid, profileid, response.body().getFoodupdate_action().getSync_id(), this);
                            } else {
                                Toast.makeText(MainApplication.mActivity, "please check your internet connection", Toast.LENGTH_SHORT).show();
                            }
                        }*/
                    }
                } else if (response.body()!!.foodupdate_action != null && response.body()!!.foodupdate_action.update_data.equals(
                        "False",
                        ignoreCase = true
                    )
                ) {
                    AppExecutors.instance!!.diskIO()
                        .execute(Runnable { allFoodList = mDb!!.personDao().allFoodDiary })
                    if (allFoodList != null && allFoodList!!.isNotEmpty() && allFoodList!!.size >= response.body()!!.fooditemlist.total) {
                        Log.e("Food Page size is: ", allFoodList!!.size.toString())
                    } else {
                        if (response.body()!!.fooditemlist != null) {
                            val currentPage = response.body()!!.fooditemlist.current_page
                            val lastPage = response.body()!!.fooditemlist.last_page
                            if (mFoodList != null && mFoodList!!.size > 0) {
                                mFoodList!!.clear()
                            }
                            mFoodList = response.body()!!.fooditemlist.data
                            Log.e("Food Page size is: ", mFoodList!!.size.toString())
                            Log.e("Current Page is: ", currentPage.toString())
                            AppExecutors.instance!!.diskIO().execute {
                                MainApplication.currentActivity!!.runOnUiThread {
                                    for ((calorie_gm, carbs_gm, cuisine_type, fats_gm, fiber_gm, food_item_name, food_type, id, meal_bed_time, meal_breakfast, meal_dinner, meal_early_morning, meal_evening_snack, meal_lunch, meal_morning_snack, measurement_unit, protein_gm, quantity_primary, quantity_unit_primary, quantity_secondary, quantity_unit_secondary) in mFoodList!!) {
                                        val foodDiary = FoodDiary(
                                            id,
                                            food_type,
                                            food_item_name,
                                            measurement_unit,
                                            quantity_primary,
                                            quantity_unit_primary ?: "",
                                            quantity_secondary,
                                            quantity_unit_secondary,
                                            calorie_gm,
                                            protein_gm,
                                            carbs_gm,
                                            fats_gm,
                                            fiber_gm,
                                            meal_early_morning,
                                            meal_breakfast,
                                            meal_morning_snack,
                                            meal_lunch,
                                            meal_evening_snack,
                                            meal_dinner,
                                            meal_bed_time?.toString() ?: "",
                                            cuisine_type?.toString() ?: ""
                                        )
                                        mDb!!.personDao().insertFood(foodDiary)
                                    }
                                }
                            }
                            if (currentPage != lastPage) {
                                if (isConnection(MainApplication.currentActivity)) {
                                    ApiCall.instance
                                        .getFoodListMaster(
                                            AppSharedPreferences.Companion.getInstance(
                                                this
                                            )!!.deviceToken, currentPage + 1, this
                                        )
                                } else {
                                    Toast.makeText(
                                        MainApplication.currentActivity,
                                        "please check your internet connection",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onFailure(data: Any) {
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
            /*FunctionHelper.showSnackMessage(
                findViewById(android.R.id.content),
                "please check your internet connection"
            )*/
        }
    }

    private val branchReferralInitListener =
        Branch.BranchReferralInitListener { linkProperties, error ->
            // do stuff with deep link data (nav to page, display content, etc)
        }

    companion object {
        fun isConnection(ctx: Context?): Boolean {
            val connectivityManager =
                ctx!!.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val ni = connectivityManager.activeNetworkInfo
            return ni != null && ni.isAvailable && ni.isConnected
        }
    }
}