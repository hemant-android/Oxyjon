package app.oxyjon.bean

data class BuyBookTestPlanResponse(
    val `data`: Data,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val id: String,
        val payment_order_id: String,
        val profile_id: Int,
        val test_plan_name: String,
        val test_plan_payment_status: String,
        val test_plan_price: String
    )
}