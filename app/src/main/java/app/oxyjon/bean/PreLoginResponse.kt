package app.oxyjon.bean

data class PreLoginResponse(
    val `data`: Data,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val fcmtoken: String,
        val auth_token: String
    )
}