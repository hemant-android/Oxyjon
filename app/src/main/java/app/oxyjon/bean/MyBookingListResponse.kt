package app.oxyjon.bean

data class MyBookingListResponse(
    val errorCode: String,
    val errorMsg: String,
    val myhealthplan: ArrayList<Myhealthplan>
) {
    data class Myhealthplan(
        val test_plan_details: String,
        val test_plan_id: Int,
        val test_plan_name: String,
        val test_plan_price: String
    )
}