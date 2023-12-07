package app.oxyjon.ui.activity

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.adapter.Question2ActiveAdapter
import app.oxyjon.adapter.WeightListAdapter
import app.oxyjon.bean.OnBoardingResponse
import app.oxyjon.bean.UpdateQuestionFirstResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.ui.kotlin.activity.DashboardActivity
import app.oxyjon.utils.CheckConnection
import app.oxyjon.utils.FunctionHelper
import app.oxyjon.utils.GridSpacingItemDecoration
import app.oxyjon.utils.Helper
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.firebase.analytics.FirebaseAnalytics
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper.setUserAttribute
import com.moengage.core.analytics.MoEAnalyticsHelper.trackEvent
import com.moengage.inapp.MoEInAppHelper
import retrofit2.Response

class QuestionSecondActivity : BaseActivity(),
    Question2ActiveAdapter.ClickListener, WeightListAdapter.OnClickListener, IApiCallback {
    var preferences: AppSharedPreferences? = null

    @JvmField
    @BindView(R.id.tvTitle)
    var tvTitle: TextView? = null

    @JvmField
    @BindView(R.id.tvDesc)
    var tvDesc: TextView? = null

    @JvmField
    @BindView(R.id.tvSkip)
    var tvSkip: TextView? = null

    @JvmField
    @BindView(R.id.spFeet)
    var spFeet: Spinner? = null

    @JvmField
    @BindView(R.id.spInches)
    var spInches: Spinner? = null

    @JvmField
    @BindView(R.id.tvFeet)
    var tvFeet: TextView? = null

    @JvmField
    @BindView(R.id.tvInches)
    var tvInches: TextView? = null

    @JvmField
    @BindView(R.id.rvWeight)
    var rvWeight: RecyclerView? = null

    @JvmField
    @BindView(R.id.rvPhysicallyActive)
    var rvPhysicallyActive: RecyclerView? = null
    var adapter: Question2ActiveAdapter? = null
    var weightAdapter: WeightListAdapter? = null
    var activityKey: String = ""
    var progressDialog: ProgressDialog? = null
    private var mActivityList: ArrayList<OnBoardingResponse.Data.OnboardScreenQuest2.Activity> =
        ArrayList()
    var userName: String = ""
    var strWeight: String = ""
    var feet: Array<String?> = arrayOf("1", "2", "3", "4", "5", "6")
    var inch: Array<String?> =
        arrayOf("0","1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
    var checkedItemFeet: Int = -1
    var checkedItemInch: Int = -1
    private var navigationType: String? = ""

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onStart() {
        super.onStart()
        MoEInAppHelper.getInstance().showInApp(this)
    }

    public override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        MoEInAppHelper.getInstance().onConfigurationChanged()
    }

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_second)
        ButterKnife.bind(this)

        MainApplication.currentActivity = this
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        preferences = AppSharedPreferences.getInstance(this)
        progressDialog = Helper.initProgress(MainApplication.currentActivity)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            navigationType = bundle.getString("navigationType")
        }
        if (CheckConnection.isConnection(this)) {
            if (progressDialog != null && !progressDialog!!.isShowing) {
                progressDialog!!.show()
            }
            ApiCall.instance.getOnBoarding(preferences!!.getprofileid(), this)
        } else {
            Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show()
        }
        val properties = Properties()
        properties.addAttribute("opened", true)
        trackEvent(this, "OpenOnBoardingBMIScreen", properties)
        weightAdapter = WeightListAdapter(this)
        rvWeight!!.adapter = weightAdapter
        weightAdapter!!.setClickListener(this)
        showSpinnerFeet(spFeet)
        showSpinnerInches(spInches)
        tvFeet!!.setOnClickListener { selectFeet() }
        tvInches!!.setOnClickListener { selectInches() }
        if (!TextUtils.isEmpty(navigationType) && navigationType.equals("foodDiary",
                ignoreCase = true)
        ) {
            tvSkip!!.visibility = View.GONE
        } else {
            tvSkip!!.visibility = View.GONE
        }
    }

    private fun selectFeet() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@QuestionSecondActivity)
        alertDialog.setTitle("Choose an feet")
        alertDialog.setSingleChoiceItems(feet,
            checkedItemFeet,
            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                checkedItemFeet = which
                tvFeet!!.text = feet[which] + " Feet"

                preferences!!.heightFeet = feet[which]+""

                dialog.dismiss()
            })
        alertDialog.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int -> })
        val customAlertDialog: AlertDialog = alertDialog.create()
        customAlertDialog.show()
    }

    private fun selectInches() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@QuestionSecondActivity)
        alertDialog.setTitle("Choose an inch")
        alertDialog.setSingleChoiceItems(inch,
            checkedItemInch,
            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                checkedItemInch = which
                tvInches!!.text = inch[which] + " Inches"
                preferences!!.heightInch = inch[which]+""
                dialog.dismiss()
            })
        alertDialog.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int -> })
        val customAlertDialog: AlertDialog = alertDialog.create()
        customAlertDialog.show()
    }

    private fun showSpinnerFeet(spinner: Spinner?) {
        val adapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, R.layout.support_simple_spinner_dropdown_item, feet)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner!!.adapter = adapter
    }

    fun showSpinnerInches(spinner: Spinner?) {
        val adapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, R.layout.support_simple_spinner_dropdown_item, inch)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner!!.adapter = adapter
    }

    @OnClick(R.id.tvNext)
    fun goNext() {
//        String strFeet = spFeet.getSelectedItem().toString();
//        String strInches = spInches.getSelectedItem().toString();
        val strFeet: String = tvFeet!!.text.toString().split(" ").toTypedArray()[0]
        val strInches: String = tvInches!!.text.toString().split(" ").toTypedArray()[0]
        if (TextUtils.isEmpty(strFeet) || strFeet.equals("Select", ignoreCase = true)) {
            Toast.makeText(this, "please select feet", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(strInches) || strInches.equals("Select", ignoreCase = true)) {
            Toast.makeText(this, "please select inches", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(strWeight)) {
            Toast.makeText(this, "please select your weight", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(activityKey)) {
            Toast.makeText(this, "Choose your activity level", Toast.LENGTH_SHORT).show()
        } else {
            if (CheckConnection.isConnection(this)) {
                if (progressDialog != null && !progressDialog!!.isShowing()) {
                    progressDialog!!.show()
                }
                if (!TextUtils.isEmpty(strFeet) && !TextUtils.isEmpty(strInches) && !TextUtils.isEmpty(
                        strWeight)
                ) {
                    val cInch: Int = strFeet.toInt() * 12
                    val inch: Int = strInches.toInt()
                    val heightInch: Int = cInch + inch
                    val weight: Double = strWeight.toDouble()
                    val heightCM: Double = (heightInch * 2.54)
                    val heightMeter: Double = heightCM / 100
                    val bmi: Double = weight / (heightMeter * heightMeter)
                    setUserAttribute(this, "BMI", bmi)
                }

                val bundle = Bundle()

                if (!TextUtils.isEmpty(strFeet)) {
                    setUserAttribute(this, "feet", strFeet)
                    bundle.putString("feet", strFeet)
                }
                if (!TextUtils.isEmpty(strInches)) {
                    setUserAttribute(this, "inches", strInches)
                    bundle.putString("inches", strInches)
                }
                if (!TextUtils.isEmpty(strWeight)) {
                    setUserAttribute(this, "weight", strWeight)
                    bundle.putString("weight", strWeight)
                }
                setUserAttribute(this, "physicalActivity", activityKey)
                bundle.putString("physicalActivity", activityKey)


                firebaseAnalytics!!.logEvent("OnBoardingForm2", bundle)
                firebaseAnalytics!!.setDefaultEventParameters(bundle)

                ApiCall.instance.updateQuestionSecond(preferences!!.getprofileid(),
                    strFeet,
                    strInches,
                    strWeight,
                    activityKey,
                    this)
            } else {
                Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    @OnClick(R.id.tvSkip)
    fun goSkip() {
        val properties: Properties = Properties()
        properties.addAttribute("isSkip", true)
        trackEvent(this, "OnBoardingBMI", properties)
        val intent: Intent = Intent(this, DashboardActivity::class.java)
        val info: AppSharedPreferences? =
            AppSharedPreferences.getInstance(this@QuestionSecondActivity)
        info!!.userOnBoard = "2"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setQuestion2ActiveRecycler(data: ArrayList<OnBoardingResponse.Data.OnboardScreenQuest2.Activity>) {
        val staggeredGridLayoutManager = GridLayoutManager(this, 2)
        rvPhysicallyActive!!.layoutManager = staggeredGridLayoutManager
        rvPhysicallyActive!!.addItemDecoration(GridSpacingItemDecoration(2, 20, false))
        adapter = Question2ActiveAdapter(this, this, data)
        rvPhysicallyActive!!.adapter = adapter
    }

    fun callIntent(intent: Intent) {
        val info: AppSharedPreferences? =
            AppSharedPreferences.getInstance(this@QuestionSecondActivity)
        info!!.userOnBoard = "2"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    public override fun onRecyclerItemClick(pos: String) {
        activityKey = pos
    }

    public override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        if ((type == "onBoarding")) {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
            val response: Response<OnBoardingResponse> = data as Response<OnBoardingResponse>
            if (response.isSuccessful) {
                if ((response.body()!!.errorCode == "0")) {
                    if (null != response.body()!!.data.onboard_screen_quest1 && null != response.body()!!.data.onboard_screen_quest1.profiledata.fullname) {
                        userName = response.body()!!.data.onboard_screen_quest1.profiledata.fullname
                    }
                    tvTitle!!.text = response.body()!!.data.onboard_screen_quest2.screen_title
                    tvDesc!!.text = response.body()!!.data.onboard_screen_quest2.screen_message
                    mActivityList = response.body()!!.data.onboard_screen_quest2.activity_list
                    setQuestion2ActiveRecycler(mActivityList)
                    if (!TextUtils.isEmpty(navigationType) && navigationType.equals("foodDiary",
                            ignoreCase = true)
                    ) {
                        tvSkip!!.visibility = View.GONE
                    } else {
                        if (response.body()!!.data.onboard_screen_quest2.is_required.equals("No",
                                ignoreCase = true)
                        ) {
                            tvSkip!!.visibility = View.GONE
                        } else {
                            tvSkip!!.visibility = View.GONE
                        }
                    }
                    if (response.body()!!.data.onboard_screen_quest2.lifestyledata != null) {
                        if (null != response.body()!!.data.onboard_screen_quest2.lifestyledata.height_ft && !TextUtils.isEmpty(
                                response.body()!!.data.onboard_screen_quest2.lifestyledata.height_ft)
                        ) {
                            tvFeet!!.text =
                                response.body()!!.data.onboard_screen_quest2.lifestyledata.height_ft + " Feet"
                            for (i in feet.indices) {
                                if (feet[i]
                                        .equals(response.body()!!.data.onboard_screen_quest2.lifestyledata.height_ft,
                                            ignoreCase = true)
                                ) {
                                    checkedItemFeet = i
                                }
                            }
                        }
                        if (null != response.body()!!.data.onboard_screen_quest2.lifestyledata.height_inches && !TextUtils.isEmpty(
                                response.body()!!.data.onboard_screen_quest2.lifestyledata.height_inches)
                        ) {
                            tvInches!!.text =
                                response.body()!!.data.onboard_screen_quest2.lifestyledata.height_inches + " Inches"
                            for (i in inch.indices) {
                                if (inch.get(i)
                                        .equals(response.body()!!.data.onboard_screen_quest2.lifestyledata.height_inches,
                                            ignoreCase = true)
                                ) {
                                    checkedItemInch = i
                                }
                            }
                        }
                        if (null != response.body()!!.data.onboard_screen_quest2.lifestyledata.weight && !TextUtils.isEmpty(
                                response.body()!!.data.onboard_screen_quest2.lifestyledata.weight)
                        ) {
                            if (weightAdapter != null) {
                                weightAdapter!!.setSelectedData(response.body()!!.data.onboard_screen_quest2.lifestyledata.weight)
                                strWeight =
                                    response.body()!!.data.onboard_screen_quest2.lifestyledata.weight
                                rvWeight!!.scrollToPosition(strWeight.toInt() - 10)
                            }
                        }
                        if (null != response.body()!!.data.onboard_screen_quest2.lifestyledata.activity_score && !TextUtils.isEmpty(
                                response.body()!!.data.onboard_screen_quest2.lifestyledata.activity_score)
                        ) {
                            for (i in mActivityList.indices) {
                                if (mActivityList[i].key.equals(response.body()!!.data.onboard_screen_quest2.lifestyledata.activity_score,
                                        ignoreCase = true)
                                ) {
                                    mActivityList.get(i).selectedOption = true
                                    adapter!!.notifyDataSetChanged()
                                    activityKey =
                                        response.body()!!.data.onboard_screen_quest2.lifestyledata.activity_score
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, response.body()!!.errorMsg, Toast.LENGTH_SHORT).show()
                }
            } else {
                FunctionHelper.showSnackMessage(findViewById(android.R.id.content),
                    getString(R.string.some_error_occurred))
            }
        } else if ((type == "updateQuestionSecond")) {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
            callTrackScreenAPi(preferences!!.userId,
                preferences!!.getprofileid(),
                "onboardingStep2",
                "0",
                "0")
            val response: Response<UpdateQuestionFirstResponse> =
                data as Response<UpdateQuestionFirstResponse>
            if (response.isSuccessful) {
                if ((response.body()!!.errorCode == "0")) {
                    val properties: Properties = Properties()
                    properties.addAttribute("isSkip", false)
                    trackEvent(this, "OnBoardingBMI", properties)
                    if (!TextUtils.isEmpty(navigationType) && navigationType.equals("foodDiary",
                            ignoreCase = true)
                    ) {
                        finish()
                    } else {
                        callIntent(Intent(this, DashboardActivity::class.java))
                    }
                } else {
                    Toast.makeText(this, response.body()!!.errorMsg, Toast.LENGTH_SHORT).show()
                }
            } else {
                FunctionHelper.showSnackMessage(findViewById(android.R.id.content),
                    getString(R.string.some_error_occurred))
            }
        }
    }

    public override fun onFailure(data: Any) {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
        val error: String = data as String
        if (error.contains("No address associated with hostname")) {
            FunctionHelper.showSnackMessage(findViewById(android.R.id.content),
                "please check your internet connection")
        } else if (error.contains("java.net.SocketTimeoutException")) {
            FunctionHelper.showSnackMessage(findViewById(android.R.id.content),
                "please check your internet connection")
        } else {
            FunctionHelper.showSnackMessage(findViewById(android.R.id.content),
                getString(R.string.some_error_occurred))
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

    public override fun onWeightItemClick(weight: String, pos: Int) {
        strWeight = weight
        rvWeight!!.scrollToPosition(pos)
        preferences!!.weight = strWeight
    }
}