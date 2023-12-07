package app.oxyjon.ui.activity

import android.content.Intent
import android.graphics.PorterDuff
import android.icu.text.DecimalFormat
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import app.oxyjon.R
import app.oxyjon.bean.OnBoardingResponse
import app.oxyjon.bean.UpdateQuestionFirstResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.ui.kotlin.activity.DashboardActivity
import app.oxyjon.utils.CheckConnection
import app.oxyjon.utils.FunctionHelper
import butterknife.BindView
import butterknife.ButterKnife
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper.setBirthDate
import com.moengage.core.analytics.MoEAnalyticsHelper.setFirstName
import com.moengage.core.analytics.MoEAnalyticsHelper.setGender
import com.moengage.core.analytics.MoEAnalyticsHelper.trackEvent
import com.moengage.core.model.UserGender
import retrofit2.Response

class QuestionFirstActivity constructor() : BaseActivity(), IApiCallback {
    var preferences: AppSharedPreferences? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tvTitle)
    var tvTitle: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tvDesc)
    var tvMessage: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.edtUserName)
    var edtUserName: EditText? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.edtCompanyCode)
    var edtCompanyCode: EditText? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.edtAge)
    var edtAge: EditText? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.imgMinius)
    var imgMinius: ImageView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.imgPlus)
    var imgPlus: ImageView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.llMale)
    var llMale: LinearLayout? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.llFeMale)
    var llFeMale: LinearLayout? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tvMale)
    var tvMale: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tvFeMale)
    var tvFeMale: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.imgMale)
    var imgMale: ImageView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.imgFeMale)
    var imgFeMale: ImageView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tvNext)
    var tvNext: TextView? = null
    var tvdate: String = ""
    var tvmonth: String = ""
    var tvyear: String = ""
    var gender: String = ""
    var age: Double = 0.0

    @RequiresApi(Build.VERSION_CODES.N)
    var df: DecimalFormat = DecimalFormat()
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_first)
        ButterKnife.bind(this)
        preferences = AppSharedPreferences.Companion.getInstance(this)
        df!!.maximumFractionDigits = 1
        tvNext!!.visibility = View.GONE
        if (CheckConnection.isConnection(this)) {
            ApiCall.instance.getOnBoarding(
                preferences!!.getprofileid(), this)
        } else {
            Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show()
        }
        val properties = Properties()
        properties.addAttribute("opened", true)
        trackEvent(this, "OpenOnBoardingFirstScreen", properties)
        imgPlus!!.setOnClickListener {
            if (!TextUtils.isEmpty(edtAge!!.text.toString().trim { it <= ' ' })) {
                age = edtAge!!.text.toString().trim { it <= ' ' }.toDouble()
                age += 1
                edtAge!!.setText("" + df.format(age))
            }
        }
        imgMinius!!.setOnClickListener {
            if (!TextUtils.isEmpty(edtAge!!.text.toString().trim { it <= ' ' })) {
                age = edtAge!!.text.toString().trim { it <= ' ' }.toDouble()
                age -= 1
                if (age > 0) {
                    edtAge!!.setText("" + df.format(age))
                }
            }
        }
        llFeMale!!.setOnClickListener {
            gender = "F"
            tvNext!!.visibility = View.VISIBLE
            tvFeMale!!.setTextColor(resources.getColor(R.color.white))
            tvMale!!.setTextColor(resources.getColor(R.color.blueDark))
            llFeMale!!.setBackgroundResource(R.drawable.drawable_gender_selected)
            llMale!!.setBackgroundResource(R.drawable.drawable_gender_default)
            imgMale!!.setImageResource(R.drawable.ic_male)
            imgFeMale!!.setImageResource(R.drawable.ic_female)
            imgFeMale!!.setColorFilter(ContextCompat.getColor(this@QuestionFirstActivity,
                R.color.white), PorterDuff.Mode.SRC_IN)
            imgMale!!.setColorFilter(ContextCompat.getColor(this@QuestionFirstActivity,
                R.color.blue_color), PorterDuff.Mode.SRC_IN)
        }
        llMale!!.setOnClickListener {
            gender = "M"
            tvNext!!.visibility = View.VISIBLE
            tvMale!!.setTextColor(resources.getColor(R.color.white))
            tvFeMale!!.setTextColor(resources.getColor(R.color.blueDark))
            llMale!!.setBackgroundResource(R.drawable.drawable_gender_selected)
            llFeMale!!.setBackgroundResource(R.drawable.drawable_gender_default)
            imgFeMale!!.setImageResource(R.drawable.ic_female)
            imgMale!!.setImageResource(R.drawable.ic_male)
            imgMale!!.setColorFilter(ContextCompat.getColor(this@QuestionFirstActivity,
                R.color.white), PorterDuff.Mode.SRC_IN)
            imgFeMale!!.setColorFilter(ContextCompat.getColor(this@QuestionFirstActivity,
                R.color.blue_color), PorterDuff.Mode.SRC_IN)
        }
        tvNext!!.setOnClickListener { //                callIntent(new Intent(QuestionFirstActivity.this, BenefitActivity.class));
            if (TextUtils.isEmpty(edtUserName!!.text.toString().trim { it <= ' ' })) {
                edtUserName!!.requestFocus()
                Toast.makeText(this@QuestionFirstActivity,
                    "please enter your name",
                    Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(edtAge!!.text.toString().trim { it <= ' ' })) {
                edtAge!!.requestFocus()
                Toast.makeText(this@QuestionFirstActivity,
                    "please enter your age",
                    Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(gender)) {
                Toast.makeText(this@QuestionFirstActivity,
                    "please select gender",
                    Toast.LENGTH_SHORT).show()
            } else {
                if (CheckConnection.isConnection(this@QuestionFirstActivity)) {
                    setFirstName(this@QuestionFirstActivity,
                        edtUserName!!.text.toString().trim { it <= ' ' })
                    if (gender.equals("M", ignoreCase = true)) {
                        setGender(this@QuestionFirstActivity, UserGender.MALE)
                    } else {
                        setGender(this@QuestionFirstActivity, UserGender.FEMALE)
                    }
                    setBirthDate(this@QuestionFirstActivity,
                        edtAge!!.text.toString().trim { it <= ' ' })
                    ApiCall.instance
                        .updateQuestionFirst(edtCompanyCode!!.text.toString().trim { it <= ' ' },
                            edtUserName!!.text.toString().trim { it <= ' ' },
                            gender,
                            edtAge!!.text.toString().trim { it <= ' ' },
                            this@QuestionFirstActivity)
                } else {
                    Toast.makeText(this@QuestionFirstActivity,
                        getString(R.string.check_connection),
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun callIntent(intent: Intent) {
        val info: AppSharedPreferences? = AppSharedPreferences.getInstance(this)
        info!!.userOnBoard = "1"
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    public override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        if ((type == "onBoarding")) {
            FunctionHelper.enableUserIntraction()
            val response: Response<OnBoardingResponse> = data as Response<OnBoardingResponse>
            if (response.isSuccessful) {
                if ((response.body()!!.errorCode == "0")) {
                    tvTitle!!.text = response.body()!!.data.onboard_screen_quest1.screen_title
                    tvMessage!!.text = response.body()!!.data.onboard_screen_quest1.screen_message
                    if (response.body()!!.data.onboard_screen_quest1.profiledata != null) {
                        if (null != response.body()!!.data.onboard_screen_quest1.profiledata.fullname && !TextUtils.isEmpty(
                                response.body()!!.data.onboard_screen_quest1.profiledata.fullname)
                        ) {
                            edtUserName!!.setText(response.body()!!.data.onboard_screen_quest1.profiledata.fullname)
                        }
                        if (null != response.body()!!.data.onboard_screen_quest1.profiledata.gender && !TextUtils.isEmpty(
                                response.body()!!.data.onboard_screen_quest1.profiledata.gender)
                        ) {
                            if (response.body()!!.data.onboard_screen_quest1.profiledata.gender.equals(
                                    "M",
                                    ignoreCase = true)
                            ) {
                                gender = "M"
                                tvNext!!.visibility = View.VISIBLE
                                tvMale!!.setTextColor(resources.getColor(R.color.white))
                                tvFeMale!!.setTextColor(resources.getColor(R.color.blueDark))
                                imgMale!!.setImageResource(R.drawable.ic_male)
                                imgFeMale!!.setImageResource(R.drawable.ic_female)
                                imgMale!!.setColorFilter(ContextCompat.getColor(this@QuestionFirstActivity,
                                    R.color.white), PorterDuff.Mode.SRC_IN)
                                imgFeMale!!.setColorFilter(ContextCompat.getColor(this@QuestionFirstActivity,
                                    R.color.blue_color), PorterDuff.Mode.SRC_IN)
                                llMale!!.setBackgroundResource(R.drawable.drawable_gender_selected)
                                llFeMale!!.setBackgroundResource(R.drawable.drawable_gender_default)
                            } else {
                                gender = "F"
                                tvNext!!.visibility = View.VISIBLE
                                tvFeMale!!.setTextColor(resources.getColor(R.color.white))
                                tvMale!!.setTextColor(resources.getColor(R.color.blueDark))
                                imgFeMale!!.setImageResource(R.drawable.ic_female)
                                imgMale!!.setImageResource(R.drawable.ic_male)
                                imgFeMale!!.setColorFilter(ContextCompat.getColor(this@QuestionFirstActivity,
                                    R.color.white), PorterDuff.Mode.SRC_IN)
                                imgMale!!.setColorFilter(ContextCompat.getColor(this@QuestionFirstActivity,
                                    R.color.blue_color), PorterDuff.Mode.SRC_IN)
                                llFeMale!!.setBackgroundResource(R.drawable.drawable_gender_selected)
                                llMale!!.setBackgroundResource(R.drawable.drawable_gender_default)
                            }
                            if (response.body()!!.data.onboard_screen_quest1.profiledata.patient_age != null && !TextUtils.isEmpty(
                                    response.body()!!.data.onboard_screen_quest1.profiledata.patient_age)
                            ) {
                                edtAge!!.setText("" + response.body()!!.data.onboard_screen_quest1.profiledata.patient_age)
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
        } else if ((type == "updateQuestionFirst")) {
            FunctionHelper.enableUserIntraction()
            callTrackScreenAPi(preferences!!.userId,
                preferences!!.getprofileid(),
                "onboardingStep1",
                "0",
                "0")
            val response: Response<UpdateQuestionFirstResponse> =
                data as Response<UpdateQuestionFirstResponse>
            if (response.isSuccessful) {
                if ((response.body()!!.errorCode == "0")) {

                    if (response.body()!!.data?.profile_type == "B2B")
                    {
                        val properties: Properties = Properties()
                        properties.addAttribute("isSkip", true)
                        trackEvent(this, "OnBoardingStep1", properties)

                        if (response.body()!!.data?.screen_quest2 == true)
                        {
                            val intent = Intent(this, QuestionSecondActivity::class.java)
                            val info: AppSharedPreferences =AppSharedPreferences.getInstance(this@QuestionFirstActivity)!!
                            info.userOnBoard = "3"
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()

                        }else{
                            val intent = Intent(this, DashboardActivity::class.java)
                            val info: AppSharedPreferences =AppSharedPreferences.getInstance(this@QuestionFirstActivity)!!
                            info.userOnBoard = "2"
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }

                    }else {
                        val properties: Properties = Properties()
                        properties.addAttribute("isSkip", false)
                        trackEvent(this, "OnBoardingStep1", properties)
                        callIntent(Intent(this@QuestionFirstActivity, BenefitActivity::class.java))
                    }
                } else {
                    if (response.body()!!!=null && response.body()!!.errorMsg!=null) {
                        Toast.makeText(this, response.body()!!.errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                FunctionHelper.showSnackMessage(findViewById(android.R.id.content),
                    getString(R.string.some_error_occurred))
            }
        }
    }

    public override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
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
        actionHeading: String
    ) {
        if (CheckConnection.isConnection(this)) {
            ApiCall.instance
                .trackScreen(customerId, profileId, eventName, actionId, actionHeading, this)
        } else {
            Toast.makeText(this, "please check your internet connection", Toast.LENGTH_SHORT).show()
        }
    }
}