package app.oxyjon.bean

data class PhysicalActivityResponse(
    val activity_remarks: String,
    val `data`: ArrayList<Data>,
    val errorCode: String,
    val errorMsg: String,
) {
    data class Data(
        val activity_data: List<ActivityData>,
        val activity_day: String,
        val activity_day_remarks: String
    ) {
        data class ActivityData(
            val content_type: String,
            val id: String,
            val activity_details: String,
            val activity_image: String,
            val activity_title: String,
            val activity_video_url: String
        )
    }
}