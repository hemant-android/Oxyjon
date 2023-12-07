package app.oxyjon.bean

data class MyHealthDiaryResponse(
    val errorCode: String,
    val errorMsg: String,
    val health_summary: HealthSummary
) {
    data class HealthSummary(
        val details: String,
        val diet_plan_url: String,
        val heading: String,
        val health_summary: String,
        val banner_type: String,
    )
}