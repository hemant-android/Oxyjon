package app.oxyjon.bean

data class UpdateProfileResponse(
    val `data`: Data,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val activity_list: List<Activity>,
        val activity_score: Double,
        val age: Int,
        val customer_id: Int,
        val email: Any,
        val gender: String,
        val height_ft: Int,
        val height_inches: Int,
        val mobile: Long,
        val name: String,
        val profileId: Int,
        val weight: String
    ) {
        data class Activity(
            val key: String,
            val value: String
        )
    }
}