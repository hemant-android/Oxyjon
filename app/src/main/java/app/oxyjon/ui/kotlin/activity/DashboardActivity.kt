package app.oxyjon.ui.kotlin.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.bean.PremiumResponse
import app.oxyjon.databinding.ActivityDashboardBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.retrofit.response.CommonResponse
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.utils.CheckConnection
import app.oxyjon.utils.FunctionHelper
import com.google.android.material.snackbar.Snackbar
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper
import eu.dkaratzas.android.inapp.update.Constants
import eu.dkaratzas.android.inapp.update.InAppUpdateManager
import eu.dkaratzas.android.inapp.update.InAppUpdateManager.InAppUpdateHandler
import eu.dkaratzas.android.inapp.update.InAppUpdateStatus
import retrofit2.Response


class DashboardActivity : BaseActivity(), InAppUpdateHandler, IApiCallback {
    private var planId: String? = ""
    lateinit var binding: ActivityDashboardBinding
    private var type = ""
    private var id = ""
    var isPremiumMember: String? = ""

    private lateinit var navControllerMain: NavController

    private val REQ_CODE_VERSION_UPDATE = 530
    private val TAG = "DashboardActivity"
    private var inAppUpdateManager: InAppUpdateManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MainApplication.currentActivity = this

        inAppUpdateManager = InAppUpdateManager.Builder(this, REQ_CODE_VERSION_UPDATE)
            .resumeUpdates(true) // Resume the update, if the update was stalled. Default is true
            .mode(Constants.UpdateMode.FLEXIBLE)
            .snackBarMessage("An update has just been downloaded.").snackBarAction("RESTART")
            .useCustomNotification(true).handler(this)

        inAppUpdateManager!!.checkForAppUpdate()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_dashboard) as NavHostFragment

        navControllerMain = navHostFragment.findNavController()

        val bundle = intent.extras
        if (bundle != null) {
            type = bundle.getString("type").toString()
            id = bundle.getString("id") ?: ""
        }

        if (type != null && !TextUtils.isEmpty(type)) {
            when (type) {
                "DietPlan12Month" -> {
                    Intent(this, PlanDetailActivity::class.java).also {
                        it.putExtra("planId", 6)
                        startActivity(it)
                    }
                }

                "DietPlan6Month" -> {
                    Intent(this, PlanDetailActivity::class.java).also {
                        it.putExtra("planId", 5)
                        startActivity(it)
                    }
                }

                "DietPlan3Month" -> {
                    Intent(this, PlanDetailActivity::class.java).also {
                        it.putExtra("planId", 4)
                        startActivity(it)
                    }
                }

                "DietPlan1Month" -> {
                    Intent(this, PlanDetailActivity::class.java).also {
                        it.putExtra("planId", 3)
                        startActivity(it)
                    }
                }

                "Blog" -> {
                    Intent(this, BlogDetailActivity::class.java).also {
                        it.putExtra("navType", "deepLink")
                        it.putExtra("blogId", id)
                        startActivity(it)
                    }
                }
            }
        }

        binding.imgCallUs.setOnClickListener {
            val properties = Properties()
            properties.addAttribute("isClick", true)
            MoEAnalyticsHelper.trackEvent(this, "ClickCallUsButton", properties)

            if (isPremiumMember == "Yes") {
                var selectedService = ""

                val properties = Properties()
                properties.addAttribute("isClick", true)
                MoEAnalyticsHelper.trackEvent(this, "ClickCallUsButton", properties)

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

                dialog.setContentView(R.layout.dialog_request_callback_paid)

                val tvFirst = dialog.findViewById(R.id.tvFirst) as TextView
                val tvSecond = dialog.findViewById(R.id.tvSecond) as TextView
                val tvThird = dialog.findViewById(R.id.tvThird) as TextView
                val edtNote = dialog.findViewById(R.id.edtNote) as EditText

                val llSubmitRequest = dialog.findViewById(R.id.llSubmitRequest) as LinearLayout

                tvFirst.setOnClickListener {
                    selectedService = tvFirst.text.toString()

                    tvFirst.setBackgroundResource(R.drawable.bg_rectangle_fill)
                    tvSecond.setBackgroundResource(R.drawable.bg_rectangle_gray)
                    tvThird.setBackgroundResource(R.drawable.bg_rectangle_gray)
                }
                tvSecond.setOnClickListener {
                    selectedService = tvSecond.text.toString()
                    tvFirst.setBackgroundResource(R.drawable.bg_rectangle_gray)
                    tvSecond.setBackgroundResource(R.drawable.bg_rectangle_fill)
                    tvThird.setBackgroundResource(R.drawable.bg_rectangle_gray)
                }
                tvThird.setOnClickListener {
                    selectedService = tvThird.text.toString()
                    tvFirst.setBackgroundResource(R.drawable.bg_rectangle_gray)
                    tvSecond.setBackgroundResource(R.drawable.bg_rectangle_gray)
                    tvThird.setBackgroundResource(R.drawable.bg_rectangle_fill)
                }

                llSubmitRequest.setOnClickListener {
                    if (!TextUtils.isEmpty(selectedService)) {

                        if (CheckConnection.isConnection(this)) {
                            FunctionHelper.disable_user_Intration(
                                this,
                                resources.getString(R.string.loading)
                            )
                            if (dialog != null && dialog.isShowing) {
                                dialog.dismiss()
                            }

                            ApiCall.instance.getCallBackRequest(
                                "Paid",
                                selectedService,
                                edtNote.text.toString().trim(),
                                this
                            )

                        } else {
                            Toast.makeText(
                                this,
                                "please check your internet connection",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            this,
                            "please select service type",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                if (dialog != null && dialog.isShowing) {
                    dialog.dismiss()
                } else {
                    dialog.show()
                }
            } else {
                var selectedService = ""
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

                dialog.setContentView(R.layout.dialog_request_callback_free)

                val tvFirst = dialog.findViewById(R.id.tvFirst) as TextView
                val tvSecond = dialog.findViewById(R.id.tvSecond) as TextView
                val edtNote = dialog.findViewById(R.id.edtNote) as EditText

                val llSubmitRequest = dialog.findViewById(R.id.llSubmitRequest) as LinearLayout

                tvFirst.setOnClickListener {
                    selectedService = tvFirst.text.toString()

                    tvFirst.setBackgroundResource(R.drawable.bg_rectangle_fill)
                    tvSecond.setBackgroundResource(R.drawable.bg_rectangle_gray)
                }
                tvSecond.setOnClickListener {
                    selectedService = tvSecond.text.toString()
                    tvFirst.setBackgroundResource(R.drawable.bg_rectangle_gray)
                    tvSecond.setBackgroundResource(R.drawable.bg_rectangle_fill)
                }

                llSubmitRequest.setOnClickListener {

                    if (!TextUtils.isEmpty(selectedService)) {

                        if (CheckConnection.isConnection(this)) {
                            FunctionHelper.disable_user_Intration(
                                this,
                                resources.getString(R.string.loading)
                            )

                            if (dialog != null && dialog.isShowing) {
                                dialog.dismiss()
                            }

                            ApiCall.instance.getCallBackRequest(
                                "Free",
                                selectedService,
                                edtNote.text.toString().trim(),
                                this
                            )

                        } else {
                            Toast.makeText(
                                this,
                                "please check your internet connection",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            this,
                            "please select service type",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                if (dialog != null && dialog.isShowing) {
                    dialog.dismiss()
                } else {
                    dialog.show()
                }
            }
        }

        binding.imgNotification.setOnClickListener {
            val intent = Intent(this, NotificationListActivity::class.java)
            startActivity(intent)
        }
        binding.llPremium.setOnClickListener {
            val intent = Intent(this, MyCarePlanActivity::class.java)
            startActivity(intent)

            val properties = Properties()
            properties.addAttribute("isClick", true)
            MoEAnalyticsHelper.trackEvent(this, "ClickPremiumButton", properties)
        }

        binding.llHome.setOnClickListener {
            binding.llHome.setBackgroundResource(R.color.skyLight)
            binding.llDiary.setBackgroundResource(R.color.transparent)
            binding.llPlan.setBackgroundResource(R.color.transparent)
            binding.llProfile.setBackgroundResource(R.color.transparent)
            navControllerMain.popBackStack()
            navControllerMain.navigate(R.id.dashboardFragment)
            val properties = Properties()
            properties.addAttribute("isCLick", true)
            MoEAnalyticsHelper.trackEvent(this, "homeTab", properties)
        }

        binding.llDiary.setOnClickListener {
            binding.llHome.setBackgroundResource(R.color.transparent)
            binding.llDiary.setBackgroundResource(R.color.skyLight)
            binding.llPlan.setBackgroundResource(R.color.transparent)
            binding.llProfile.setBackgroundResource(R.color.transparent)
            navControllerMain.popBackStack()
            navControllerMain.navigate(R.id.diaryFragment)

            val properties = Properties()
            properties.addAttribute("isCLick", true)
            MoEAnalyticsHelper.trackEvent(this, "dairyTab", properties)
        }

        binding.llPlan.setOnClickListener {
            binding.llHome.setBackgroundResource(R.color.transparent)
            binding.llDiary.setBackgroundResource(R.color.transparent)
            binding.llPlan.setBackgroundResource(R.color.skyLight)
            binding.llProfile.setBackgroundResource(R.color.transparent)
            navControllerMain.popBackStack()
            navControllerMain.navigate(R.id.planFragment)

            val properties = Properties()
            properties.addAttribute("isCLick", true)
            MoEAnalyticsHelper.trackEvent(this, "planTab", properties)
        }
        binding.llProfile.setOnClickListener {
            binding.llHome.setBackgroundResource(R.color.transparent)
            binding.llDiary.setBackgroundResource(R.color.transparent)
            binding.llPlan.setBackgroundResource(R.color.transparent)
            binding.llProfile.setBackgroundResource(R.color.skyLight)
            navControllerMain.popBackStack()
            navControllerMain.navigate(R.id.profileFragment)

            val properties = Properties()
            properties.addAttribute("isCLick", true)
            MoEAnalyticsHelper.trackEvent(this, "profileTab", properties)
        }

        binding.tvMessage.setOnClickListener {
            val intent = Intent(this, PlanDetailActivity::class.java)
            intent.putExtra("planId", planId!!.toInt())
            intent.putExtra("planName", "Health plan")
            startActivity(intent)

            val properties = Properties()
            properties.addAttribute("isClick", true)
            MoEAnalyticsHelper.trackEvent(this, "ClickOrangeBarHome", properties)
        }

    }

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this

        if (CheckConnection.isConnection(this)) {
            ApiCall.instance.getUserPaidOrNot(this)
        }
    }

    @SuppressLint("LongLogTag")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_VERSION_UPDATE) {
            if (resultCode != RESULT_OK) {
                Log.e("Update flow failed! Result code: ", "" + resultCode)
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Update successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onInAppUpdateError(code: Int, error: Throwable?) {
    }

    override fun onInAppUpdateStatus(status: InAppUpdateStatus?) {
        if (status!!.isDownloaded) {
            val rootView = window.decorView.findViewById<View>(android.R.id.content)
            val snackbar = Snackbar.make(
                rootView, "An update has just been downloaded.", Snackbar.LENGTH_INDEFINITE
            )

            snackbar.setAction("RESTART") { view: View? ->
                inAppUpdateManager!!.completeUpdate()
            }
            snackbar.show()
        }
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        if (type == "IsPremium") {
            val response = data as Response<PremiumResponse>
            if (response.isSuccessful) {
                if (response.isSuccessful && response.body()!!.errorCode == "0") {

                    if (response.body()!!.profile_type != null && response.body()!!.profile_type == "B2B") {
                        binding.llPlan.visibility = View.GONE
                        binding.llBottom.weightSum = 3F

                        val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
                        lp.weight = 1f
                        binding.llHome.layoutParams = lp
                        binding.llDiary.layoutParams = lp
                        binding.llProfile.layoutParams = lp

                    } else {
                        binding.llPlan.visibility = View.VISIBLE

                        binding.llBottom.weightSum = 4F

                        val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
                        lp.weight = 1f
                        binding.llHome.layoutParams = lp
                        binding.llDiary.layoutParams = lp
                        binding.llPlan.layoutParams = lp
                        binding.llProfile.layoutParams = lp
                    }

                    if (response.body()!!.health_plan_id != null && !TextUtils.isEmpty(response.body()!!.health_plan_id)) {
                        planId = response.body()!!.health_plan_id
                    }

                    if (!TextUtils.isEmpty(response.body()!!.subscription_status) && response.body()!!.subscription_status == "Free") {

                        if (response.body()!!.on_board_status == "Onboarded") {
                            binding.tvMessage.visibility = View.VISIBLE
                            binding.rlPremium.visibility = View.GONE
                            binding.rlPremiumBlur.visibility = View.VISIBLE

                            binding.tvMessage.text = response.body()!!.premium_message

                            isPremiumMember = "Yes"

                        } else {
                            binding.tvMessage.visibility = View.GONE
                            binding.rlPremium.visibility = View.GONE
                            binding.rlPremiumBlur.visibility = View.GONE

                            isPremiumMember = "No"
                        }

                    } else {
                        isPremiumMember = "Yes"

                        if (!TextUtils.isEmpty(response.body()!!.is_premium_member) && response.body()!!.is_premium_member == "Yes") {
                            binding.rlPremium.visibility = View.VISIBLE
                            binding.rlPremiumBlur.visibility = View.GONE
                            binding.tvMessage.visibility = View.GONE
                        } else {
                            binding.rlPremium.visibility = View.GONE
                            binding.rlPremiumBlur.visibility = View.VISIBLE
                            binding.tvMessage.visibility = View.VISIBLE
                            binding.tvMessage.text = response.body()!!.premium_message
                        }
                    }


                }
            }
        }
        if (type == "callBackRequest") {
            FunctionHelper.enableUserIntraction()

            val properties = Properties()
            properties.addAttribute("isClick", true)
            properties.addAttribute("isSubmit", true)
            MoEAnalyticsHelper.trackEvent(this, "ClickCallUsButton", properties)

            val response = data as Response<CommonResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "1") {

                    Toast.makeText(
                        this,
                        response.body()!!.errorMsg,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }
}