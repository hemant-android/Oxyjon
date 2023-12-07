package app.oxyjon.bean

data class IntroVideoResponse(
    val `data`: Data,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val action_button: String,
        val videomessage: String,
        val videourl: String
    )
}