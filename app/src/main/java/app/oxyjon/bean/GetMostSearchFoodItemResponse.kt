package app.oxyjon.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetMostSearchFoodItemResponse constructor() {
    @SerializedName("errorCode")
    @Expose
    var errorCode: String? = null

    @SerializedName("errorMsg")
    @Expose
    var errorMsg: String? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<Datum>? = null

    inner class Datum constructor() {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("food_type")
        @Expose
        var foodType: String? = null

        @SerializedName("food_item_name")
        @Expose
        var foodItemName: String? = null

        @SerializedName("measurement_unit")
        @Expose
        var measurementUnit: String? = null

        @SerializedName("quantity_primary")
        @Expose
        var quantityPrimary: String? = null

        @SerializedName("quantity_unit_primary")
        @Expose
        var quantityUnitPrimary: String? = null

        @SerializedName("quantity_secondary")
        @Expose
        var quantitySecondary: String? = null

        @SerializedName("quantity_unit_secondary")
        @Expose
        var quantityUnitSecondary: String? = null

        @SerializedName("calorie_gm")
        @Expose
        var calorieGm: String? = null

        @SerializedName("protein_gm")
        @Expose
        var proteinGm: String? = null

        @SerializedName("carbs_gm")
        @Expose
        var carbsGm: String? = null

        @SerializedName("fats_gm")
        @Expose
        var fatsGm: String? = null

        @SerializedName("fiber_gm")
        @Expose
        var fiberGm: String? = null

        @SerializedName("meal_early_morning")
        @Expose
        var mealEarlyMorning: String? = null

        @SerializedName("meal_breakfast")
        @Expose
        var mealBreakfast: String? = null

        @SerializedName("meal_morning_snack")
        @Expose
        var mealMorningSnack: String? = null

        @SerializedName("meal_lunch")
        @Expose
        var mealLunch: String? = null

        @SerializedName("meal_evening_snack")
        @Expose
        var mealEveningSnack: String? = null

        @SerializedName("meal_dinner")
        @Expose
        var mealDinner: String? = null

        @SerializedName("meal_bed_time")
        @Expose
        var mealBedTime: String? = null

        @SerializedName("cuisine_type")
        @Expose
        var cuisineType: String? = null
        var select: Boolean = false
    }
}