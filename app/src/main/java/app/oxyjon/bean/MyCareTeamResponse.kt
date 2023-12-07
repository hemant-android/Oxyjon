package app.oxyjon.bean

data class MyCareTeamResponse(
    val banner_no_onboarding: String,
    val plan_type: String,
    val is_premium_member: String,
    val on_board_status: String,
    val subscription_status: String,
    val premium_message: String,
    val profile_type: String,
    val b2b_banner: String,
    val health_plan_id: String,
    val is_on_boarded: Boolean,
    val caregiver: Caregiver,
    val b2b_health_plan: B2bHealthPlan,
    val healthplan_data: ArrayList<HealthPlanData>,
    val educator: ArrayList<Educator>,
    val errorCode: String,
    val errorMsg: String,
    val mydoctor: ArrayList<Mydoctor>,
    val profileId: String,
    val no_of_doc_call_left: Int,
    val no_of_educator_call_left: Int,
) {
    data class Caregiver(
        val caregiver_mobile_no: String,
        val caregiver_name: String,
        val caregiver_note: String,
        val caregiver_relation: String,
    )

    data class HealthPlanData(
        val health_plan_name: String,
        val health_plan_id: String,
        val plan_start_date: String,
        val plan_end_date: String,
        val healthplan_active_no_of_days: String,
        val no_of_doctor_consultation: String,
        val no_of_educator_consultation: String,
        val health_plan_price: String,
        val health_plan_banner: String,
    )

    data class Educator(
        val eductaor_name: String,
        val about: String,
        val contact_no: String,
        val profile_url: String,
    )

    data class Mydoctor(
        val plan_type: String,
        val health_plan_id: String,
        val name: String,
        val details: String,
        val profile_url: String,
        val review_star: String,
    )

    data class B2bHealthPlan(
        val my_plan_id: String,
        val health_plan_name: String,
        val health_plan_id: String,
        val plan_start_date: String,
        val plan_end_date: String,
        val healthplan_active_no_of_days: String,
        val no_of_doctor_consultation: String,
        val no_of_educator_consultation: String,
        val health_plan_price: String,
        val health_plan_banner: String,
        val other_details: String,
        val health_plan_detail: ArrayList<String>
    )
}