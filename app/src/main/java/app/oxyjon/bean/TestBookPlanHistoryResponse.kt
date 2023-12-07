package app.oxyjon.bean

data class TestBookPlanHistoryResponse(
    val `data`: ArrayList<Data>,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val id: String,
        val offer_price: String,
        val price: String,
        val test_details: String,
        val test_name: String
    )
}