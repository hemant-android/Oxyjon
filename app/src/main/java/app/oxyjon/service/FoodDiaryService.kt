package app.oxyjon.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import app.oxyjon.MainApplication
import app.oxyjon.bean.FoodDiaryListResponse
import app.oxyjon.bean.MedicineListResponse
import app.oxyjon.database.AppDatabase
import app.oxyjon.database.AppExecutors
import app.oxyjon.database.FoodDiary
import app.oxyjon.database.Medicine
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import retrofit2.Response


class FoodDiaryService : Service() {
    private var mMedicineList: ArrayList<MedicineListResponse.Medicinelist.Data>? = ArrayList()
    private var mFoodList: ArrayList<FoodDiaryListResponse.Fooditemlist.Data>? = ArrayList()
    private var allMedicineList: List<Medicine?>? = ArrayList()
    private var allFoodList: List<FoodDiary?>? = ArrayList()
    private var mDb: AppDatabase? = null
    var page: Int = 1
    var profileId: String? = ""
    var customerId: String? = ""
    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent?.extras != null) {
            val bundle: Bundle? = intent.extras
            if (bundle != null) {
                profileId = bundle.getString("profileId")
                customerId = bundle.getString("customerId")

//            getMedicineList(profileId, customerId, 1);
//            ApiCall.getInstance().getMedicineList(customerId, profileId, page, this);
                MedicineListAsyncTask(profileId, customerId, page).execute()
                //                new FoodDiaryAsyncTask(profileId, customerId, page).execute();
            }
        }
        return START_STICKY
    }

    override fun onCreate() {
        mDb = AppDatabase.getInstance(this)
        super.onCreate()
    }

    inner class FoodDiaryAsyncTask constructor(profileId: String, customerId: String, page: Int) :
        AsyncTask<String?, Void?, String?>(), IApiCallback {
        var profileId: String = ""
        var customerId: String = ""
        var page: Int

        init {
            this.profileId = profileId
            this.customerId = customerId
            this.page = page
        }

        override fun doInBackground(vararg strings: String?): String? {
            if (isConnection(MainApplication.currentActivity)) {
                ApiCall.instance.getFoodList(customerId, profileId, page, this)
            } else {
                Toast.makeText(MainApplication.currentActivity,
                    "please check your internet connection",
                    Toast.LENGTH_SHORT).show()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }

        override fun onSuccess(type: Any, data: Any, extraData: Any?) {
            if ((type == "foodList")) {
                val response: Response<FoodDiaryListResponse?>? =
                    data as Response<FoodDiaryListResponse?>?
                if ((response != null) && (response.body() != null) && response.isSuccessful && (response.body()!!.errorCode == "0")) {
                    if (response.body()!!.foodupdate_action != null && response.body()!!.foodupdate_action.update_data.equals(
                            "True",
                            ignoreCase = true)
                    ) {
                        if (response.body()!!.fooditemlist != null) {
                            val currentPage: Int = response.body()!!.fooditemlist.current_page
                            val lastPage: Int = response.body()!!.fooditemlist.last_page
                            if (mFoodList != null && mFoodList!!.size > 0) {
                                mFoodList!!.clear()
                            }
                            mFoodList = response.body()!!.fooditemlist.data
                            Log.e("Food Page size is: ", mFoodList!!.size.toString())
                            Log.e("Food Current Page is: ", currentPage.toString())
                            AppExecutors.instance!!.diskIO()
                                .execute {
                                    MainApplication.currentActivity!!.runOnUiThread {
                                        for (data: FoodDiaryListResponse.Fooditemlist.Data in mFoodList!!) {
                                            val foodDiary: FoodDiary = FoodDiary(data.id,
                                                data.food_type,
                                                data.food_item_name,
                                                data.measurement_unit,
                                                data.quantity_primary,
                                                data.quantity_unit_primary ?: "",
                                                data.quantity_secondary,
                                                data.quantity_unit_secondary,
                                                data.calorie_gm,
                                                data.protein_gm,
                                                data.carbs_gm,
                                                data.fats_gm,
                                                data.fiber_gm,
                                                data.meal_early_morning,
                                                data.meal_breakfast,
                                                data.meal_morning_snack,
                                                data.meal_lunch,
                                                data.meal_evening_snack,
                                                data.meal_dinner,
                                                if (data.meal_bed_time != null) data.meal_bed_time.toString() else "",
                                                if (data.cuisine_type != null) data.cuisine_type.toString() else "")
                                            mDb!!.personDao().insertFood(foodDiary)
                                        }
                                    }
                                }
                            if (currentPage != lastPage) {
                                if (isConnection(MainApplication.currentActivity)) {
                                    FoodDiaryAsyncTask(profileId,
                                        customerId,
                                        currentPage + 1).execute()
                                } else {
                                    Toast.makeText(MainApplication.currentActivity,
                                        "please check your internet connection",
                                        Toast.LENGTH_SHORT).show()
                                }
                            } else if (currentPage == lastPage) {
                                if (isConnection(MainApplication.currentActivity)) {
                                    ApiCall.instance.foodSyncComplete(customerId,
                                        profileId,
                                        response.body()!!.foodupdate_action.sync_id,
                                        this)
                                } else {
                                    Toast.makeText(MainApplication.currentActivity,
                                        "please check your internet connection",
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else if (response.body()!!.foodupdate_action != null && response.body()!!.foodupdate_action.update_data.equals(
                            "False",
                            ignoreCase = true)
                    ) {
                        AppExecutors.instance!!.diskIO().execute {
                            allFoodList = mDb!!.personDao().allFoodDiary
                        }
                        if ((allFoodList != null) && (allFoodList!!.isNotEmpty()) && (allFoodList!!.size >= response.body()!!.fooditemlist.total)) {
                            Log.e("Food Page size is: ", allFoodList!!.size.toString())
                        } else {
                            if (response.body()!!.fooditemlist != null) {
                                val currentPage: Int = response.body()!!.fooditemlist.current_page
                                val lastPage: Int = response.body()!!.fooditemlist.last_page
                                if (mFoodList != null && mFoodList!!.size > 0) {
                                    mFoodList!!.clear()
                                }
                                mFoodList = response.body()!!.fooditemlist.data
                                Log.e("Food Page size is: ", mFoodList!!.size.toString())
                                Log.e("Current Page is: ", currentPage.toString())
                                AppExecutors.instance!!.diskIO()
                                    .execute {
                                        MainApplication.currentActivity!!.runOnUiThread {
                                            for (data: FoodDiaryListResponse.Fooditemlist.Data in mFoodList!!) {
                                                val foodDiary: FoodDiary =
                                                    FoodDiary(data.id,
                                                        data.food_type,
                                                        data.food_item_name,
                                                        data.measurement_unit,
                                                        data.quantity_primary,
                                                        data.quantity_unit_primary ?: "",
                                                        data.quantity_secondary,
                                                        data.quantity_unit_secondary,
                                                        data.calorie_gm,
                                                        data.protein_gm,
                                                        data.carbs_gm,
                                                        data.fats_gm,
                                                        data.fiber_gm,
                                                        data.meal_early_morning,
                                                        data.meal_breakfast,
                                                        data.meal_morning_snack,
                                                        data.meal_lunch,
                                                        data.meal_evening_snack,
                                                        data.meal_dinner,
                                                        if (data.meal_bed_time != null) data.meal_bed_time.toString() else "",
                                                        if (data.cuisine_type != null) data.cuisine_type.toString() else "")
                                                mDb!!.personDao().insertFood(foodDiary)
                                            }
                                        }
                                    }
                                if (currentPage != lastPage) {
                                    if (isConnection(MainApplication.currentActivity)) {
                                        FoodDiaryAsyncTask(profileId,
                                            customerId,
                                            currentPage + 1).execute()
                                    } else {
                                        Toast.makeText(MainApplication.currentActivity,
                                            "please check your internet connection",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        override fun onFailure(data: Any) {
            Log.e("onFailure ", "Fail Food Response")
            val error: String = data as String
            if (error.contains("No address associated with hostname")) {
                Log.e("onFailure ", "No address associated with hostname")
            } else if (error.contains("java.net.SocketTimeoutException")) {
                Log.e("onFailure ", "java.net.SocketTimeoutException")
            } else {
                Log.e("onFailure ", "please check your internet connection")
            }
        }
    }

    inner class MedicineListAsyncTask constructor(
        profileId: String?,
        customerId: String?,
        page: Int,
    ) : AsyncTask<String?, Void?, String?>(), IApiCallback {
        var profileId: String? = ""
        var customerId: String? = ""
        var page: Int

        init {
            this.profileId = profileId
            this.customerId = customerId
            this.page = page
        }

        override fun doInBackground(vararg params: String?): String? {
            if (isConnection(MainApplication.currentActivity)) {
                ApiCall.instance.getMedicineList(customerId, profileId, page, this)
            } else {
                Toast.makeText(MainApplication.currentActivity,
                    "please check your internet connection",
                    Toast.LENGTH_SHORT).show()
            }
            return null
        }


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }

        override fun onSuccess(type: Any, data: Any, extraData: Any?) {
            if ((type == "medicineList")) {
                val response: Response<MedicineListResponse?>? =
                    data as Response<MedicineListResponse?>?
                if ((response != null) && (response.body() != null) && response.isSuccessful && (response.body()!!.errorCode == "0")) {
                    if (response.body()!!.medicinelist_action != null && response.body()!!.medicinelist_action.update_data.equals(
                            "True",
                            ignoreCase = true)
                    ) {
                        if (response.body()!!.medicinelist != null) {
                            val currentPage: Int = response.body()!!.medicinelist.current_page
                            val lastPage: Int = response.body()!!.medicinelist.last_page
                            if (mMedicineList != null && mMedicineList!!.size > 0) {
                                mMedicineList!!.clear()
                            }
                            mMedicineList = response.body()!!.medicinelist.data

//                            Log.e("Medicine Page size is: ", String.valueOf(mMedicineList.size()));
//                            Log.e("Current Page is: ", String.valueOf(currentPage));
                            AppExecutors.instance!!.diskIO()
                                .execute {
                                    MainApplication.currentActivity!!.runOnUiThread {
                                        for (data: MedicineListResponse.Medicinelist.Data in mMedicineList!!) {
                                            val medicine: Medicine = Medicine(data.id,
                                                data.medicine_name,
                                                data.medicine_form,
                                                data.medicine_category,
                                                data.created_at)
                                            mDb!!.personDao().insertMedicine(medicine)
                                        }
                                    }
                                }
                            if (currentPage != lastPage) {
                                if (isConnection(MainApplication.currentActivity)) {
                                    MedicineListAsyncTask(profileId,
                                        customerId,
                                        currentPage + 1).execute()
                                } else {
                                    Toast.makeText(MainApplication.currentActivity,
                                        "please check your internet connection",
                                        Toast.LENGTH_SHORT).show()
                                }
                            } else if (currentPage == lastPage) {
                                if (isConnection(MainApplication.currentActivity)) {
                                    ApiCall.instance.medicineSyncComplete(customerId,
                                        profileId,
                                        response.body()!!.medicinelist_action.sync_id,
                                        this)
                                } else {
                                    Toast.makeText(MainApplication.currentActivity,
                                        "please check your internet connection",
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else if (response.body()!!.medicinelist_action != null && response.body()!!.medicinelist_action.update_data.equals(
                            "False",
                            ignoreCase = true)
                    ) {
                        AppExecutors.instance!!.diskIO().execute {
                            allMedicineList = mDb!!.personDao().allMedicine
                        }
                        if ((allMedicineList != null) && (allMedicineList!!.isNotEmpty()) && (allMedicineList!!.size >= response.body()!!.medicinelist.total)) {
//                            Log.e("Page size is: ", String.valueOf(allMedicineList.size()));
                        } else {
                            if (response.body()!!.medicinelist != null) {
                                val currentPage: Int = response.body()!!.medicinelist.current_page
                                val lastPage: Int = response.body()!!.medicinelist.last_page
                                if (mMedicineList != null && mMedicineList!!.size > 0) {
                                    mMedicineList!!.clear()
                                }
                                mMedicineList = response.body()!!.medicinelist.data

//                                Log.e("Medicine Page size is: ", String.valueOf(mMedicineList.size()));
//                                Log.e("Current Page is: ", String.valueOf(currentPage));
                                AppExecutors.instance!!.diskIO()
                                    .execute {
                                        MainApplication.currentActivity!!.runOnUiThread {
                                            for (data: MedicineListResponse.Medicinelist.Data in mMedicineList!!) {
                                                val medicine: Medicine = Medicine(data.id,
                                                    data.medicine_name,
                                                    data.medicine_form,
                                                    data.medicine_category,
                                                    data.created_at)
                                                mDb!!.personDao().insertMedicine(medicine)
                                            }
                                        }
                                    }
                                if (currentPage != lastPage) {
                                    if (isConnection(MainApplication.currentActivity)) {
                                        MedicineListAsyncTask(profileId,
                                            customerId,
                                            currentPage + 1).execute()
                                    } else {
                                        Toast.makeText(MainApplication.currentActivity,
                                            "please check your internet connection",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        override fun onFailure(data: Any) {
            Log.e("onFailure ", "Fail Medicine Response")
            val error: String = data as String
            if (error.contains("No address associated with hostname")) {
                Log.e("onFailure ", "No address associated with hostname")
            } else if (error.contains("java.net.SocketTimeoutException")) {
                Log.e("onFailure ", "java.net.SocketTimeoutException")
            } else {
                Log.e("onFailure ", "please check your internet connection")
            }
        }

    }

    companion object {
        fun isConnection(ctx: Context?): Boolean {
            try {
                val connectivityManager: ConnectivityManager = ctx!!.getSystemService(
                    CONNECTIVITY_SERVICE) as ConnectivityManager
                val ni: NetworkInfo? = connectivityManager.activeNetworkInfo
                return (ni != null) && ni.isAvailable && ni.isConnected
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }
    }
}