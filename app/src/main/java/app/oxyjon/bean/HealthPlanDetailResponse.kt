package app.oxyjon.bean

data class HealthPlanDetailResponse(
    val errorCode: String,
    val errorMsg: String,
    val healthplan_details: HealthplanDetails
) {
    data class HealthplanDetails(
        val banner: String,
        val benefits: ArrayList<Benefit>,
        val careteam: ArrayList<Careteam>,
        val consultation: ArrayList<Consultation>,
        val id: Int,
        val plan_details: String,
        val plan_title: String,
        val plan_duration: Int,
        val price: String,
        val review: ArrayList<Review>
    ) {
        data class Benefit(
            val name: String
        )

        data class Careteam(
            val details: String,
            val name: String,
            val profile_url: String,
            val review_star: String,
            val type: String
        )

        data class Consultation(
            val count: String,
            val name: String,
            val type: String
        )

        data class Review(
            val image_url: String,
            val review: String,
            val review_star: String,
            val user_name: String
        )
    }
}