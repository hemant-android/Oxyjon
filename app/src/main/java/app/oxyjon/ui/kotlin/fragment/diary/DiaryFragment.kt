package app.oxyjon.ui.kotlin.fragment.diary

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.bean.GetMyFoodDiaryResponse
import app.oxyjon.bean.MyHealthDiaryResponse
import app.oxyjon.bean.MyMedicineResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.FragmentDiaryBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.retrofit.response.CommonResponse
import app.oxyjon.ui.activity.WebViewActivity
import app.oxyjon.ui.kotlin.activity.*
import app.oxyjon.utils.CheckConnection
import app.oxyjon.utils.FunctionHelper
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper
import retrofit2.Response
import java.sql.Date
import java.text.SimpleDateFormat

class DiaryFragment : Fragment(), IApiCallback {

    private lateinit var binding: FragmentDiaryBinding
    var preferences: AppSharedPreferences? = null
    private var dietPlanUrl: String? = ""
    private var bannerType: String? = ""
    private var heading: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDiaryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences = AppSharedPreferences.getInstance(requireActivity())

        if (CheckConnection.isConnection(requireActivity())) {
            FunctionHelper.disable_user_Intration(
                requireActivity(),
                requireContext().resources.getString(R.string.loading)
            )
            ApiCall.instance.getMyHealthDiary(preferences!!.getprofileid(), this)
        }

        if (preferences!!.fullName != null && !TextUtils.isEmpty(preferences!!.fullName)) {
            binding.tvProfileName.visibility = View.VISIBLE
            binding.tvProfileName.text = "Hi " + preferences!!.fullName!!.split(" ")[0]
        } else {
            binding.tvProfileName.visibility = View.GONE
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.llStepCount.visibility = View.VISIBLE
        } else {
            binding.llStepCount.visibility = View.GONE
        }
        binding.llSugarCount.setOnClickListener {

            val intent = Intent(requireActivity(), ViewSugarActivity::class.java)
            startActivity(intent)

            val properties = Properties()
            properties.addAttribute("isClickSugar", true)
            MoEAnalyticsHelper.trackEvent(requireActivity(), "ClickSugarDiary", properties)
        }

        binding.llWeight.setOnClickListener {

            val intent = Intent(requireActivity(), ViewWeightActivity::class.java)
            startActivity(intent)

            val properties = Properties()
            properties.addAttribute("isClickWeight", true)
            MoEAnalyticsHelper.trackEvent(requireActivity(), "ClickWeightDiary", properties)
        }

        binding.llBP.setOnClickListener {

            val intent = Intent(requireActivity(), ViewBPActivity::class.java)
            startActivity(intent)

            val properties = Properties()
            properties.addAttribute("isClickBP", true)
            MoEAnalyticsHelper.trackEvent(requireActivity(), "ClickBPDiary", properties)
        }

        binding.llStepCount.setOnClickListener {

            val properties = Properties()
            properties.addAttribute("isClickStep", true)
            MoEAnalyticsHelper.trackEvent(requireActivity(), "ClickStepDiary", properties)

            if (preferences!!.isFitConnect) {
                val intent = Intent(activity, GoogleFitStepActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(requireActivity(), StepGoalActivity::class.java)
                startActivity(intent)
            }
        }

        binding.llHealthPlan.setOnClickListener {

            /*val intent = Intent(requireActivity(), WebViewActivity::class.java)
            intent.putExtra("navType", "Diary")
            intent.putExtra("docUrl", dietPlanUrl)
            intent.putExtra("docName", heading)
            startActivity(intent)*/

            if (bannerType == "diet_plan_new") {
                val intent = Intent(MainApplication.currentActivity!!, DietChartActivity::class.java)
                startActivity(intent)

                val properties = Properties()
                properties.addAttribute("isCLickHealthPlan", true)
                MoEAnalyticsHelper.trackEvent(requireActivity(), "ClickHealthPlanDiaryNew", properties)

            } else {
                val properties = Properties()
                properties.addAttribute("isCLickHealthPlan", true)
                MoEAnalyticsHelper.trackEvent(requireActivity(), "ClickHealthPlanDiary", properties)

                val intent = Intent(requireActivity(), WebViewActivity::class.java)
                intent.putExtra("navType", "Diary")
                intent.putExtra("docUrl", dietPlanUrl)
                intent.putExtra("docName", heading)
                startActivity(intent)
            }

        }

        binding.llFoodDiary.setOnClickListener {
            callApiForCheckFoodDiaryList()

            val properties = Properties()
            properties.addAttribute("isClickFood", true)
            MoEAnalyticsHelper.trackEvent(requireActivity(), "ClickFoodDiary", properties)
        }

        binding.rlHealthScore.setOnClickListener {
            val intent = Intent(requireActivity(), AnalyticsReportActivity::class.java)
            startActivity(intent)

            val properties = Properties()
            properties.addAttribute("isCLickMyHealthScore", true)
            MoEAnalyticsHelper.trackEvent(requireActivity(), "ClickMyHealthScoreDiary", properties)
        }

        binding.rlMyHealthTeam.setOnClickListener {

            val intent = Intent(requireActivity(), MyCareTeamActivity::class.java)
            startActivity(intent)

            val properties = Properties()
            properties.addAttribute("isCLickMyHealthTeam", true)
            MoEAnalyticsHelper.trackEvent(requireActivity(), "ClickMyHeathTeamDiary", properties)
        }

        binding.rlMyCarePlan.setOnClickListener {

            val intent = Intent(requireActivity(), MyCarePlanActivity::class.java)
            startActivity(intent)

            val properties = Properties()
            properties.addAttribute("isCLickMyCarePlan", true)
            MoEAnalyticsHelper.trackEvent(requireActivity(), "ClickMyCarePlanDiary", properties)
        }

        binding.rlAllergy.setOnClickListener {

            val intent = Intent(requireActivity(), AllergyActivity::class.java)
            startActivity(intent)

            val properties = Properties()
            properties.addAttribute("isCLickAllergy", true)
            MoEAnalyticsHelper.trackEvent(requireActivity(), "ClickAllergyDiary", properties)
        }

        binding.rlPastSummary.setOnClickListener {
            val intent = Intent(requireActivity(), SummeryActivity::class.java)
            startActivity(intent)
            val properties = Properties()
            properties.addAttribute("isCLickSummery", true)
            MoEAnalyticsHelper.trackEvent(requireActivity(), "ClickPastSummeryDiary", properties)
        }

        binding.rlMedicine.setOnClickListener {
            callApiForCheckMedicineList()
            val properties = Properties()
            properties.addAttribute("isCLickMedicine", true)
            MoEAnalyticsHelper.trackEvent(requireActivity(), "ClickMedicineDiary", properties)
        }

        binding.rlDocuments.setOnClickListener {
            val intent = Intent(requireActivity(), DocumentListActivity::class.java)
            startActivity(intent)

            val properties = Properties()
            properties.addAttribute("isCLickDocument", true)
            MoEAnalyticsHelper.trackEvent(requireActivity(), "ClickDocumentDiary", properties)
        }

        binding.imgNotification.setOnClickListener {
            val intent = Intent(requireActivity(), NotificationListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun callApiForCheckFoodDiaryList() {
        if (CheckConnection.isConnection(MainApplication.currentActivity!!)) {
            ApiCall.instance
                .getMyFoodDiaryList(preferences!!.getprofileid(), getCurrentDate(), this)
        } else {
            val intent = Intent(activity, FoodDiaryActivity::class.java)
            intent.putExtra("navigationType", "addFoodDiary")
            startActivity(intent)
        }
    }

    private fun callApiForCheckMedicineList() {
        if (CheckConnection.isConnection(requireActivity())) {
            ApiCall.instance.getMyMedicineList(preferences!!.getprofileid(), this)
        } else {
            val intent = Intent(activity, MedicineListActivity::class.java)
            intent.putExtra("navigationType", "medicines")
            startActivity(intent)
        }
    }

    private fun getCurrentDate(): String? {
        val yearFormat = SimpleDateFormat("yyyy-MM-dd")
        val d = Date(System.currentTimeMillis())
        return yearFormat.format(d)
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "myFoodDiaryList") {
            val response = data as Response<GetMyFoodDiaryResponse>
            if (response.isSuccessful) {
                if (response.isSuccessful && response.body()!!.errorCode == "1") {
                    if (response.body()!!.data != null && response.body()!!.data!!.size > 0) {
                        if (requireActivity()!=null && isAdded) {
                            val intent = Intent(requireActivity(), FoodDiaryActivity::class.java)
                            intent.putExtra("navigationType", "viewFoodDiary")
                            startActivity(intent)
                        }
                    } else {
                        if (requireActivity()!=null && isAdded) {
                            val intent = Intent(requireContext(), FoodDiaryActivity::class.java)
                            intent.putExtra("navigationType", "addFoodDiary")
                            startActivity(intent)
                        }
                    }
                } else {
                    if (requireActivity()!=null && isAdded) {
                        val intent = Intent(requireContext(), FoodDiaryActivity::class.java)
                        intent.putExtra("navigationType", "addFoodDiary")
                        startActivity(intent)
                    }
                }
            }
        }
        if (type == "myMedicine") {
            val response = data as Response<MyMedicineResponse>
            if (response.isSuccessful) {
                if (response.isSuccessful && response.body()!!.errorCode == "1") {
                    if (response.body()!!.data != null && response.body()!!.data.size > 0) {
                        if (requireActivity()!=null && isAdded) {
                            val intent = Intent(requireActivity(), MedicineListActivity::class.java)
                            intent.putExtra("navigationType", "viewMedicines")
                            startActivity(intent)
                        }
                    } else {
                        if (requireActivity()!=null && isAdded) {
                            val intent = Intent(requireActivity(), MedicineListActivity::class.java)
                            intent.putExtra("navigationType", "medicines")
                            startActivity(intent)
                        }
                    }
                } else {
                    if (requireActivity()!=null && isAdded) {
                        val intent = Intent(requireActivity(), MedicineListActivity::class.java)
                        intent.putExtra("navigationType", "medicines")
                        startActivity(intent)
                    }
                }
            }
        }

        if (type == "myHealthDiary") {
            val response = data as Response<MyHealthDiaryResponse>
            if (response.isSuccessful) {
                if (response.isSuccessful && response.body()!!.errorCode == "0") {
                    if (response.body()!!.health_summary != null) {
                        if (response.body()!!.health_summary.health_summary == "false") {
                            binding.llHealthPlan.visibility = View.GONE
                        } else {
                            binding.llHealthPlan.visibility = View.VISIBLE
                            binding.tvTitle.text = response.body()!!.health_summary.heading
                            binding.tvDesc.text = response.body()!!.health_summary.details

                            dietPlanUrl = response.body()!!.health_summary.diet_plan_url
                            heading = response.body()!!.health_summary.heading
                            bannerType = response.body()!!.health_summary.banner_type
                        }
                    }
                }
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }
}