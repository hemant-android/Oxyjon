package app.oxyjon.ui.kotlin.fragment.dashboard

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import app.oxyjon.MainApplication
import app.oxyjon.MainApplication.Companion.clickFood
import app.oxyjon.MainApplication.Companion.clickMedicine
import app.oxyjon.MainApplication.Companion.clickStepCounter
import app.oxyjon.MainApplication.Companion.clickSugar
import app.oxyjon.MainApplication.Companion.currentActivity
import app.oxyjon.R
import app.oxyjon.bean.DashboardResponse
import app.oxyjon.bean.FoodDiaryListResponse
import app.oxyjon.bean.GetMyFoodDiaryResponse
import app.oxyjon.bean.MedicineListResponse
import app.oxyjon.bean.MyMedicineResponse
import app.oxyjon.bean.StepsCaloriesData
import app.oxyjon.bean.StepsCaloriesUploadData
import app.oxyjon.database.AppDatabase
import app.oxyjon.database.AppExecutors
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.database.FoodDiary
import app.oxyjon.database.Medicine
import app.oxyjon.databinding.FragmentDashboardBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.retrofit.response.CommonResponse
import app.oxyjon.retrofit.response.StepCountResponse
import app.oxyjon.ui.activity.WebViewActivity
import app.oxyjon.ui.kotlin.activity.AddSugarActivity
import app.oxyjon.ui.kotlin.activity.AnalyticsReportActivity
import app.oxyjon.ui.kotlin.activity.BlogDetailActivity
import app.oxyjon.ui.kotlin.activity.CongratulationActivity
import app.oxyjon.ui.kotlin.activity.DietChartActivity
import app.oxyjon.ui.kotlin.activity.DietPlanDetailActivity
import app.oxyjon.ui.kotlin.activity.DoctorConsultationActivity
import app.oxyjon.ui.kotlin.activity.FoodDiaryActivity
import app.oxyjon.ui.kotlin.activity.GoogleFitStepActivity
import app.oxyjon.ui.kotlin.activity.MedicineListActivity
import app.oxyjon.ui.kotlin.activity.MyCarePlanActivity
import app.oxyjon.ui.kotlin.activity.NotificationListActivity
import app.oxyjon.ui.kotlin.activity.PlanDetailActivity
import app.oxyjon.ui.kotlin.activity.StepGoalActivity
import app.oxyjon.ui.kotlin.activity.TestBookActivity
import app.oxyjon.ui.kotlin.fragment.dashboard.adapter.ActionBoxAdapter
import app.oxyjon.ui.kotlin.fragment.dashboard.adapter.BuyPlanAdapter
import app.oxyjon.ui.kotlin.fragment.dashboard.adapter.BuyTestAdapter
import app.oxyjon.ui.kotlin.fragment.dashboard.adapter.NewsFeedAdapter
import app.oxyjon.ui.kotlin.fragment.dashboard.adapter.PromotionAdapter
import app.oxyjon.ui.kotlin.fragment.dashboard.adapter.RecipeAdapter
import app.oxyjon.ui.kotlin.fragment.dashboard.adapter.TopBannerAdapter
import app.oxyjon.utils.CheckConnection.isConnection
import app.oxyjon.utils.FunctionHelper
import app.oxyjon.utils.PermissionUtil
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper.setAlias
import com.moengage.core.analytics.MoEAnalyticsHelper.setUserAttribute
import com.moengage.core.analytics.MoEAnalyticsHelper.trackEvent
import retrofit2.Response
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @DashboardFragment
 */
class DashboardFragment : Fragment(), IApiCallback, BuyPlanAdapter.onClickListner,
    BuyTestAdapter.onClickListner, ActionBoxAdapter.onClickListner, PromotionAdapter.onClickListner,
    NewsFeedAdapter.onClickListner, TopBannerAdapter.onClickListner, RecipeAdapter.onClickListner {
    var preferences: AppSharedPreferences? = null
    private lateinit var binding: FragmentDashboardBinding
    private var customeid = ""
    private var profileId = ""
    private var goalDataPoint: String? = ""
    private var fitDetailMessage: String? = ""
    private var fitMessage: String? = ""
    private val mBuyPlanAdapter: BuyPlanAdapter by lazy { BuyPlanAdapter(requireActivity()) }

    private val mBuyTestAdapter: BuyTestAdapter by lazy { BuyTestAdapter(requireActivity()) }
    private val mActionBoxAdapter: ActionBoxAdapter by lazy { ActionBoxAdapter(requireActivity()) }
    private val mTopBannerAdapter: TopBannerAdapter by lazy { TopBannerAdapter(requireActivity()) }
    private val mPromotionAdapter: PromotionAdapter by lazy { PromotionAdapter(requireActivity()) }
    private val mNewsFeedAdapter: NewsFeedAdapter by lazy { NewsFeedAdapter(requireActivity()) }
    private val mRecipeAdapter: RecipeAdapter by lazy { RecipeAdapter(requireActivity()) }
    private var mDb: AppDatabase? = null

    private val handler = Handler()
    private var handlerForFeedback: Handler? = Handler()
    private var mMedicineList = ArrayList<MedicineListResponse.Medicinelist.Data>()
    private var allMedicineList: List<Medicine> = ArrayList()
    private var mFoodList = ArrayList<FoodDiaryListResponse.Fooditemlist.Data>()
    private var allFoodList: List<FoodDiary> = ArrayList()
    private val REQUEST_OAUTH_REQUEST_CODE = 0x1001

    private val TAG = "DashboardFragment"
    private val type = ""
    private var stepCountLastValue = ""
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var healthPlanIdDoctor: String? = ""

    var checkedItemLike = -1
    var checkedItemImprove = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences = AppSharedPreferences.getInstance(requireActivity())

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        customeid = preferences!!.userId!!
        profileId = preferences!!.getprofileid()!!

        mDb = AppDatabase.getInstance(currentActivity!!)

        setAlias(requireContext(), preferences!!.userMobileNumber!!)
        setUserAttribute(currentActivity!!, "last_completed_call_date", Date())
        setUserAttribute(currentActivity!!, "call_count", 0)
        setUserAttribute(currentActivity!!, "subscription_status", "Non")
        setUserAttribute(currentActivity!!, "is_legacy", "No")
        setUserAttribute(currentActivity!!, "retention_status", "Active")
        setUserAttribute(currentActivity!!, "EducatorName", "")
        setUserAttribute(currentActivity!!, "TotalNoSpoken", 0)
        setUserAttribute(currentActivity!!, "TotalNoNotSpoken", 0)
        setUserAttribute(currentActivity!!, "LastCallDate", Date())

        getDashboardList()

        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvAction.layoutManager = layoutManager

        binding.rvTopBanner.adapter = mTopBannerAdapter
        binding.rvAction.adapter = mActionBoxAdapter
        binding.rvPromotion.adapter = mPromotionAdapter
        binding.rvBuyPlan.adapter = mBuyPlanAdapter
        binding.rvTest.adapter = mBuyTestAdapter
        binding.rvNesFeed.adapter = mNewsFeedAdapter
        binding.rvRecipes.adapter = mRecipeAdapter

        mActionBoxAdapter.setClickListner(this)
        mPromotionAdapter.setClickListner(this)
        mBuyPlanAdapter.setClickListner(this)
        mBuyTestAdapter.setClickListner(this)
        mNewsFeedAdapter.setClickListner(this)
        mRecipeAdapter.setClickListner(this)
        mTopBannerAdapter.setClickListner(this)

        binding.tvGoogleConnect.setOnClickListener {

            if (binding.tvGoogleConnect.text.toString() == "Install") {

                var launchIntent: Intent? = null
                try {
                    launchIntent =
                        currentActivity!!.packageManager.getLaunchIntentForPackage("com.google.android.apps.fitness")
                } catch (ignored: Exception) {
                }

                if (launchIntent == null) {
                    startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=" + "com.google.android.apps.fitness")))
                } else {
                    startActivity(launchIntent)
                }


            } else {
                val intent = Intent(currentActivity!!, StepGoalActivity::class.java)
                startActivity(intent)

                val properties = Properties()
                properties.addAttribute("isCLick", true)
                trackEvent(currentActivity!!, "ClickSteps", properties)
            }
        }

        binding.rlConnected.setOnClickListener {
            Intent(currentActivity!!, GoogleFitStepActivity::class.java).also {
                startActivity(it)
            }

            val properties = Properties()
            properties.addAttribute("isCLick", true)
            trackEvent(currentActivity!!, "googleFitConnected", properties)
        }

        binding.rlHealthScore.setOnClickListener {
            val intent = Intent(requireActivity(), AnalyticsReportActivity::class.java)
            startActivity(intent)

            val properties = Properties()
            properties.addAttribute("isCLickMyHealthScore", true)
            trackEvent(requireActivity(), "ClickMyHealthScoreHome", properties)
        }

        if (preferences!!.fullName != null && !TextUtils.isEmpty(preferences!!.fullName)) {
            binding.tvProfileName.visibility = View.VISIBLE
            binding.tvProfileName.text = "Hi " + preferences!!.fullName!!.split(" ")[0]
        } else {
            binding.tvProfileName.visibility = View.GONE
        }

        if (isConnection(currentActivity!!)) {
            ApiCall.instance.getMedicineList(customeid, profileId, 1, this)
        } else {
            if (currentActivity != null) {
                Toast.makeText(
                    currentActivity,
                    "please check your internet connection",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (isConnection(currentActivity!!)) {
            ApiCall.instance
                .getFoodListMaster(
                    AppSharedPreferences.getInstance(currentActivity)!!.deviceToken,
                    1,
                    this
                )
        } else {
            Toast.makeText(
                currentActivity,
                "please check your internet connection",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.imgNotification.setOnClickListener {
            val intent = Intent(currentActivity!!, NotificationListActivity::class.java)
            startActivity(intent)
        }

        binding.llDoctorProfile.setOnClickListener {
            val intent = Intent(currentActivity!!, DoctorConsultationActivity::class.java)
//            intent.putExtra("planId", healthPlanIdDoctor!!.toInt())
            intent.putExtra("planName", "Doctor consultation")
            startActivity(intent)
        }

        binding.imgBannerB2B.setOnClickListener {
            val intent = Intent(currentActivity!!, MyCarePlanActivity::class.java)
            startActivity(intent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (preferences!!.isFitConnect) {
                binding.llGoogleFitConnect.visibility = View.VISIBLE
                binding.rlConnect.visibility = View.GONE
                binding.rlConnected.visibility = View.VISIBLE

                requestPermissions(true)

            } else {
                binding.llGoogleFitConnect.visibility = View.VISIBLE
                binding.rlConnect.visibility = View.VISIBLE
                binding.rlConnected.visibility = View.GONE
                binding.tvGoogleFitTitle.text = "Please connect the step counter"

            }
        } else {
            binding.llGoogleFitConnect.visibility = View.GONE
        }

        if (preferences!!.sugarDialogCount < 3 && !preferences!!.sugarDialogPopup && !clickSugar) {
            dialogAddSugarValue()
        } else if (preferences!!.foodDiaryDialogCount < 3 && !preferences!!.foodDiaryDialogPopup && !clickFood) {
            dialogAddFoodDiaryValue()
        } else if (preferences!!.stepCounterDialogCount < 3 && !preferences!!.stepCountDialogPopup && !clickStepCounter) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                dialogStepCounterValue()
            } else {
                if (preferences!!.medicineDialogCount < 3 && !preferences!!.medicineDialogPopup && !clickMedicine) {
                    dialogAddMedicineValue()
                }
            }
        } else if (preferences!!.medicineDialogCount < 3 && !preferences!!.medicineDialogPopup && !clickMedicine) {
            dialogAddMedicineValue()
        } else {
            if (!preferences!!.feedbackDialogPopup) {
                preferences!!.isFeedbackDialogPopup(true)
                handlerForFeedback!!.postDelayed({
                    dialogFeedback()
                }, 3000)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        fitInstallOrNot()

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (preferences!!.isFitConnect) {
                binding.llGoogleFitConnect.visibility = View.VISIBLE
                binding.rlConnect.visibility = View.GONE
                binding.rlConnected.visibility = View.VISIBLE

                requestPermissions(true)

            } else {
                binding.llGoogleFitConnect.visibility = View.VISIBLE
                binding.rlConnect.visibility = View.VISIBLE
                binding.rlConnected.visibility = View.GONE
                binding.tvGoogleFitTitle.text = "Please connect the step counter"

            }
        } else {
            binding.llGoogleFitConnect.visibility = View.GONE
        }*/
    }

    private fun getDashboardList() {
        if (isConnection(requireContext())) {
            FunctionHelper.disable_user_Intration(
                currentActivity!!,
                currentActivity!!.resources.getString(R.string.loading)
            )
            ApiCall.instance.getDashboardData(profileId, this)
        } else {
            Toast.makeText(
                currentActivity!!,
                "please check your internet connection",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun dialogAddSugarValue() {
        val properties = Properties()
        properties.addAttribute("isOpen", true)
        trackEvent(currentActivity!!, "tooltipAddSugar", properties)
        val dialog = Dialog(currentActivity!!, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        val layoutParams = dialog.window!!.attributes
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT

        dialog.setContentView(R.layout.dialog_sugar_value_tooltip)
        val tvOk = dialog.findViewById(R.id.tvOk) as TextView
        val imgClose = dialog.findViewById(R.id.imgClose) as ImageView
        tvOk.setOnClickListener {
            try {
                if (dialog != null && dialog.isShowing) {
                    dialog.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (currentActivity!! != null && isAdded) {
                val intent = Intent(currentActivity!!, AddSugarActivity::class.java)
                startActivity(intent)
            }
            clickSugar = true

            val properties = Properties()
            properties.addAttribute("isCLick", true)
            properties.addAttribute("isSkip", false)
            trackEvent(currentActivity!!, "tooltipAddSugar", properties)

            val bundle = Bundle()
            bundle.putBoolean("isCLick", true)
            bundle.putBoolean("isSkip", false)
            firebaseAnalytics!!.logEvent("tooltipAddSugar", bundle)
            firebaseAnalytics!!.setDefaultEventParameters(bundle)
        }

        imgClose.setOnClickListener {
            try {
                if (dialog != null && dialog.isShowing) {
                    dialog.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            clickSugar = true
            preferences!!.sugarDialogCount = preferences!!.sugarDialogCount + 1

            val properties = Properties()
            properties.addAttribute("isCLick", true)
            properties.addAttribute("isSkip", true)
            trackEvent(currentActivity!!, "tooltipAddSugar", properties)
            skipDialog()
        }
        if (!dialog?.isShowing!!) {
            dialog.show()
        }
    }

    private fun dialogAddFoodDiaryValue() {
        val properties = Properties()
        properties.addAttribute("isOpen", true)
        trackEvent(currentActivity!!, "tooltipAddFoodDiary", properties)
        val dialog = Dialog(currentActivity!!, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        val layoutParams = dialog.window!!.attributes
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog.setContentView(R.layout.dialog_food_diary_tooltip)
        val tvOk = dialog.findViewById(R.id.tvOk) as TextView
        val imgClose = dialog.findViewById(R.id.imgClose) as ImageView
        tvOk.setOnClickListener {
            try {
                if (dialog != null && dialog.isShowing) {
                    dialog.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (currentActivity!! != null && isAdded) {
                val intent = Intent(currentActivity!!, FoodDiaryActivity::class.java)
                intent.putExtra("navigationType", "viewFoodDiary")
                startActivity(intent)
            }

            clickFood = true

            val properties = Properties()
            properties.addAttribute("isCLick", true)
            properties.addAttribute("isSkip", false)
            trackEvent(currentActivity!!, "tooltipAddFoodDiary", properties)

            val bundle = Bundle()
            bundle.putBoolean("isCLick", true)
            bundle.putBoolean("isSkip", false)
            firebaseAnalytics!!.logEvent("tooltipAddFoodDiary", bundle)
            firebaseAnalytics!!.setDefaultEventParameters(bundle)

        }

        imgClose.setOnClickListener {
            try {
                if (dialog != null && dialog.isShowing) {
                    dialog.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            clickFood = true
            preferences!!.foodDiaryDialogCount = preferences!!.foodDiaryDialogCount + 1
            skipDialog()

            val properties = Properties()
            properties.addAttribute("isCLick", true)
            properties.addAttribute("isSkip", true)
            trackEvent(currentActivity!!, "tooltipAddFoodDiary", properties)
        }
        if (!dialog?.isShowing!!) {
            dialog.show()
        }
    }

    private fun dialogStepCounterValue() {
        val properties = Properties()
        properties.addAttribute("isOpen", true)
        trackEvent(currentActivity!!, "tooltipStepCounter", properties)
        val dialog = Dialog(currentActivity!!, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        val layoutParams = dialog.window!!.attributes
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        val window: Window? = dialog.window
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setGravity(Gravity.TOP)
        dialog.setContentView(R.layout.dialog_step_counter_tooltip)

        val tvOk = dialog.findViewById(R.id.tvOk) as TextView
        val imgClose = dialog.findViewById(R.id.imgClose) as ImageView
        tvOk.setOnClickListener {
            try {
                if (dialog != null && dialog.isShowing) {
                    dialog.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (currentActivity!! != null && isAdded) {
                val intent = Intent(currentActivity!!, StepGoalActivity::class.java)
                startActivity(intent)
            }
            clickStepCounter = true
            val properties = Properties()
            properties.addAttribute("isCLick", true)
            properties.addAttribute("isSkip", false)
            trackEvent(currentActivity!!, "tooltipStepCounter", properties)
        }

        imgClose.setOnClickListener {
            try {
                if (dialog != null && dialog.isShowing) {
                    dialog.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            clickStepCounter = true
            preferences!!.stepCounterDialogCount = preferences!!.stepCounterDialogCount + 1

            val properties = Properties()
            properties.addAttribute("isCLick", true)
            properties.addAttribute("isSkip", true)
            trackEvent(currentActivity!!, "tooltipStepCounter", properties)
            skipDialog()
        }
        if (!dialog?.isShowing!!) {
            dialog.show()
        }
    }

    private fun dialogAddMedicineValue() {
        val properties = Properties()
        properties.addAttribute("isOpen", true)
        trackEvent(currentActivity!!, "tooltipAddMedicine", properties)
        val dialog = Dialog(currentActivity!!, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        val layoutParams = dialog.window!!.attributes
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog.setContentView(R.layout.dialog_medicine_tooltip)
        val tvOk = dialog.findViewById(R.id.tvOk) as TextView
        val imgClose = dialog.findViewById(R.id.imgClose) as ImageView
        tvOk.setOnClickListener {
            try {
                if (dialog != null && dialog.isShowing) {
                    dialog.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (currentActivity!! != null && isAdded) {
                val intent = Intent(currentActivity!!, MedicineListActivity::class.java)
                intent.putExtra("navigationType", "medicines")
                startActivity(intent)
            }
            clickMedicine = true

            val properties = Properties()
            properties.addAttribute("isCLick", true)
            properties.addAttribute("isSkip", false)
            trackEvent(currentActivity!!, "tooltipAddMedicine", properties)
        }

        imgClose.setOnClickListener {
            if (dialog != null && dialog.isShowing) {

                try {
                    dialog.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (!preferences!!.feedbackDialogPopup) {
                    preferences!!.isFeedbackDialogPopup(true)
                    handlerForFeedback!!.postDelayed({
                        dialogFeedback()
                    }, 3000)
                }
            }
            clickMedicine = true
            preferences!!.medicineDialogCount = preferences!!.medicineDialogCount + 1
            val properties = Properties()
            properties.addAttribute("isCLick", true)
            properties.addAttribute("isSkip", true)
            trackEvent(currentActivity!!, "tooltipAddMedicine", properties)
        }
        if (!dialog?.isShowing!!) {
            dialog.show()
        }
    }

    private fun dialogFeedback() {
        val properties = Properties()
        properties.addAttribute("isOpen", true)
        trackEvent(currentActivity!!, "feedbackPopup", properties)

        var optionList =
            arrayOf("Food Diary", "Sugar Tracking", "Step Counter", "Medicine Alarm", "Others")

        val dialog = Dialog(currentActivity!!, R.style.DialogSlideAnim)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window: Window? = dialog.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.BOTTOM
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        window.attributes = wlp

        dialog.setContentView(R.layout.dialog_feedback)

        val ratingBar = dialog.findViewById(R.id.ratingBar) as AppCompatRatingBar
        val tvSelectLike = dialog.findViewById(R.id.tvSelectOneLike) as TextView
        val tvSelectImprove = dialog.findViewById(R.id.tvSelectOneImprove) as TextView
        val edtAddNot = dialog.findViewById(R.id.edtAddNot) as EditText
        val tvSubmit = dialog.findViewById(R.id.tvSubmit) as TextView

        tvSelectLike.setOnClickListener {
            val alertDialog = AlertDialog.Builder(currentActivity!!)
            alertDialog.setTitle("Choose an option")
            alertDialog.setSingleChoiceItems(
                optionList,
                checkedItemLike
            ) { dialog: DialogInterface, which: Int ->
                checkedItemLike = which
                tvSelectLike.text = optionList[which]
                try {
                    dialog.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            alertDialog.setNegativeButton(
                "Cancel"
            ) { dialog: DialogInterface?, which: Int -> }
            val customAlertDialog = alertDialog.create()
            customAlertDialog.show()
        }

        tvSelectImprove.setOnClickListener {
            val alertDialog = AlertDialog.Builder(currentActivity!!)
            alertDialog.setTitle("Choose an option")
            alertDialog.setSingleChoiceItems(
                optionList,
                checkedItemImprove
            ) { dialog: DialogInterface, which: Int ->
                checkedItemImprove = which
                tvSelectImprove.text = optionList[which]
                try {
                    dialog.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            alertDialog.setNegativeButton(
                "Cancel"
            ) { dialog: DialogInterface?, which: Int -> }
            val customAlertDialog = alertDialog.create()
            customAlertDialog.show()
        }

        tvSubmit.setOnClickListener {
            if (!TextUtils.isEmpty(tvSelectImprove.text.toString())) {
                if (!TextUtils.isEmpty(tvSelectImprove.text.toString())) {
                    if (isConnection(currentActivity!!)) {
                        try {
                            if (dialog != null && dialog.isShowing) {
                                dialog.dismiss()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        ApiCall.instance.getFeedbackRequest(
                            ratingBar.rating.toString(),
                            tvSelectLike.text.toString(),
                            tvSelectImprove.text.toString(),
                            edtAddNot.text.toString().trim(),
                            this
                        )
                    } else {
                        Toast.makeText(
                            currentActivity!!,
                            "please check your internet connection",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        currentActivity!!,
                        "please select at least one service improvement",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    currentActivity!!,
                    "please select at least one service",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (currentActivity!! != null) {
            if (!(currentActivity as Activity).isFinishing) {
                //show dialog
                if (!dialog?.isShowing!!) {
                    try {
                        dialog.show()
                    } catch (e: WindowManager.BadTokenException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    }

    private fun skipDialog() {

        if (preferences!!.sugarDialogCount < 3 && !preferences!!.sugarDialogPopup && !clickSugar) {
            dialogAddSugarValue()
        } else if (preferences!!.foodDiaryDialogCount < 3 && !preferences!!.foodDiaryDialogPopup && !clickFood) {
            dialogAddFoodDiaryValue()
        } else if (preferences!!.stepCounterDialogCount < 3 && !preferences!!.stepCountDialogPopup && !clickStepCounter) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                dialogStepCounterValue()
            } else {
                if (preferences!!.medicineDialogCount < 3 && !preferences!!.medicineDialogPopup && !clickMedicine) {
                    dialogAddMedicineValue()
                }
            }
        } else if (preferences!!.medicineDialogCount < 3 && !preferences!!.medicineDialogPopup && !clickMedicine) {
            dialogAddMedicineValue()
        }

    }

    override fun onSelectAction(action: String?) {
        if (action == "Add Sugar") {
            val properties = Properties()
            properties.addAttribute("isClick", true)
            trackEvent(currentActivity!!, "ClickSugarButton", properties)
            val intent = Intent(currentActivity!!, AddSugarActivity::class.java)
            startActivity(intent)
        } else if (action == "Food Diary") {
            val properties = Properties()
            properties.addAttribute("isClick", true)
            trackEvent(currentActivity!!, "ClickFoodItemButton", properties)
            callApiForCheckFoodDiaryList()
        }
    }

    override fun onSelectTopBanner(planId: String?, type: String?, imageLink: String?) {
            if (!TextUtils.isEmpty(type) && type == "health_plan") {
            val intent = Intent(currentActivity!!, PlanDetailActivity::class.java)
            intent.putExtra("planId", planId!!.toInt())
            startActivity(intent)

            val properties = Properties()
            properties.addAttribute("isCLick", true)
            properties.addAttribute("bannerId", planId)
            trackEvent(currentActivity!!, "topBanner", properties)
        } else if (!TextUtils.isEmpty(type) && type == "diet_plan_new") {

            val intent = Intent(currentActivity!!, DietChartActivity::class.java)
            startActivity(intent)

            val properties = Properties()
            properties.addAttribute("isCLick", true)
            trackEvent(currentActivity!!, "topBannerDietPlanNew", properties)
        } else if (!TextUtils.isEmpty(type) && type == "diet_plan") {

            val properties = Properties()
            properties.addAttribute("isCLick", true)
            trackEvent(currentActivity!!, "topBannerDietPlan", properties)

            val intent = Intent(currentActivity!!, WebViewActivity::class.java)
            intent.putExtra("navType", "Home")
            intent.putExtra("docUrl", imageLink)
            intent.putExtra("docName", "")
            startActivity(intent)
        } else {
            val intent = Intent(currentActivity!!, PlanDetailActivity::class.java)
            intent.putExtra("planId", planId)
            startActivity(intent)

            val properties = Properties()
            properties.addAttribute("isCLick", true)
            properties.addAttribute("bannerId", planId!!.toInt())
            trackEvent(currentActivity!!, "topBanner", properties)
        }
    }

    override fun onSelectPlanAction(planId: Int?, type: String?) {

        val properties = Properties()
        properties.addAttribute("isCLick", true)
        properties.addAttribute("planType", type)
        properties.addAttribute("planId", planId)
        trackEvent(currentActivity!!, "buyPlanDashboard", properties)

        when (type) {
            "doctor_consultation" -> {
                val intent = Intent(currentActivity!!, DoctorConsultationActivity::class.java)
                intent.putExtra("planId", planId)
                startActivity(intent)
            }

            "educator_consultation" -> {
                val intent = Intent(currentActivity!!, DietPlanDetailActivity::class.java)
                intent.putExtra("planId", planId)
                startActivity(intent)
            }

            else -> {
                val intent = Intent(currentActivity!!, PlanDetailActivity::class.java)
                intent.putExtra("planId", planId)
                startActivity(intent)
            }
        }
    }

    override fun onSelectTestAction(planId: Int?) {
        val intent = Intent(currentActivity!!, TestBookActivity::class.java)
        intent.putExtra("planId", planId)
        startActivity(intent)

        val properties = Properties()
        properties.addAttribute("isClick", true)
        properties.addAttribute("planId", planId)
        trackEvent(currentActivity!!, "ClickBuyTestButton", properties)
    }

    override fun onSelectPromotionAction(action: String?) {
        if (action == "addMedicine") {
            val properties = Properties()
            properties.addAttribute("isClick", true)
            trackEvent(currentActivity!!, "ClickMedicineButton", properties)
            callApiForCheckMedicineList()
        } else {
            val properties = Properties()
            properties.addAttribute("isCLick", true)
            trackEvent(currentActivity!!, "ClickDiscountButton", properties)
            callApiForDiscountCheck()
        }
    }

    override fun onSelectBlogAction(id: String?, type: String?, url: String?, heading: String?) {
        val intent = Intent(currentActivity!!, BlogDetailActivity::class.java)
        intent.putExtra("navType", type)
        intent.putExtra("blogId", id)
        startActivity(intent)

        val parameters = Bundle().apply {
            this.putString("BlogName", heading)
            this.putInt("BlogId", id!!.toInt())
        }
        firebaseAnalytics.setDefaultEventParameters(parameters)
    }

    override fun onSelectRecipesAction(id: String?, type: String?, url: String?, heading: String?) {
        val intent = Intent(currentActivity!!, BlogDetailActivity::class.java)
        intent.putExtra("navType", type)
        intent.putExtra("blogId", id)
        startActivity(intent)

        val parameters = Bundle().apply {
            this.putString("RecipeName", heading)
            this.putInt("RecipeId", id!!.toInt())
        }
        firebaseAnalytics.setDefaultEventParameters(parameters)
    }

    private fun callApiForCheckFoodDiaryList() {
        if (isConnection(currentActivity!!)) {
            ApiCall.instance
                .getMyFoodDiaryList(preferences!!.getprofileid(), getCurrentDate(), this)
        } else {
            val intent = Intent(activity, FoodDiaryActivity::class.java)
            intent.putExtra("navigationType", "addFoodDiary")
            startActivity(intent)
        }
    }

    private fun callApiForCheckMedicineList() {
        if (isConnection(currentActivity!!)) {
            ApiCall.instance.getMyMedicineList(preferences!!.getprofileid(), this)
        } else {
            val intent = Intent(activity, MedicineListActivity::class.java)
            intent.putExtra("navigationType", "medicines")
            startActivity(intent)
        }
    }

    private fun callApiForDiscountCheck() {
        if (isConnection(currentActivity!!)) {
            ApiCall.instance.getDiscountCheck(preferences!!.getprofileid(), this)
        }
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "saveStepList") {
            val response = data as Response<CommonResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {
//                    Toast.makeText(currentActivity!!,"Steps uploaded ",Toast.LENGTH_SHORT).show()
                }
            }
        }
        if (type == "callFeedbackRequest") {
            val response = data as Response<CommonResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "1") {

                    val properties = Properties()
                    properties.addAttribute("isOpen", true)
                    properties.addAttribute("isSubmit", true)
                    trackEvent(currentActivity!!, "feedbackPopup", properties)

                    preferences!!.isFeedbackDialogPopup(true)
                    Toast.makeText(
                        currentActivity!!,
                        response.body()!!.errorMsg,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }

        if (type == "checkDiscount") {
            val response = data as Response<CommonResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {
                    val intent = Intent(currentActivity!!, CongratulationActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        if (type == "trackSteps") {
            val response = data as Response<StepCountResponse>
            if (response.isSuccessful && response.body()!!.errorCode == "0") {
                if (response.body()!!.data != null && response.body()!!.data.isNotEmpty()) {
                    stepCountLastValue = response.body()!!.data[0].stepcount
                }
            }
        }
        if (type == "getDashboardList") {
            val response = data as Response<DashboardResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0" && response.body()!!.data != null && response.body()!!.data.size > 0) {

                    binding.llMain.visibility = View.VISIBLE

                    if (response.body()!!.data[0].profile_type != null && response.body()!!.data[0].profile_type == "B2B") {
                        binding.imgBannerB2B.visibility = View.VISIBLE
                        binding.llDoctorProfile.visibility = View.GONE

                        if (!TextUtils.isEmpty(response.body()!!.data[0].top_b2b_banner)) {
                            Glide.with(currentActivity!!)
                                .load(response.body()!!.data[0].top_b2b_banner)
                                .placeholder(R.drawable.progress_animation).into(binding.imgBannerB2B!!)
                        }
                    }else{
                        binding.imgBannerB2B.visibility = View.GONE
                        binding.llDoctorProfile.visibility = View.VISIBLE
                    }

                    if (response.body()!!.data[0].profile_info != null) {
                        preferences!!.fullName = response.body()!!.data[0].profile_info.name
                        binding.tvProfileName.text = "Hi " + preferences!!.fullName!!.split(" ")[0]
                    }

                    binding.tvHealthScoreTxt.text = response.body()!!.data[0].analytics_message
                    binding.tvHealthScore.text = response.body()!!.data[0].analytics_data

                    if (response.body()!!.data[0].doctor != null && response.body()!!.data[0].doctor.size > 0) {

                        healthPlanIdDoctor = response.body()!!.data[0].doctor[0].doctor_id
                        binding.tvDrName.text = response.body()!!.data[0].doctor[0].doctor_name
                        binding.tvDrSpecialist.text =
                            response.body()!!.data[0].doctor[0].doctor_qualification
                        binding.tvDrAddress.text =
                            response.body()!!.data[0].doctor[0].doctor_address

                        if (!TextUtils.isEmpty(response.body()!!.data[0].doctor[0].profile_picture)) {
                            Glide.with(currentActivity!!)
                                .load(response.body()!!.data[0].doctor[0].profile_picture)
                                .placeholder(R.drawable.progress_animation).into(binding.imgDr!!)
                        } else {
                            Glide.with(currentActivity!!)
                                .load("https://oxyjon.s3.ap-south-1.amazonaws.com/app_up_banners/Doctor_gender_neutral.jpg")
                                .placeholder(R.drawable.progress_animation).into(binding.imgDr!!)
                        }
                    }

                    if (response.body()!!.data[0].google_fit != null) {
                        goalDataPoint = response.body()!!.data[0].google_fit.goal_data_point
                        fitMessage = response.body()!!.data[0].google_fit.message
                        fitDetailMessage = response.body()!!.data[0].google_fit.detail_message
                        if (preferences!!.isFitConnect) {
                            binding.tvGoogleFitTitle.text = fitMessage
                            binding.tvConnectedFitDesc.text = fitDetailMessage
                        }

                    }

                    if (response.body()!!.data[0].top_banner_list != null && response.body()!!.data[0].top_banner_list.isNotEmpty()) {
                        binding.rvTopBanner.visibility = View.VISIBLE
                        mTopBannerAdapter.setData(response.body()!!.data[0].top_banner_list)
                    } else {
                        binding.rvTopBanner.visibility = View.GONE
                    }
                    if (response.body()!!.data[0].recipes != null && response.body()!!.data[0].recipes.isNotEmpty()) {
                        binding.llRecipes.visibility = View.VISIBLE
                        mRecipeAdapter.setData(response.body()!!.data[0].recipes)
                    } else {
                        binding.llRecipes.visibility = View.GONE
                    }

                    if (response.body()!!.data[0].promotion_block != null && response.body()!!.data[0].promotion_block.isNotEmpty()) {
                        binding.rvPromotion.visibility = View.VISIBLE
                        mPromotionAdapter.setData(response.body()!!.data[0].promotion_block)
                    } else {
                        binding.rvPromotion.visibility = View.GONE
                    }
                    if (response.body()!!.data[0].action_box != null && response.body()!!.data[0].action_box.isNotEmpty()) {
                        binding.llAction.visibility = View.VISIBLE
                        mActionBoxAdapter.setData(response.body()!!.data[0].action_box)
                    } else {
                        binding.llAction.visibility = View.GONE
                    }

                    if (response.body()!!.data[0].healthplan != null && response.body()!!.data[0].healthplan.isNotEmpty()) {
                        binding.llByPlan.visibility = View.VISIBLE
                        mBuyPlanAdapter.setData(response.body()!!.data[0].healthplan)
                    } else {
                        binding.llByPlan.visibility = View.GONE
                    }

                    if (response.body()!!.data[0].bloodtestplan != null && response.body()!!.data[0].bloodtestplan.isNotEmpty()) {
                        binding.llTest.visibility = View.GONE
                        mBuyTestAdapter.setData(response.body()!!.data[0].bloodtestplan)
                    } else {
                        binding.llTest.visibility = View.GONE
                    }

                    if (response.body()!!.data[0].newsfeed != null && response.body()!!.data[0].newsfeed.isNotEmpty()) {
                        binding.llBlog.visibility = View.VISIBLE
                        mNewsFeedAdapter.setData(response.body()!!.data[0].newsfeed)
                    } else {
                        binding.llBlog.visibility = View.GONE
                    }

                } else {
                    binding.llMain.visibility = View.GONE
                    Toast.makeText(context, response.body()!!.errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
        if (type == "myMedicine") {
            val response = data as Response<MyMedicineResponse>
            if (response.isSuccessful) {
                if (response.isSuccessful && response.body()!!.errorCode == "1") {
                    if (response.body()!!.data != null && response.body()!!.data.size > 0) {
                        val intent = Intent(currentActivity!!, MedicineListActivity::class.java)
                        intent.putExtra("navigationType", "viewMedicines")
                        startActivity(intent)
                    } else {
                        val intent = Intent(currentActivity!!, MedicineListActivity::class.java)
                        intent.putExtra("navigationType", "medicines")
                        startActivity(intent)
                    }
                } else {
                    val intent = Intent(currentActivity!!, MedicineListActivity::class.java)
                    intent.putExtra("navigationType", "medicines")
                    startActivity(intent)
                }
            }
        }
        if (type == "myFoodDiaryList") {
            val response = data as Response<GetMyFoodDiaryResponse>
            if (response.isSuccessful) {
                if (response.isSuccessful && response.body()!!.errorCode == "1") {
                    if (response.body()!!.data != null && response.body()!!.data!!.size > 0) {
                        if (currentActivity!! != null && isAdded) {
                            val intent = Intent(currentActivity!!, FoodDiaryActivity::class.java)
                            intent.putExtra("navigationType", "viewFoodDiary")
                            startActivity(intent)
                        }

                    } else {
                        if (currentActivity!! != null && isAdded) {
                            val intent = Intent(currentActivity!!, FoodDiaryActivity::class.java)
                            intent.putExtra("navigationType", "addFoodDiary")
                            startActivity(intent)
                        }
                    }
                } else {
                    if (currentActivity!! != null && isAdded) {
                        val intent = Intent(currentActivity!!, FoodDiaryActivity::class.java)
                        intent.putExtra("navigationType", "addFoodDiary")
                        startActivity(intent)
                    }
                }
            }
        }
        if (type == "medicineListMaster") {
            val response = data as Response<MedicineListResponse>
            if (response.isSuccessful && response.body()!!.errorCode == "0") {
                if (response.body()!!.medicinelist_action != null && response.body()!!.medicinelist_action.update_data.equals(
                        "True",
                        ignoreCase = true
                    )
                ) {
                    if (response.body()!!.medicinelist != null) {
                        val currentPage = response.body()!!.medicinelist.current_page
                        val lastPage = response.body()!!.medicinelist.last_page

                        if (mMedicineList != null && mMedicineList.size > 0) {
                            mMedicineList.clear()
                        }
                        mMedicineList = response.body()!!.medicinelist.data

//                        currentActivity!!.startService(Intent(currentActivity!!, MyService::class.java).putExtra("medicine",mMedicineList))

                        AppExecutors.instance!!.diskIO().execute {
                            currentActivity!!.runOnUiThread {
                                for ((created_at, id, medicine_category, medicine_form, medicine_name) in mMedicineList) {
                                    val medicine = Medicine(
                                        id,
                                        medicine_name,
                                        medicine_form,
                                        medicine_category,
                                        created_at
                                    )
                                    mDb!!.personDao().insertMedicine(medicine)
                                }
                            }
                        }

                        if (currentPage != lastPage) {
                            if (isConnection(currentActivity!!)) {
                                ApiCall.instance
                                    .getMedicineList(customeid, profileId, currentPage + 1, this)
                            } else {
                                Toast.makeText(
                                    currentActivity,
                                    "please check your internet connection",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (currentPage == lastPage) {
                            if (isConnection(currentActivity!!)) {
                                ApiCall.instance.medicineSyncComplete(
                                    customeid,
                                    profileId,
                                    response.body()!!.medicinelist_action.sync_id,
                                    this
                                )
                            } else {
                                Toast.makeText(
                                    currentActivity,
                                    "please check your internet connection",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else if (response.body()!!.medicinelist_action != null && response.body()!!.medicinelist_action.update_data.equals(
                        "False",
                        ignoreCase = true
                    )
                ) {
                    AppExecutors.instance!!.diskIO().execute {
                        allMedicineList = mDb!!.personDao().allMedicine as List<Medicine>
                    }

                    if (allMedicineList != null && allMedicineList.isNotEmpty() && allMedicineList.size >= response.body()!!.medicinelist.total) {
                        Log.e("Page size is: ", allMedicineList.size.toString())
                    } else {
                        if (response.body()!!.medicinelist != null) {
                            val currentPage = response.body()!!.medicinelist.current_page
                            val lastPage = response.body()!!.medicinelist.last_page
                            if (mMedicineList != null && mMedicineList.size > 0) {
                                mMedicineList.clear()
                            }
                            mMedicineList = response.body()!!.medicinelist.data

//                            currentActivity!!.startService(Intent(currentActivity!!, MyService::class.java).putExtra("medicine",mMedicineList))

                            AppExecutors.instance!!.diskIO().execute {
                                currentActivity!!.runOnUiThread {
                                    for ((created_at, id, medicine_category, medicine_form, medicine_name) in mMedicineList) {
                                        val medicine = Medicine(
                                            id,
                                            medicine_name,
                                            medicine_form,
                                            medicine_category,
                                            created_at
                                        )
                                        mDb!!.personDao().insertMedicine(medicine)

                                    }
                                }
                            }
                            if (currentPage != lastPage) {
                                if (isConnection(currentActivity!!)) {
                                    ApiCall.instance.getMedicineList(
                                        customeid,
                                        profileId,
                                        currentPage + 1,
                                        this
                                    )
                                } else {
                                    Toast.makeText(
                                        currentActivity,
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

        if (type == "foodListMaster") {
            val response = data as Response<FoodDiaryListResponse?>?
            if (response?.body() != null && response.isSuccessful && response.body()!!.errorCode == "0") {
                if (response.body()!!.foodupdate_action != null && response.body()!!.foodupdate_action.update_data.equals(
                        "True",
                        ignoreCase = true
                    )
                ) {
                    if (response.body()!!.fooditemlist != null) {
                        AppExecutors.instance!!.diskIO().execute {
                            allFoodList = mDb!!.personDao().allFoodDiary as List<FoodDiary>
                        }
                        if (allFoodList != null && allFoodList.isNotEmpty() && allFoodList.size >= response.body()!!.fooditemlist.total) {
                            Log.e("Food Page size match: ", allFoodList.size.toString())
                        } else {
                            val currentPage = response.body()!!.fooditemlist.current_page
                            val lastPage = response.body()!!.fooditemlist.last_page
                            if (mFoodList != null && mFoodList.size > 0) {
                                mFoodList.clear()
                            }
                            mFoodList = response.body()!!.fooditemlist.data
                            AppExecutors.instance!!.diskIO().execute {
                                currentActivity!!.runOnUiThread { //                                        mDb.personDao().deleteFoodDiary();
                                    for ((calorie_gm, carbs_gm, cuisine_type, fats_gm, fiber_gm, food_item_name, food_type, id, meal_bed_time, meal_breakfast, meal_dinner, meal_early_morning, meal_evening_snack, meal_lunch, meal_morning_snack, measurement_unit, protein_gm, quantity_primary, quantity_unit_primary, quantity_secondary, quantity_unit_secondary) in mFoodList) {
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
                                if (isConnection(currentActivity!!)) {
                                    ApiCall.instance
                                        .getFoodListMaster(
                                            AppSharedPreferences.getInstance(
                                                currentActivity
                                            )!!.deviceToken,
                                            currentPage + 1,
                                            this
                                        )
                                } else {
                                    Toast.makeText(
                                        currentActivity,
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
                    }
                } else if (response.body()!!.foodupdate_action != null && response.body()!!.foodupdate_action.update_data.equals(
                        "False",
                        ignoreCase = true
                    )
                ) {
                    AppExecutors.instance!!.diskIO().execute {
                        allFoodList = mDb!!.personDao().allFoodDiary as List<FoodDiary>
                    }
                    if (allFoodList != null && allFoodList.isNotEmpty() && allFoodList.size >= response.body()!!.fooditemlist.total) {
                        Log.e("Food Page size is: ", allFoodList.size.toString())
                    } else {
                        if (response.body()!!.fooditemlist != null) {
                            val currentPage = response.body()!!.fooditemlist.current_page
                            val lastPage = response.body()!!.fooditemlist.last_page
                            if (mFoodList != null && mFoodList.size > 0) {
                                mFoodList.clear()
                            }
                            mFoodList = response.body()!!.fooditemlist.data
                            AppExecutors.instance!!.diskIO().execute {
                                currentActivity!!.runOnUiThread {
                                    for ((calorie_gm, carbs_gm, cuisine_type, fats_gm, fiber_gm, food_item_name, food_type, id, meal_bed_time, meal_breakfast, meal_dinner, meal_early_morning, meal_evening_snack, meal_lunch, meal_morning_snack, measurement_unit, protein_gm, quantity_primary, quantity_unit_primary, quantity_secondary, quantity_unit_secondary) in mFoodList) {
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
                                if (isConnection(currentActivity!!)) {
                                    ApiCall.instance
                                        .getFoodListMaster(
                                            AppSharedPreferences.getInstance(
                                                currentActivity
                                            )!!.deviceToken,
                                            currentPage + 1,
                                            this
                                        )
                                } else {
                                    Toast.makeText(
                                        currentActivity,
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
        FunctionHelper.enableUserIntraction()
    }

    private fun getCurrentDate(): String? {
        val yearFormat = SimpleDateFormat("yyyy-MM-dd")
        val d = Date(System.currentTimeMillis())
        return yearFormat.format(d)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                Log.i(TAG, "Fitness permission granted")
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        subscribeStepCount()
                        readStepCountDelta() // Read today's data
                        handler.postDelayed(this, 5000)
                    }
                }, 100)
                binding.llGoogleFitConnect.visibility = View.VISIBLE
                binding.rlConnect.visibility = View.GONE
                binding.rlConnected.visibility = View.VISIBLE

                preferences!!.isFitConnect = true

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    readHistoricStepCount()
                }

            } else if (requestCode == 101) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    binding.llGoogleFitConnect.visibility = View.VISIBLE
                    binding.rlConnect.visibility = View.VISIBLE
                    binding.rlConnected.visibility = View.GONE
                    requestPermissions(true)
                } else {
                    binding.llGoogleFitConnect.visibility = View.GONE
                }
            }
        } else {
            Log.i(TAG, "Fitness permission denied")
        }
    }

    private fun checkPermission(): Boolean {
        return if (PermissionUtil.verifyPermissions(
                currentActivity,
                PermissionUtil.readFitnessPermissions()
            )
        ) {
            true
        } else {
            PermissionUtil.requestPermission(
                PermissionUtil.readFitnessPermissions(),
                currentActivity
            )
            false
        }
    }

    private fun hasFitPermission(): Boolean {
        // Request permission to collect Google Fit data
        val fitnessOptions: FitnessOptions = getFitnessSignInOptions()!!
        return GoogleSignIn.hasPermissions(
            GoogleSignIn.getLastSignedInAccount(currentActivity!!),
            fitnessOptions
        )
    }

    private fun getFitnessSignInOptions(): FitnessOptions? {
        // Request access to step count data from Fit history
        return FitnessOptions.builder().addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .addDataType(DataType.TYPE_CALORIES_EXPENDED)
            .addDataType(DataType.TYPE_BASAL_METABOLIC_RATE)
            .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_WRITE).build()
    }

    private fun requestFitnessPermission() {
        GoogleSignIn.requestPermissions(
            this,
            REQUEST_OAUTH_REQUEST_CODE,
            GoogleSignIn.getLastSignedInAccount(currentActivity!!), getFitnessSignInOptions()!!
        )
    }

    private fun subscribeStepCount() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.getRecordingClient(
            currentActivity!!, GoogleSignIn.getLastSignedInAccount(
                currentActivity!!
            )!!
        ).subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
        Fitness.getRecordingClient(
            currentActivity!!, GoogleSignIn.getLastSignedInAccount(
                currentActivity!!
            )!!
        ).subscribe(DataType.TYPE_CALORIES_EXPENDED)
    }

    private fun requestPermissions(isBack: Boolean) {
        // below line is use to request
        // permission in the current activity.
        Dexter.withContext(currentActivity) // below line is use to request the number of
            // permissions which are required in our app.
            .withPermissions(
                Manifest.permission.ACTIVITY_RECOGNITION,
                Manifest.permission.ACCESS_COARSE_LOCATION,  // below is the list of permissions
                Manifest.permission.ACCESS_FINE_LOCATION
            ) // after adding permissions we are
            // calling an with listener method.
            .withListener(object : MultiplePermissionsListener {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        val bundle = MainApplication.currentActivity!!.intent.extras
                        if (bundle != null || type != null && !TextUtils.isEmpty(type) && type.equals(
                                "StepCounter",
                                ignoreCase = true
                            )
                        ) {
                            if (!TextUtils.isEmpty(bundle!!.getString("navigationType")) && bundle.getString(
                                    "navigationType"
                                ).equals(
                                    "addMedicine",
                                    ignoreCase = true
                                ) || type != null && !TextUtils.isEmpty(type) && type.equals(
                                    "StepCounter",
                                    ignoreCase = true
                                )
                            ) {
                                getGoogleFitDataWithSignIn()
                            } else {
                                if (isBack) {
                                    getGoogleFitDataWithSignIn()
                                } else {
                                    getGoogleFitData()
                                }
                            }
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                if (isBack) {
                                    getGoogleFitDataWithSignIn()
                                } else {
                                    getGoogleFitData()
                                }
                            } else {
                                binding.llGoogleFitConnect.visibility = View.GONE
                                preferences!!.isFitConnect = false
                            }
                        }
                    } else if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        // permission is denied permanently, we will show user a dialog message.
                        if (isLocationEnabled()) {
                            showSettingsDialog()
                        } else {
                            val builder = AlertDialog.Builder(currentActivity)
                            // below line is the title
                            // for our alert dialog.
                            builder.setTitle("Enable Location")
                            // below line is our message for our dialog
                            builder.setMessage("You need to enable location.")
                            builder.setPositiveButton(
                                "GOTO SETTINGS"
                            ) { dialog, which -> // this method is called on click on positive
                                // button and on clicking shit button we
                                // are redirecting our user from our app to the
                                // settings page of our app.
                                dialog.cancel()
                                // below is the intent from which we
                                // are redirecting our user.
                                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
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
                            builder.show()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest?>?,
                    permissionToken: PermissionToken,
                ) {
                    permissionToken.continuePermissionRequest()
                }
            }).check()
    }

    private fun showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        val builder = AlertDialog.Builder(MainApplication.currentActivity)

        // below line is the title
        // for our alert dialog.
        builder.setTitle("Need Permission")

        // below line is our message for our dialog
        builder.setMessage("You need to give permission for step counter. Please go to settings-> Permission-> Physical activity & Location and click allow.")
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
        builder.show()
    }

    private fun getGoogleFitData() {
        /*if (checkPermission()) {
            if (hasFitPermission()) {
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        readStepCountDelta()
                        handler.postDelayed(this, 5000)
                    }
                }, 100)
                binding.llGoogleFitConnect.visibility = View.VISIBLE
                binding.rlConnect.visibility = View.GONE
                binding.rlConnected.visibility = View.VISIBLE
                preferences!!.isFitConnect = true
            } else {
                binding.llGoogleFitConnect.visibility = View.VISIBLE
                binding.rlConnect.visibility = View.VISIBLE
                binding.rlConnected.visibility = View.GONE
            }
        } else {
            PermissionUtil.requestPermission(
                PermissionUtil.readFitnessPermissions(),
                currentActivity
            )
        }*/

        if (hasFitPermission()) {
            handler.postDelayed(object : Runnable {
                override fun run() {
                    readStepCountDelta()
                    handler.postDelayed(this, 5000)
                }
            }, 100)
            binding.llGoogleFitConnect.visibility = View.VISIBLE
            binding.rlConnect.visibility = View.GONE
            binding.rlConnected.visibility = View.VISIBLE
            preferences!!.isFitConnect = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                readHistoricStepCount()
            }

        } else {
            binding.llGoogleFitConnect.visibility = View.VISIBLE
            binding.rlConnect.visibility = View.VISIBLE
            binding.rlConnected.visibility = View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getGoogleFitDataWithSignIn() {
        /*if (checkPermission()) {
            if (hasFitPermission()) {
                setUserAttribute(currentActivity!!, "stepCounter", "connected")
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        readStepCountDelta()
                        handler.postDelayed(this, 5000)
                    }
                }, 100)
                binding.llGoogleFitConnect.visibility = View.VISIBLE
                binding.rlConnect.visibility = View.GONE
                binding.rlConnected.visibility = View.VISIBLE
                preferences!!.isFitConnect = true
            } else {
                requestFitnessPermission()
            }
        } else {
            PermissionUtil.requestPermission(
                PermissionUtil.readFitnessPermissions(),
                currentActivity
            )
        }*/

        if (hasFitPermission()) {
            setUserAttribute(currentActivity!!, "stepCounter", "connected")
            handler.postDelayed(object : Runnable {
                override fun run() {
                    readStepCountDelta()
                    handler.postDelayed(this, 5000)
                }
            }, 100)
            binding.llGoogleFitConnect.visibility = View.VISIBLE
            binding.rlConnect.visibility = View.GONE
            binding.rlConnected.visibility = View.VISIBLE
            preferences!!.isFitConnect = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                readHistoricStepCount()
            }

        } else {
            requestFitnessPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun readHistoricStepCount() {
        if (!hasFitPermission()) {
            requestFitnessPermission()
            return
        }

        Fitness.getHistoryClient(
            currentActivity!!,
            GoogleSignIn.getLastSignedInAccount(currentActivity!!)!!
        )
            .readData(queryFitnessData())
            .addOnSuccessListener { dataReadResponse -> // For the sake of the sample, we'll print the data so we can see what we just
                // added. In general, logging fitness information should be avoided for privacy
                // reasons.
                printData(dataReadResponse)
            }
            .addOnFailureListener { e ->
                Log.e(
                    TAG,
                    "There was a problem reading the historic data.",
                    e
                )
            }
    }

    private fun readStepCountDelta() {
        if (!hasFitPermission()) {
            requestFitnessPermission()
            return
        }

        if (preferences!!.weight != null && !TextUtils.isEmpty(preferences!!.weight)) {
            insertWeight(currentActivity!!, DataType.TYPE_WEIGHT, preferences!!.weight!!.toFloat())

            if (preferences!!.heightFeet != null && !TextUtils.isEmpty(preferences!!.heightFeet)) {

                var tInch =
                    (preferences!!.heightFeet!!.toInt() * 12) + preferences!!.heightInch!!.toInt()
                var cMeter = tInch * 0.0254

                insertHeight(currentActivity!!, DataType.TYPE_HEIGHT, cMeter.toFloat())
            }
        }

        Fitness.getHistoryClient(
            currentActivity!!,
            GoogleSignIn.getLastSignedInAccount(currentActivity!!)!!
        )
            .readDailyTotal(DataType.AGGREGATE_STEP_COUNT_DELTA)
            .addOnSuccessListener { dataSet ->
                val total =
                    if (dataSet.isEmpty) 0 else dataSet.dataPoints[0].getValue(Field.FIELD_STEPS)
                        .asInt().toLong()
//                tvSteps.setText(" (" + String.format(Locale.ENGLISH, "%d", total) + " step)")

                binding.tvGoogleFitTitle.text = fitMessage
                binding.tvConnectedFitDesc.text = fitDetailMessage
                val properties = Properties()
                val i1 = String.format(Locale.ENGLISH, "%d", total).toInt()
                properties.addAttribute("steps", i1)
                trackEvent(currentActivity!!, "todaySteps", properties)
                setUserAttribute(
                    currentActivity!!,
                    "todaySteps",
                    String.format(Locale.ENGLISH, "%d", total)
                )

                if (stepCountLastValue.equals("", ignoreCase = true)) {
                    callTrackForStepsAPi(
                        preferences!!.userId!!, preferences!!.getprofileid()!!,
                        getCurrentDate().toString(), String.format(Locale.ENGLISH, "%d", total)
                    )
                } else {
                    if (!TextUtils.isEmpty(stepCountLastValue) && stepCountLastValue.toInt() < i1) {
                        callTrackForStepsAPi(
                            preferences!!.userId!!, preferences!!.getprofileid()!!,
                            getCurrentDate().toString(), String.format(Locale.ENGLISH, "%d", total)
                        )
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w(
                    TAG,
                    "There was a problem getting the step count.",
                    e
                )
            }

        readCalories()
//        readCaloriesWithHeightWeight()
    }

    private fun readCalories() {
        if (!hasFitPermission()) {
            requestFitnessPermission()
            return
        }
        Fitness.getHistoryClient(
            currentActivity!!,
            GoogleSignIn.getLastSignedInAccount(currentActivity!!)!!
        )
            .readDailyTotal(DataType.AGGREGATE_CALORIES_EXPENDED)
            .addOnSuccessListener { dataSet ->
                val total =
                    if (dataSet.isEmpty) 0 else dataSet.dataPoints[0].getValue(Field.FIELD_CALORIES)
                        .asFloat().toLong()

                val properties = Properties()
                val i1 = String.format(Locale.ENGLISH, "%d", total).toInt()
                properties.addAttribute("calories", i1)
                trackEvent(currentActivity!!, "todayCalories", properties)
                setUserAttribute(
                    currentActivity!!,
                    "todayCalories",
                    String.format(Locale.ENGLISH, "%d", total)
                )

                if (stepCountLastValue.equals("", ignoreCase = true)) {
                    callTrackForCalAPi(
                        preferences!!.userId!!,
                        preferences!!.getprofileid()!!,
                        getCurrentDate().toString(),
                        String.format(Locale.ENGLISH, "%d", total)
                    )
                } else {
                    if (!TextUtils.isEmpty(stepCountLastValue) && stepCountLastValue.toInt() < i1) {
                        callTrackForCalAPi(
                            preferences!!.userId!!,
                            preferences!!.getprofileid()!!,
                            getCurrentDate().toString(),
                            String.format(Locale.ENGLISH, "%d", total)
                        )
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w(
                    TAG,
                    "There was a problem getting the step count.",
                    e
                )
            }
    }

    private fun readCaloriesWithHeightWeight() {
        if (!hasFitPermission()) {
            requestFitnessPermission()
            return
        }
        Fitness.getHistoryClient(
            currentActivity!!,
            GoogleSignIn.getLastSignedInAccount(currentActivity!!)!!
        )
            .readDailyTotal(DataType.AGGREGATE_CALORIES_EXPENDED)
            .addOnSuccessListener { dataSet ->
                val total =
                    if (dataSet.isEmpty) 0 else dataSet.dataPoints[0].getValue(Field.FIELD_CALORIES)
                        .asFloat().toLong()

                val properties = Properties()
            }
            .addOnFailureListener { e ->
                Log.w(
                    TAG,
                    "There was a problem getting the step count.",
                    e
                )
            }
    }

    private fun callTrackForStepsAPi(
        customerId: String,
        profileId: String,
        dateTime: String,
        steps: String,
    ) {
        if (isConnection(currentActivity!!)) {
            ApiCall.instance.trackSteps(customerId, profileId, dateTime, steps, this)
        } else {
            Toast.makeText(
                currentActivity,
                "please check your internet connection",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun callTrackForCalAPi(
        customerId: String,
        profileId: String,
        dateTime: String,
        cals: String,
    ) {
        if (isConnection(currentActivity!!)) {
            ApiCall.instance.trackCal(customerId, profileId, dateTime, cals, this)
        } else {
            Toast.makeText(
                currentActivity,
                "please check your internet connection",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun insertWeight(context: Context, dataType: DataType, value: Float) {
        val startTime = Calendar.getInstance().timeInMillis
        val dataSource: DataSource = DataSource.Builder()
            .setAppPackageName(currentActivity!!)
            .setDataType(dataType)
            .setType(DataSource.TYPE_RAW)
            .build()
        val dataPoint: DataPoint = DataPoint.builder(dataSource)
            .setTimeInterval(startTime, startTime, TimeUnit.MILLISECONDS)
            .setFloatValues(value)
            .build()
        val dataSet: DataSet = DataSet.builder(dataSource)
            .add(dataPoint)
            .build()
        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .insertData(dataSet)
            .addOnSuccessListener {
                println("success")
            }
            .addOnFailureListener {
                println("failed")
            }
    }

    private fun insertHeight(context: Context, dataType: DataType, value: Float) {
        val startTime = Calendar.getInstance().timeInMillis
        val dataSource: DataSource = DataSource.Builder()
            .setAppPackageName(currentActivity!!)
            .setDataType(dataType)
            .setType(DataSource.TYPE_RAW)
            .build()
        val dataPoint: DataPoint = DataPoint.builder(dataSource)
            .setTimeInterval(startTime, startTime, TimeUnit.MILLISECONDS)
            .setFloatValues(value)
            .build()
        val dataSet: DataSet = DataSet.builder(dataSource)
            .add(dataPoint)
            .build()
        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .insertData(dataSet)
            .addOnSuccessListener {
                println("success")
            }
            .addOnFailureListener {
                println("failed")
            }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            currentActivity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun printData(dataReadResult: DataReadResponse) {
        val mStepsArr = ArrayList<StepsCaloriesData>()
        val mCalsArr = ArrayList<StepsCaloriesData>()
        if (dataReadResult.buckets.size > 0) {
            for (bucket in dataReadResult.buckets) {
                val dataSets = bucket.dataSets
                for (dataSet in dataSets) {
                    val dataPoint = dataSet.dataPoints
                    for (dp in dataPoint) {
                        val stepsData = StepsCaloriesData()
                        val startDateTime = dp.getStartTime(TimeUnit.MILLISECONDS)
                        val dateTime = getDate(startDateTime, "yyyy-MM-dd")
                        val fieldValue = dp.getValue(dp.dataType.fields[0]).toString().toFloat()
                        val roundedValue = String.format("%.0f", fieldValue)
                        val name = dp.dataType.fields[0].name


                        if (name.equals("calories", ignoreCase = true)) {
                            stepsData.dateTime = dateTime
                            stepsData.roundedValue = roundedValue
                            stepsData.name = name
                            mCalsArr.add(stepsData)
                        } else {
                            if (!TextUtils.isEmpty(roundedValue) && roundedValue.toDouble() > 0) {
                                stepsData.dateTime = dateTime
                                stepsData.roundedValue = roundedValue
                                stepsData.name = name
                                mStepsArr.add(stepsData)
                            }
                        }

                    }
                }
            }
        }

        val mCombineArr = ArrayList<StepsCaloriesUploadData>()

        for (i in mStepsArr.indices) {
            for (j in mCalsArr.indices) {
                if (mStepsArr[i].dateTime == mCalsArr[j].dateTime) {
                    var data = StepsCaloriesUploadData()
                    data.calories = mCalsArr[j].roundedValue
                    data.steps = mStepsArr[i].roundedValue
                    data.date = mStepsArr[i].dateTime
                    mCombineArr.add(data)
                }
            }
        }

        var gson = GsonBuilder().create()
        var myCustomArray = gson.toJsonTree(mCombineArr).asJsonArray

        var obj = JsonObject()
        obj.add("steps_list", myCustomArray)

        if (isConnection(currentActivity!!)) {
            ApiCall.instance.saveMyStepsList(obj, this)
        }

    }

    companion object {
        /**
         * Returns a [DataReadRequest] for all step count changes in the past week.
         */
        fun queryFitnessData(): DataReadRequest {
            val cal = Calendar.getInstance()
            val now = Date()
            cal.time = now
            val endTimeNew = cal.timeInMillis
            val dt = org.joda.time.DateTime().withTimeAtStartOfDay()
            val endTime = dt.millis
            val startTime = dt.minusWeeks(4).millis
            return DataReadRequest.Builder()
                .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_HEART_RATE_BPM)
                .enableServerQueries()
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTimeNew, TimeUnit.MILLISECONDS)
                .build()
        }

        @RequiresApi(Build.VERSION_CODES.N)
        fun getDate(milliSeconds: Long, dateFormat: String?): String {
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = android.icu.text.SimpleDateFormat(dateFormat)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }
    }

    private fun fitInstallOrNot() {
        val launchIntent =
            currentActivity!!.packageManager.getLaunchIntentForPackage("com.google.android.apps.fitness")
        if (launchIntent != null) {
            binding.tvGoogleConnect.text = "Connect"
//            startActivity(launchIntent)
        } else {
            binding.tvGoogleConnect.text = "Install"
        }
    }
}