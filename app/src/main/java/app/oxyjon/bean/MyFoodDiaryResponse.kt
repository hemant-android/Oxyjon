package app.oxyjon.bean

data class MyFoodDiaryResponse(
    val `data`: ArrayList<Data>,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val added_on: String,
        val food_id: Int,
        val food_item_name: String,
        val id: Int,
        val meal_date: String,
        val meal_quantity: String,
        val meal_quantity_type: String,
        val meal_quantity_unit: String,
        val meal_time: String,
        val profile_id: Int
    )
}