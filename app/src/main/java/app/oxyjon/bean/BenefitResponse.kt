package app.oxyjon.bean

data class BenefitResponse(
    val `data`: Data,
    val errorCode: String,
    val errorMsg: String,
) {
    data class Data(
        val benefit: ArrayList<Benefit>,
        val plan_list: ArrayList<Plan>,
        val plan_list_ui2: ArrayList<Plan>,
        val review_list: ArrayList<Review>,
        val screen_quest1: Boolean,
        val screen_quest2: Boolean,
        val screen_quest3: Boolean,
        val payment_status: String,
        val on_board_status: String,
        val health_plan_heading: String,
        val is_hp_select_ui_active: String,
        val top_banner_url: String
    ) {
        data class Benefit(
            val details: String,
            val title: String,
        )

        data class Plan(
            val icon_url: String,
            val banner_url: String,
            val plan_detail: String,
            val plan_id: String,
            val plan_type: String,
            val plan_name: String,
            val plan_price: String,
            val what_we_offer: ArrayList<String>
        )

        data class Review(
            val image_url: String,
            val review: String,
            val review_star: String,
            val user_name: String,
        )
    }
}