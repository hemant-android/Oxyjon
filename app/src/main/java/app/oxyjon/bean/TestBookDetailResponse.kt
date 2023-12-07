package app.oxyjon.bean

data class TestBookDetailResponse(
    val bloodtest_details: BloodtestDetails,
    val errorCode: String,
    val errorMsg: String
) {
    data class BloodtestDetails(
        val banner: String,
        val benefits: ArrayList<Benefit>,
        val id: Int,
        val plan_details: String,
        val plan_title: String,
        val price: String,
        val tests: ArrayList<Test>
    ) {
        data class Benefit(
            val name: String
        )

        data class Test(
            val name: String
        )
    }
}