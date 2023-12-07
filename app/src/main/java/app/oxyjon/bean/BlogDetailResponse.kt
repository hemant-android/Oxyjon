package app.oxyjon.bean

data class BlogDetailResponse(
    val `data`: ArrayList<Data>,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val contenttype: String,
        val detail_url: String,
        val details: String,
        val heading: String,
        val id: Int,
        val image_url: String,
        val video_url: String
    )
}