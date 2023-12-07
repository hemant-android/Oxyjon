package app.oxyjon.ui.kotlin.fragment.plan

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import app.oxyjon.R
import app.oxyjon.bean.PlanListResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.FragmentPlanBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.retrofit.response.CommonResponse
import app.oxyjon.ui.kotlin.activity.DashboardActivity
import app.oxyjon.ui.kotlin.activity.DietPlanDetailActivity
import app.oxyjon.ui.kotlin.activity.DoctorConsultationActivity
import app.oxyjon.ui.kotlin.activity.NotificationListActivity
import app.oxyjon.ui.kotlin.activity.PlanDetailActivity
import app.oxyjon.ui.kotlin.fragment.plan.adapter.DietPlanAdapter
import app.oxyjon.ui.kotlin.fragment.plan.adapter.DoctorConsultationPlanAdapter
import app.oxyjon.ui.kotlin.fragment.plan.adapter.MostSuggestedPlanAdapter
import app.oxyjon.ui.kotlin.fragment.plan.adapter.SugarReductionPlanAdapter
import app.oxyjon.utils.CheckConnection
import app.oxyjon.utils.FunctionHelper
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper
import retrofit2.Response

class PlanFragment : Fragment(), SugarReductionPlanAdapter.onClickListner,
    DietPlanAdapter.onClickListner, DoctorConsultationPlanAdapter.onClickListner,
    MostSuggestedPlanAdapter.onClickListner, IApiCallback {
    private lateinit var planListArr: ArrayList<PlanListResponse.Data>
    private lateinit var binding: FragmentPlanBinding
    var preferences: AppSharedPreferences? = null
    private val mSugarReductionPlanAdapter: SugarReductionPlanAdapter by lazy {
        SugarReductionPlanAdapter(requireContext())
    }
    private val mDietPlanAdapter: DietPlanAdapter by lazy { DietPlanAdapter(requireContext()) }
    private val mDoctorConsultationPlanAdapter: DoctorConsultationPlanAdapter by lazy {
        DoctorConsultationPlanAdapter(requireContext())
    }
    private val mMostSuggestedPlanAdapter: MostSuggestedPlanAdapter by lazy {
        MostSuggestedPlanAdapter(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPlanBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences = AppSharedPreferences.getInstance(requireActivity())

        binding.rvSugarReductionPlan.adapter = mSugarReductionPlanAdapter
        binding.rvDietPlan.adapter = mDietPlanAdapter
        binding.rvDoctorConsultationPlan.adapter = mDoctorConsultationPlanAdapter
        binding.rvMostSuggestedPlan.adapter = mMostSuggestedPlanAdapter

        mSugarReductionPlanAdapter.setClickListner(this)
        mDietPlanAdapter.setClickListner(this)
        mDoctorConsultationPlanAdapter.setClickListner(this)
        mMostSuggestedPlanAdapter.setClickListner(this)

        if (preferences!!.fullName != null && !TextUtils.isEmpty(preferences!!.fullName)) {
            binding.tvProfileName.visibility = View.VISIBLE
            binding.tvProfileName.text = "Hi " + preferences!!.fullName!!.split(" ")[0]
        } else {
            binding.tvProfileName.visibility = View.GONE
        }

        if (CheckConnection.isConnection(requireActivity())) {
            FunctionHelper.disable_user_Intration(
                context, requireContext().resources.getString(R.string.loading)
            )
            ApiCall.instance.getHealthplan(this)
        } else {
            Toast.makeText(context, "please check your internet connection", Toast.LENGTH_SHORT)
                .show()
        }

        if (CheckConnection.isConnection(requireActivity())) {
            ApiCall.instance.getUserPaidOrNot(this)
        }

        binding.imgNotification.setOnClickListener {
            val intent = Intent(requireActivity(), NotificationListActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSelectMostSuggestedPlan(planId: Int?, planType: String?) {
        val intent = Intent(requireActivity(), PlanDetailActivity::class.java)
        intent.putExtra("planId", planId)
        intent.putExtra("planName", "Health plan")
        startActivity(intent)
    }

    override fun onSelectDietPlan(planId: Int?, type: String?) {
        val properties = Properties()
        properties.addAttribute("isCLick", true)
        properties.addAttribute("planType", type)
        properties.addAttribute("planId", planId)
        MoEAnalyticsHelper.trackEvent(requireActivity(), "buyDietPlanSection", properties)

        val intent = Intent(requireActivity(), DietPlanDetailActivity::class.java)
        intent.putExtra("planId", planId)
        intent.putExtra("planName", "Diet plan")
        startActivity(intent)
    }

    override fun onSelectDoctorConsultationPlan(planId: Int?, type: String?) {
        val properties = Properties()
        properties.addAttribute("isCLick", true)
        properties.addAttribute("planType", type)
        properties.addAttribute("planId", planId)
        MoEAnalyticsHelper.trackEvent(
            requireActivity(), "buyDoctorConsultationPlanSection", properties
        )

        val intent = Intent(requireActivity(), DoctorConsultationActivity::class.java)
        intent.putExtra("planId", planId)
        intent.putExtra("planName", "Doctor consultation")
        startActivity(intent)

    }

    override fun onSelectSugarPlan(planId: Int?, type: String?) {
        val properties = Properties()
        properties.addAttribute("isCLick", true)
        properties.addAttribute("planType", type)
        properties.addAttribute("planId", planId)
        MoEAnalyticsHelper.trackEvent(requireActivity(), "buySugarPlanSection", properties)
        when (type) {
            "doctor_consultation" -> {
                val intent = Intent(requireActivity(), DoctorConsultationActivity::class.java)
                intent.putExtra("planId", planId)
                intent.putExtra("planName", "Doctor consultation")
                startActivity(intent)
            }

            "educator_consultation" -> {
                val intent = Intent(requireActivity(), DietPlanDetailActivity::class.java)
                intent.putExtra("planId", planId)
                intent.putExtra("planName", "Diet plan")
                startActivity(intent)
            }

            else -> {
                val intent = Intent(requireActivity(), PlanDetailActivity::class.java)
                intent.putExtra("planId", planId)
                intent.putExtra("planName", "Health plan")
                startActivity(intent)
            }
        }
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "healthplan") {
            val response = data as Response<PlanListResponse>
            if (response.isSuccessful) {
                if (response.isSuccessful && response.body()!!.errorCode == "0") {
                    if (response.body()!!.seggested != null && response.body()!!.seggested?.size!! > 0) {
                        binding.tvMostSuggestedTitle.visibility = View.VISIBLE
                        binding.rvMostSuggestedPlan.visibility = View.VISIBLE
                        mMostSuggestedPlanAdapter.setData(response.body()!!.seggested)
                    } else {
                        binding.tvMostSuggestedTitle.visibility = View.GONE
                        binding.rvMostSuggestedPlan.visibility = View.GONE
                    }
                    if (response.body()!!.data != null && response.body()!!.data?.size!! > 0) {

                        binding.llAllPlan.visibility = View.VISIBLE
                        planListArr = response.body()!!.data

                        var sugarReductionArr =
                            response.body()!!.data.filter { it.plan_type == "health_plans" }
                        var doctorConsultationArr =
                            response.body()!!.data.filter { it.plan_type == "doctor_consultation" }
                        var dietPlanArr =
                            response.body()!!.data.filter { it.plan_type == "educator_consultation" }

                        if (sugarReductionArr != null && sugarReductionArr.isNotEmpty()) {
                            binding.rvSugarReductionPlan.visibility = View.VISIBLE
                            binding.tvSugarReductionPlan.visibility = View.VISIBLE
                            mSugarReductionPlanAdapter.setData(sugarReductionArr as ArrayList<PlanListResponse.Data>)
                        } else {
                            binding.rvSugarReductionPlan.visibility = View.GONE
                            binding.tvSugarReductionPlan.visibility = View.GONE
                        }

                        if (doctorConsultationArr != null && doctorConsultationArr.isNotEmpty()) {
                            binding.rvDoctorConsultationPlan.visibility = View.VISIBLE
                            binding.tvDoctorConsultationPlan.visibility = View.VISIBLE
                            mDoctorConsultationPlanAdapter.setData(doctorConsultationArr as ArrayList<PlanListResponse.Data>)
                        } else {
                            binding.rvDoctorConsultationPlan.visibility = View.GONE
                            binding.tvDoctorConsultationPlan.visibility = View.GONE
                        }

                        if (dietPlanArr != null && dietPlanArr.isNotEmpty()) {
                            binding.rvDietPlan.visibility = View.VISIBLE
                            binding.tvDietPlan.visibility = View.VISIBLE
                            mDietPlanAdapter.setData(dietPlanArr as ArrayList<PlanListResponse.Data>)
                        } else {
                            binding.rvDietPlan.visibility = View.GONE
                            binding.tvDietPlan.visibility = View.GONE
                        }
                    } else {
                        binding.llAllPlan.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }

}