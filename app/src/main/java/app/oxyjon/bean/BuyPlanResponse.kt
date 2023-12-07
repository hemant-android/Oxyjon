package app.oxyjon.bean

data class BuyPlanResponse(
    val `data`: Data,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val health_plan_name: String,
        val health_plan_payment_status: String,
        val payment_order_id: String,
        val health_plan_price: String,
        val id: String,
        val profile_id: String
    )
}