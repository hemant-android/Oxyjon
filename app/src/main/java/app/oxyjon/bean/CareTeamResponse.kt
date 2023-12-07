package app.oxyjon.bean

data class CareTeamResponse(
    val banner_no_onboarding: String,
    val caregiver: Caregiver,
    val educator: List<Any>,
    val errorCode: String,
    val errorMsg: String,
    val mydoctor: ArrayList<Mydoctor>,
    val profileId: String
) {
    data class Caregiver(
        val caregiver_mobile_no: Any,
        val caregiver_name: Any,
        val caregiver_note: Any,
        val caregiver_relation: Any
    )

    data class Mydoctor(
        val details: String,
        val name: String,
        val profile_url: String,
        val review_star: String
    )
}