package app.oxyjon.ui.kotlin.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.bean.MyCareTeamResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityMyCareTeamBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.utils.CheckConnection
import app.oxyjon.utils.FunctionHelper
import com.bumptech.glide.Glide
import retrofit2.Response

class MyCareTeamActivity : BaseActivity(), IApiCallback {
    lateinit var binding: ActivityMyCareTeamBinding
    private var healthPlanId: String? = ""
    private var healthPlanIdDoctor: String? = ""
    private var isOnBoarded: Boolean? = false
    var preferences: AppSharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyCareTeamBinding.inflate(layoutInflater)
        setContentView(binding.root)


        preferences = AppSharedPreferences.getInstance(this)

        MainApplication.currentActivity = this

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.tvAddFamilyMember.setOnClickListener {
            val intent = Intent(this, AddFamilyMemberActivity::class.java)
            startActivity(intent)
        }

        binding.imgNoEducator.setOnClickListener {
            val intent = Intent(this, PlanDetailActivity::class.java)
            intent.putExtra("planId", healthPlanId!!.toInt())
            startActivity(intent)
        }

        binding.llDoctor.setOnClickListener {
            if (!isOnBoarded!!) {
                val intent = Intent(this, DoctorConsultationActivity::class.java)
                intent.putExtra("planId", healthPlanIdDoctor!!.toInt())
                startActivity(intent)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this

        if (CheckConnection.isConnection(this)) {
            FunctionHelper.disable_user_Intration(
                this,
                resources.getString(R.string.loading)
            )
            ApiCall.instance.getMyCareTeam(preferences!!.getprofileid(), this)
        } else {
            Toast.makeText(
                this,
                "please check your internet connection",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "myCareTeam") {
            val response = data as Response<MyCareTeamResponse>
            if (response.isSuccessful) {
                if (response.isSuccessful && response.body()!!.errorCode == "1") {

                    healthPlanId = response.body()!!.health_plan_id
                    isOnBoarded = response.body()!!.is_on_boarded

                    if (response.body()!!.profile_type != null && response.body()!!.profile_type == "B2B") {
                        binding.imgB2BBanner.visibility = View.VISIBLE

                        Glide.with(this)
                            .load(response.body()!!.b2b_banner)
                            .placeholder(R.drawable.progress_animation)
                            .into(binding.imgB2BBanner)
                    } else {
                        binding.imgB2BBanner.visibility = View.GONE
                    }

                    if (response.body()!!.educator?.size!! > 0) {
                        binding.llEducator.visibility = View.VISIBLE
                        binding.tvEducatorTxt.visibility = View.VISIBLE
                        binding.imgNoEducator.visibility = View.GONE
                        binding.tvEducatorName.text = response.body()!!.educator[0].eductaor_name
                        binding.tvEducatorDetail.text = response.body()!!.educator[0].about
                        binding.tvEducatorNumber.text = response.body()!!.educator[0].contact_no
                        Glide.with(this)
                            .load(response.body()!!.educator[0].profile_url)
                            .placeholder(R.drawable.progress_animation).into(binding.imgEducator)

                    } else {
                        binding.llEducator.visibility = View.GONE
                        binding.tvEducatorTxt.visibility = View.GONE

                        if (response.body()!!.profile_type != null && response.body()!!.profile_type == "B2B") {
                            binding.imgNoEducator.visibility = View.GONE
                        } else {
                            try {
                                if (response.body()!!.banner_no_onboarding != null && !TextUtils.isEmpty(
                                        response.body()!!.banner_no_onboarding
                                    )
                                ) {
                                    binding.imgNoEducator.visibility = View.VISIBLE

                                    Glide.with(this)
                                        .load(response.body()!!.banner_no_onboarding)
                                        .placeholder(R.drawable.progress_animation)
                                        .into(binding.imgNoEducator)
                                } else {
                                    binding.imgNoEducator.visibility = View.GONE
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }

                    }

                    if (response.body()!!.mydoctor?.size!! > 0) {
                        binding.llDoctor.visibility = View.VISIBLE
                        healthPlanIdDoctor = response.body()!!.mydoctor[0].health_plan_id
                        binding.tvDrName.text = response.body()!!.mydoctor[0].name
                        binding.tvDrSpecialist.text = response.body()!!.mydoctor[0].details
                        binding.ratingBar.rating =
                            response.body()!!.mydoctor[0].review_star.toFloat()
                        Glide.with(this).load(response.body()!!.mydoctor[0].profile_url)
                            .placeholder(R.drawable.progress_animation).into(binding.imgDoctor)

                    } else {
                        binding.llDoctor.visibility = View.GONE
                    }


                    if (response.body()!!.caregiver != null) {
                        if (response.body()!!.caregiver?.caregiver_name != "null" && !TextUtils.isEmpty(
                                response.body()!!.caregiver?.caregiver_name
                            )
                        ) {
                            binding.tvName.text = response.body()!!.caregiver?.caregiver_name
                            binding.llName.visibility = View.VISIBLE
                            binding.llFamily.visibility = View.VISIBLE
                        } else {
                            binding.llName.visibility = View.GONE
                            binding.llFamily.visibility = View.GONE
                        }
                        if (response.body()!!.caregiver?.caregiver_relation != "null" && !TextUtils.isEmpty(
                                response.body()!!.caregiver?.caregiver_relation
                            )
                        ) {
                            binding.tvRelation.text =
                                response.body()!!.caregiver?.caregiver_relation
                            binding.llRelationShip.visibility = View.VISIBLE
                        } else {
                            binding.llRelationShip.visibility = View.GONE
                        }

                        if (response.body()!!.caregiver?.caregiver_mobile_no != "null" && !TextUtils.isEmpty(
                                response.body()!!.caregiver?.caregiver_mobile_no
                            )
                        ) {
                            binding.tvMobileNumber.text =
                                response.body()!!.caregiver?.caregiver_mobile_no
                            binding.llNumber.visibility = View.VISIBLE
                        } else {
                            binding.llNumber.visibility = View.GONE
                        }
                        if (response.body()!!.caregiver?.caregiver_note != null && response.body()!!.caregiver?.caregiver_note != "null") {
                            binding.tvNote.text = response.body()!!.caregiver?.caregiver_note
                        }
                    } else {
                        binding.llFamily.visibility = View.GONE
                    }
                }
            }

        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }
}