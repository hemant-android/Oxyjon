package app.oxyjon.bean

data class ThingsToDoAvoidResponse(
    val `data`: Data,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val eating_patterns: ArrayList<String>,
        val others_do_not: ArrayList<String>,
        val others_to_do: ArrayList<String>,
        val to_do: ArrayList<String>,
        val to_do_not: ArrayList<String>
    )
}