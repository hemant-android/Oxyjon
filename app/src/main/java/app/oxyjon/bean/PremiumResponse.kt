package app.oxyjon.bean

data class PremiumResponse(
//    val `data`: Data,
    val errorCode: String,
    val errorMsg: String,
    val is_premium_member: String,
    val on_board_status: String,
    val subscription_status: String,
    val premium_message: String,
    val profile_type: String,
    val health_plan_id: String,
){
    data class Data(
        val health_plan_name: String,
        val health_plan_payment_status: String,
        val health_plan_price: String,
        val healthplan_active_no_of_days: String,
        val healthplan_id: String,
        val no_of_doctor_consultation: String,
        val no_of_educator_consultation: String,
        val on_board_status: String,
        val plan_end_date: String,
        val plan_start_date: String,
        val plan_status: String,
        val subscription_status: String
    )
}