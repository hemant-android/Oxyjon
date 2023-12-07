package app.oxyjon.bean

data class GetProfileResponse(
    val `data`: Data,
    val errorCode: String,
    val errorMsg: String,
) {
    data class Data(
        val age: String,
        val customer_id: String,
        val email: String,
        val gender: String,
        val height_ft: String,
        val height_inches: String,
        val name: String,
        val profileId: String,
        val weight: String,
        val mobile: String,
        val activity_score: Double,
        val activity_list: ArrayList<Activity>,
    ) {
        data class Activity(
            val key: String,
            val value: String,
            var selectedOption: Boolean = false,
        )
    }
}