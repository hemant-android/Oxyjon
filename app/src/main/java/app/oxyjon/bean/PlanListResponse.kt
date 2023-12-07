package app.oxyjon.bean

data class PlanListResponse(
    val `data`: ArrayList<Data>,
    val `seggested`: ArrayList<Seggested>,
    val errorCode: String,
    val errorMsg: String,
) {
    data class Data(
        val health_plan_id: Int,
        val plan_details: String,
        val plan_title: String,
        val plan_duration: Int,
        val plan_type: String,
        val price: String,
    )

    data class Seggested(
        val health_plan_id: Int,
        val plan_title: String,
        val plan_details: String,
        val price: String,
        val plan_duration: Int,
        val plan_type: String,
        val banner_url: String,
    )
}