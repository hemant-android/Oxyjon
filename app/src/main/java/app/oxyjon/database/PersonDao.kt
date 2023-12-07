package app.oxyjon.database

import androidx.room.*


@Dao
open interface PersonDao {
    /**
     * For Medicine
     */
    @get:Query("SELECT * FROM medicine")
    val allMedicine: List<Medicine?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMedicine(medicine: Medicine?)

    @Update
    fun updateMedicine(medicine: Medicine?)

    @Query("DELETE FROM medicine")
    fun deleteMedicine()

    @Query("SELECT * FROM medicine WHERE name = :name")
    fun loadMedicineByName(name: String?): List<Medicine?>?

    /**
     * For Food Diary
     */
    @get:Query("SELECT * FROM foodDiary")
    val allFoodDiary: List<FoodDiary?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFood(foodDiary: FoodDiary?)

    @Query("DELETE FROM foodDiary")
    fun deleteFoodDiary()
}