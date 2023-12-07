package app.oxyjon.bean

data class StressManagementResponse(
    val errorCode: String,
    val errorMsg: String,
    val stress_managemnet: ArrayList<StressManagemnet>
) {
    data class StressManagemnet(
        val contenttype: String,
        val detail_url: String,
        val details: String,
        val heading: String,
        val id: String,
        val image_url: String,
        val video_url: String
    )
}