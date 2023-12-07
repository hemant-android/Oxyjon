package app.oxyjon.bean

data class NotificationListResponse(
    val `data`: ArrayList<Data>,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val id: String,
        val contenttype: String,
        val details: String,
        val heading: String,
        val is_delete: String,
        val notificationtype: String,
        val url: String,
        val detail_url: String
    )
}