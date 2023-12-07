package app.oxyjon.bean

data class SugarDetailResponse(
    val `data`: ArrayList<Data>,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val date: String,
        val sugar_level: String,
        val type: String
    )
}