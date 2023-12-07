package app.oxyjon.ui.kotlin.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.bean.AnalyticResponse
import app.oxyjon.bean.GetMyFoodDiaryResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityAnalyticsReportBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.utils.CheckConnection
import app.oxyjon.utils.FunctionHelper
import com.google.firebase.analytics.FirebaseAnalytics
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper
import retrofit2.Response
import java.sql.Date
import java.text.SimpleDateFormat

class AnalyticsReportActivity : BaseActivity(), IApiCallback {
    var preferences: AppSharedPreferences? = null
    lateinit var binding: ActivityAnalyticsReportBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticsReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        preferences = AppSharedPreferences.getInstance(this)

        MainApplication.currentActivity = this

        binding.imgBack.setOnClickListener {
            finish()
        }

        if (CheckConnection.isConnection(this)) {
            FunctionHelper.disable_user_Intration(this, resources.getString(R.string.loading))
            ApiCall.instance.getAnalyticReportDetail(preferences!!.getprofileid(), this)
        } else {
            Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show()
        }

        binding.tvSugarViewLog.setOnClickListener {
            val intent = Intent(this, ViewSugarActivity::class.java)
            startActivity(intent)

            val properties = Properties()
            properties.addAttribute("isClickSugar", true)
            MoEAnalyticsHelper.trackEvent(this, "ClickSugarReport", properties)

            val parameters = Bundle().apply {
                this.putString("isClickSugar", "ClickSugarReport")
            }
            firebaseAnalytics.setDefaultEventParameters(parameters)
        }

        binding.tvFoodViewLog.setOnClickListener {
            callApiForCheckFoodDiaryList()

            val properties = Properties()
            properties.addAttribute("isClickFood", true)
            MoEAnalyticsHelper.trackEvent(this, "ClickFoodReport", properties)

            val parameters = Bundle().apply {
                this.putString("isClickFood", "ClickFoodReport")
            }
            firebaseAnalytics.setDefaultEventParameters(parameters)
        }

        binding.tvExerciseViewLog.setOnClickListener {
            if (preferences!!.isFitConnect) {
                val intent = Intent(this, GoogleFitStepActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, StepGoalActivity::class.java)
                startActivity(intent)
            }

            val properties = Properties()
            properties.addAttribute("isClickStep", true)
            MoEAnalyticsHelper.trackEvent(this, "ClickStepReport", properties)

            val parameters = Bundle().apply {
                this.putString("isClickStep", "ClickStepReport")
            }
            firebaseAnalytics.setDefaultEventParameters(parameters)
        }
    }

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "myFoodDiaryList") {
            val response = data as Response<GetMyFoodDiaryResponse>
            if (response.isSuccessful) {
                if (response.isSuccessful && response.body()!!.errorCode == "1") {
                    if (response.body()!!.data != null && response.body()!!.data!!.size > 0) {
                        val intent = Intent(this, FoodDiaryActivity::class.java)
                        intent.putExtra("navigationType", "viewFoodDiary")
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, FoodDiaryActivity::class.java)
                        intent.putExtra("navigationType", "addFoodDiary")
                        startActivity(intent)
                    }
                } else {
                    val intent = Intent(this, FoodDiaryActivity::class.java)
                    intent.putExtra("navigationType", "addFoodDiary")
                    startActivity(intent)
                }
            }
        }
        if (type == "analyticReport") {
            val response = data as Response<AnalyticResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {


                    if (response.body()!!.health_score != null) {
                        if (response.body()!!.health_score.final_score != null && !TextUtils.isEmpty(
                                response.body()!!.health_score.final_score
                            )
                        ) {

                            var min = response.body()!!.health_score.final_score.split("/")[0]
                            var max = response.body()!!.health_score.final_score.split("/")[1]

                            binding.circularProgressBar.progress = min.toFloat()
                            binding.circularProgressBar.maximum = max.toFloat()

                            binding.tvTotalScore.text =
                                "" + FunctionHelper.roundOffDecimal(min.toDouble()) + "/" + max

                            if (min.toFloat() < 3.3) {
                                binding.circularProgressBar.foregroundStrokeColor = ContextCompat.getColor(this, R.color.red)
                            } else if (min.toFloat() > 3.3 && min.toFloat() < 6.6) {
                                binding.circularProgressBar.foregroundStrokeColor = ContextCompat.getColor(this, R.color.yellow)
                            } else if (min.toFloat() > 6.6) {
                                binding.circularProgressBar.foregroundStrokeColor = ContextCompat.getColor(this, R.color.green)
                            }
                        }

                        if (response.body()!!.health_score.sugarScore != null) {
                            var min = response.body()!!.health_score.sugarScore.score.split("/")[0]
                            var max = response.body()!!.health_score.sugarScore.score.split("/")[1]

                            binding.progressSugar.max = max.toInt()
                            binding.progressSugar.progress = min.toFloat().toInt()

                            binding.tvSugarValue.text =
                                response.body()!!.health_score.sugarScore.label_name + " (" + response.body()!!.health_score.sugarScore.score + ")"

                            if (!TextUtils.isEmpty(response.body()!!.health_score.sugarScore.label_last_data_point)) {
                                binding.tvLastSugarValue.visibility = View.VISIBLE
                                binding.tvLastSugarValue.text =
                                    response.body()!!.health_score.sugarScore.label_last_data_point
                            } else {
                                binding.tvLastSugarValue.visibility = View.GONE
                            }
                            binding.tvSugarAction.text =
                                response.body()!!.health_score.sugarScore.action_buton
                        }
                        if (response.body()!!.health_score.food_calories_data != null) {
                            var min =
                                response.body()!!.health_score.food_calories_data.score.split("/")[0]
                            var max =
                                response.body()!!.health_score.food_calories_data.score.split("/")[1]

                            binding.progressFood.progress = min.toFloat().toInt()
                            binding.progressFood.max = max.toInt()

                            binding.tvFoodValue.text =
                                response.body()!!.health_score.food_calories_data.label_name + " (" + response.body()!!.health_score.food_calories_data.score + ")"

                            if (!TextUtils.isEmpty(response.body()!!.health_score.food_calories_data.label_last_data_point)) {
                                binding.tvLastFoodValue.visibility = View.VISIBLE
                                binding.tvLastFoodValue.text =
                                    response.body()!!.health_score.food_calories_data.label_last_data_point
                            } else {
                                binding.tvLastFoodValue.visibility = View.GONE
                            }
                            binding.tvFoodAction.text =
                                response.body()!!.health_score.food_calories_data.action_buton
                        }
                        if (response.body()!!.health_score.exercise != null) {
                            var min = response.body()!!.health_score.exercise.score.split("/")[0]
                            var max = response.body()!!.health_score.exercise.score.split("/")[1]

                            binding.progressExercise.max = max.toInt()
                            binding.progressExercise.progress = min.toFloat().toInt()

                            binding.tvExerciseValue.text =
                                response.body()!!.health_score.exercise.label_name + " (" + response.body()!!.health_score.exercise.score + ")"

                            if (!TextUtils.isEmpty(response.body()!!.health_score.exercise.label_last_data_point)) {
                                binding.tvLastExerciseValue.visibility = View.VISIBLE
                                binding.tvLastExerciseValue.text =
                                    response.body()!!.health_score.exercise.label_last_data_point
                            } else {
                                binding.tvLastExerciseValue.visibility = View.GONE
                            }
                            binding.tvExerciseAction.text =
                                response.body()!!.health_score.exercise.action_buton
                        }
                    }
                }
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }

    private fun callApiForCheckFoodDiaryList() {
        if (CheckConnection.isConnection(MainApplication.currentActivity!!)) {
            ApiCall.instance
                .getMyFoodDiaryList(preferences!!.getprofileid(), getCurrentDate(), this)
        } else {
            val intent = Intent(this, FoodDiaryActivity::class.java)
            intent.putExtra("navigationType", "addFoodDiary")
            startActivity(intent)
        }
    }

    private fun getCurrentDate(): String? {
        val yearFormat = SimpleDateFormat("yyyy-MM-dd")
        val d = Date(System.currentTimeMillis())
        return yearFormat.format(d)
    }
}