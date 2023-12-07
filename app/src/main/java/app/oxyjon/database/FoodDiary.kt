package app.oxyjon.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foodDiary")
class FoodDiary constructor(
    @field:PrimaryKey(autoGenerate = true) var id: Int,
    @field:ColumnInfo(name = "food_type") var foodType: String,
    @field:ColumnInfo(name = "food_item_name") var foodItemName: String,
    @field:ColumnInfo(name = "measurement_unit") var measurementUnit: String,
    @field:ColumnInfo(name = "quantity_primary") var quantityPrimary: String,
    @field:ColumnInfo(name = "quantity_unit_primary") var quantityUnitPrimary: String,
    @field:ColumnInfo(name = "quantity_secondary") var quantitySecondary: String,
    @field:ColumnInfo(name = "quantity_unit_secondary") var quantityUnitSecondary: String,
    @field:ColumnInfo(name = "calorie_gm") var calorieGm: String,
    @field:ColumnInfo(name = "protein_gm") var proteinGm: String,
    @field:ColumnInfo(name = "carbs_gm") var carbsGm: String,
    @field:ColumnInfo(name = "fats_gm") var fatsGm: String,
    @field:ColumnInfo(name = "fiber_gm") var fiberGm: String,
    @field:ColumnInfo(name = "meal_early_morning") var mealEarlyMorning: String,
    @field:ColumnInfo(name = "meal_breakfast") var mealBreakfast: String,
    @field:ColumnInfo(name = "meal_morning_snack") var mealMorningSnack: String,
    @field:ColumnInfo(name = "meal_lunch") var mealLunch: String,
    @field:ColumnInfo(name = "meal_evening_snack") var mealEveningSnack: String,
    @field:ColumnInfo(name = "meal_dinner") var mealDinner: String,
    @field:ColumnInfo(name = "meal_bed_time") var mealBedTime: String,
    @field:ColumnInfo(name = "cuisine_type") var cuisineType: String
) {
    var select: Boolean = true
}