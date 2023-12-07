package app.oxyjon.bean

data class UpdateQuestionThirdResponse(
    val `data`: Data,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val activity_level: String,
        val blood_pressure: String,
        val bmi: String,
        val body_type: String,
        val calories_intake: String,
        val customer_id: String,
        val heart_disease: String,
        val high_cholesterol: String,
        val kidney_disease: String,
        val profileId: String
    )
}