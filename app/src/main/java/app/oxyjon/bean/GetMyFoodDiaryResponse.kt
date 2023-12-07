package app.oxyjon.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetMyFoodDiaryResponse constructor() : Serializable {
    @SerializedName("errorCode")
    @Expose
    var errorCode: String? = null

    @SerializedName("errorMsg")
    @Expose
    var errorMsg: String? = null

    @SerializedName("totaldatacounts")
    @Expose
    var totalDataCounts: TotalDataCounts? = null

    @SerializedName("food_diery_info")
    @Expose
    var foodDiaryInfo: FoodDiaryInfo? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<Datum>? = null

    inner class TotalDataCounts constructor() : Serializable {
        @SerializedName("total_calorie")
        @Expose
        var totalCalorie: String? = null

        @SerializedName("total_protein")
        @Expose
        var totalProtein: String? = null

        @SerializedName("total_carbs")
        @Expose
        var totalCarbs: String? = null

        @SerializedName("total_fats")
        @Expose
        var totalFats: String? = null

        @SerializedName("total_fiber")
        @Expose
        var totalFiber: String? = null
    }

    inner class FoodDiaryInfo constructor() : Serializable {
        @SerializedName("block_title")
        @Expose
        var blockTitle: String? = null

        @SerializedName("block_details")
        @Expose
        var blockDetails: String? = null

        @SerializedName("daily_calories")
        @Expose
        var dailyCalories: String? = null

        @SerializedName("daily_calories_taken")
        @Expose
        var dailyCaloriesTaken: String? = null

        @SerializedName("action_link")
        @Expose
        var actionLink: String? = null

        @SerializedName("weight_type")
        @Expose
        var weightType: String? = null

        @SerializedName("weight_type_message")
        @Expose
        var weightTypeMessage: String? = null

        @SerializedName("bmi")
        @Expose
        var bmi: String? = null
    }

    inner class Datum constructor() : Serializable {
        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("total_calories")
        @Expose
        var totalCalorie: String? = null

        @SerializedName("total_calorie_intake")
        @Expose
        var totalCalorieIntake: String? = null

        @SerializedName("fooditems")
        @Expose
        var fooditems: ArrayList<FoodItem>? = null
        private var isOpen: Boolean = false
        fun getOpen(): Boolean? {
            return isOpen
        }

        fun setOpen(open: Boolean) {
            isOpen = open
        }

        inner class FoodItem constructor() : Serializable {
            @SerializedName("id")
            @Expose
            var id: Int? = null

            @SerializedName("profile_id")
            @Expose
            var profileId: Int? = null

            @SerializedName("food_id")
            @Expose
            var foodId: Int? = null

            @SerializedName("food_item_name")
            @Expose
            var foodItemName: String? = null

            @SerializedName("meal_time")
            @Expose
            var mealTime: String? = null

            @SerializedName("meal_date")
            @Expose
            var mealDate: String? = null

            @SerializedName("meal_quantity_type")
            @Expose
            var mealQuantityType: String? = null

            @SerializedName("meal_quantity")
            @Expose
            var mealQuantity: String? = null

            @SerializedName("meal_quantity_unit")
            @Expose
            var mealQuantityUnit: String? = null

            @SerializedName("food_type")
            @Expose
            var foodType: String? = null

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

            @SerializedName("added_on")
            @Expose
            var addedOn: String? = null
        }
    }
}