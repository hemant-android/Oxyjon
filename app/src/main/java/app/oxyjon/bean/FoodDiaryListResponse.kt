package app.oxyjon.bean

data class FoodDiaryListResponse(
    val errorCode: String,
    val errorMsg: String,
    val fooditemlist: Fooditemlist,
    val foodupdate_action: FoodupdateAction
) {
    data class Fooditemlist(
        val current_page: Int,
        val `data`: ArrayList<Data>,
        val from: Int,
        val last_page: Int,
        val next_page_url: String,
        val path: String,
        val per_page: Int,
        val prev_page_url: Any,
        val to: Int,
        val total: Int
    ) {
        data class Data(
            val calorie_gm: String,
            val carbs_gm: String,
            val cuisine_type: Any,
            val fats_gm: String,
            val fiber_gm: String,
            val food_item_name: String,
            val food_type: String,
            val id: Int,
            val meal_bed_time: Any,
            val meal_breakfast: String,
            val meal_dinner: String,
            val meal_early_morning: String,
            val meal_evening_snack: String,
            val meal_lunch: String,
            val meal_morning_snack: String,
            val measurement_unit: String,
            val protein_gm: String,
            val quantity_primary: String,
            val quantity_unit_primary: String,
            val quantity_secondary: String,
            val quantity_unit_secondary: String
        )
    }

    data class FoodupdateAction(
        val last_update: String,
        val last_update_completion_status: String,
        val sync_id: String,
        val update_data: String
    )
}