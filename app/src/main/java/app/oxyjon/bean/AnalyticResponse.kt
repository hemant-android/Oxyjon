package app.oxyjon.bean

data class AnalyticResponse(
    val errorCode: String,
    val errorMsg: String,
    val health_score: HealthScore
) {
    data class HealthScore(
        val final_score: String,
        val exercise: Exercise,
        val food_calories_data: FoodScore,
        val sugarScore: SugarScore
    ) {
        data class Exercise(
            val action_buton: String,
            val label_last_data_point: String,
            val label_name: String,
            val score: String
        )

        data class FoodScore(
            val action_buton: String,
            val label_last_data_point: String,
            val label_name: String,
            val score: String
        )

        data class SugarScore(
            val action_buton: String,
            val label_last_data_point: String,
            val label_name: String,
            val score: String
        )
    }
}