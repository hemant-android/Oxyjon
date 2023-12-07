package app.oxyjon.retrofit.response

data class StepCountResponse(
    val `data`: List<Data>,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val customer_id: Int,
        val profile_id: Int,
        val stepcount: String,
        val stepcount_Date: String
    )
}