package app.oxyjon.bean

data class DashboardResponse(
    val `data`: ArrayList<Data>,
    val errorCode: String,
    val errorMsg: String,
) {
    data class Data(
        val action_box: ArrayList<ActionBox>,
        val bloodtestplan: ArrayList<Bloodtestplan>,
        val doctor: ArrayList<Doctor>,
        val profile_info: ProfileInfo,
        val popups_addsugar: PopupsAddSugar,
        val popups_foodDiary: PopupsFoodDiary,
        val popups_stepCounter: PopupsStepCounter,
        val google_fit: GoogleFit,
        val healthplan: ArrayList<Healthplan>,
        val newsfeed: ArrayList<Newsfeed>,
        val promotion_block: ArrayList<PromotionBlock>,
        val top_banner_list: ArrayList<TopBanner>,
        val recipes: ArrayList<Recipes>,
        val analytics_data: String,
        val analytics_message: String,
        val top_b2b_banner: String,
        val profile_type: String,
    ) {
        data class ActionBox(
            val ui_icons: String,
            val action_details: String,
            val action_name: String,
            val action_value: String,
        )

        data class Bloodtestplan(
            val plan_details: String,
            val bloodtestname: String,
            val s_price: String,
            val id: Int,
        )

        data class GoogleFit(
            val is_connected: String,
            val message: String,
            val stepcount: String,
            val goal_data_point: String,
            val detail_message: String,
            val profile_goal: String,
        )

        data class ProfileInfo(
            val name: String,
            val gender: String,
            val height_ft: String,
            val height_inches: String,
            val weight: String,
        )

        data class Doctor(
            val doctor_name: String,
            val doctor_id: String,
            val doctor_qualification: String,
            val doctor_address: String,
            val doctor_contact_no: String,
            val profile_picture: String,
            val qr_code_img: Any,
            val doctor_code: Any,
        )

        data class PopupsAddSugar(
            val pop_name: String,
            val pop_action: String,
            val pop_title: String,
            val pop_details: String,
            val pop_button_name: String,
            val pop_can_skip: String,
            val pop_shows_nooftimes: String,
            val pop_shows_count: String,
        )

        data class PopupsFoodDiary(
            val pop_name: String,
            val pop_action: String,
            val pop_title: String,
            val pop_details: String,
            val pop_button_name: String,
            val pop_can_skip: String,
            val pop_shows_nooftimes: String,
            val pop_shows_count: String,
        )

        data class PopupsStepCounter(
            val pop_name: String,
            val pop_action: String,
            val pop_title: String,
            val pop_details: String,
            val pop_button_name: String,
            val pop_can_skip: String,
            val pop_shows_nooftimes: String,
            val pop_shows_count: String,
        )

        data class Healthplan(
            val healthplan_active_no_of_days: Int,
            val healthplan_details: String,
            val healthplan_name: String,
            val healthplan_s_price: String,
            val plan_type: String,
            val id: Int,
        )

        data class Newsfeed(
            val contenttype: String,
            val detail_url: String,
            val details: String,
            val heading: String,
            val id: String,
            val url: String,
        )

        data class PromotionBlock(
            val image_link: String,
            val image_url: String,
        )

        data class TopBanner(
            val image_link: String,
            val plan_id: String,
            val image_url: String,
            val banner_type: String,
        )

        data class Recipes(
            val contenttype: String,
            val id: String,
            val heading: String,
            val details: String,
            val url: String,
            val detail_url: String,
        )
    }
}