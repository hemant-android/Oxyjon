package app.oxyjon.bean

import java.io.Serializable

data class MyDietPlanResponse(
    val `data`: ArrayList<Data>,
    val errorCode: String,
    val errorMsg: String
) : Serializable {
    data class Data(
        val meal_data: ArrayList<MealData>,
        val meal_name: String,
        val meal_remarks: String
    ) : Serializable {
        data class MealData(
            val calories: String,
            val carb: String,
            val fat: String,
            val fibers: String,
            val food_name: String,
            val protein: String
        ) : Serializable
    }
}