package app.oxyjon.bean

data class UpdateQuestionFirstResponse(
    val `data`: Data,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val birth_date: String,
        val customer_id: Int,
        val gender: String,
        val name: String,
        val profile_type: String,
        val screen_quest2: Boolean,
        val profileId: Int
    )
}