package app.oxyjon.database
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import android.content.*
import android.util.Log

@Database(entities = [Medicine::class, FoodDiary::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao

    companion object {
        private val LOG_TAG: String = AppDatabase::class.java.simpleName
        private val LOCK: Any = Any()
        private const val DATABASE_NAME: String = "medicineList"
        private var sInstance: AppDatabase? = null
        fun getInstance(context: Context?): AppDatabase? {
            if (sInstance == null) {
                synchronized(LOCK) {
                    Log.d(LOG_TAG, "Creating new database instance")
                    sInstance = Room.databaseBuilder(context!!.applicationContext,
                        AppDatabase::class.java, DATABASE_NAME).allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            Log.d(LOG_TAG, "Getting the database instance")
            return sInstance
        }
    }
}