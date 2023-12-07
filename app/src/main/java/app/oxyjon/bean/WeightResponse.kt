package app.oxyjon.bean

data class WeightResponse(
    val `data`: ArrayList<Data>,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val date: String,
        val id: String,
        val profile_id: String,
        val unit: String,
        val weight: String
    )
}