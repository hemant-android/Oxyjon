package app.oxyjon.bean

data class DietPlanHomeResponse(
    val `data`: Data,
    val errorCode: String,
    val errorMsg: String,
    val is_plan_active: String,
    val plan_active_message: String

) {
    data class Data(
        val daily_target: ArrayList<DailyTarget>,
        val goals: ArrayList<String>
    ) {
        data class DailyTarget(
            val fibers: String,
            val carb: String,
            val fat: String,
            val protein: String,
            val total_calories: String
        )
    }
}