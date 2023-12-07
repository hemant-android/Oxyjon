package app.oxyjon.bean

data class DoctorConsultationResponse(
    val `data`: Data,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val about_doctor: String,
        val consultation_count: String,
        val profile_image: String,
        val consultation_type: String,
        val doctor_name: String,
        val education: String,
        val id: String,
        val plan_title: String,
        val price_offnline_consultation: String,
        val price_online_consultation: String,
        val ratings: String,
        val review: ArrayList<Review>
    ) {
        data class Review(
            val image_url: String,
            val user_name: String,
            val review_star: String,
            val review: String
        )
    }
}