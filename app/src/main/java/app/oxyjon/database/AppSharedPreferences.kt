package app.oxyjon.database

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

/**
 * Created by VS on 12/2/2019.
 */
class AppSharedPreferences private constructor() {

    fun clearData() {
        editor!!.clear()
        editor!!.commit()
    }

    var isLogin: Boolean
        get() {
            return preferences!!.getBoolean("isLogin", false)
        }
        set(login) {
            editor!!.putBoolean("isLogin", login)
            editor!!.apply()
        }
    var isFitConnect: Boolean
        get() {
            return preferences!!.getBoolean("isConnect", false)
        }
        set(isConnect) {
            editor!!.putBoolean("isConnect", isConnect)
            editor!!.apply()
        }
    var userId: String?
        get() {
            return preferences!!.getString("user_id", "")
        }
        set(id) {
            editor!!.putString("user_id", id)
            editor!!.apply()
        }
    var newUser: String?
        get() {
            return preferences!!.getString("newUser", "")
        }
        set(newUser) {
            editor!!.putString("newUser", newUser)
            editor!!.apply()
        }

    fun setprofileid(id: String?) {
        editor!!.putString("profile_id", id)
        editor!!.apply()
    }

    fun getprofileid(): String? {
        return preferences!!.getString("profile_id", "")
    }

    var fullName: String?
        get() {
            return preferences!!.getString("fullName", "")
        }
        set(fullName) {
            editor!!.putString("fullName", fullName)
            editor!!.apply()
        }
    var name: String?
        get() {
            return preferences!!.getString("name", "")
        }
        set(name) {
            editor!!.putString("name", name)
            editor!!.apply()
        }
    var emailId: String?
        get() {
            return preferences!!.getString("emailId", "")
        }
        set(emailId) {
            editor!!.putString("emailId", emailId)
            editor!!.apply()
        }
    var countryCode: String?
        get() {
            return preferences!!.getString("countryCode", "")
        }
        set(code) {
            editor!!.putString("countryCode", code)
            editor!!.apply()
        }

    fun setlatitude(latitude: String?) {
        editor!!.putString("latitude", latitude)
        editor!!.apply()
    }

    fun getlatitude(): String? {
        return preferences!!.getString("latitude", "")
    }

    fun setlongitude(longitude: String?) {
        editor!!.putString("longitude", longitude)
        editor!!.apply()
    }

    fun getlongitude(): String? {
        return preferences!!.getString("longitude", "")
    }

    var phoneNo: String?
        get() {
            return preferences!!.getString("phoneNo", "")
        }
        set(id) {
            editor!!.putString("phoneNo", id)
            editor!!.apply()
        }
    var cityId: String?
        get() {
            return preferences!!.getString("cityId", "")
        }
        set(cityId) {
            editor!!.putString("cityId", cityId)
            editor!!.apply()
        }
    var rememberMe: String?
        get() {
            return preferences!!.getString("RememberMe", "")
        }
        set(RememberMe) {
            editor!!.putString("RememberMe", RememberMe)
            editor!!.apply()
        }
    var image: String?
        get() {
            return preferences!!.getString("image", "")
        }
        set(image) {
            editor!!.putString("image", image)
            editor!!.apply()
        }

    fun clear() {
        editor!!.clear()
        editor!!.apply()
    }

    var password: String?
        get() {
            return preferences!!.getString("password", "")
        }
        set(password) {
            editor!!.putString("password", password)
            editor!!.apply()
        }

    fun setprofilestatus(list: String?) {
        editor!!.putString("statuslist", list)
        editor!!.apply()
    }

    fun getprofilestatus(): String? {
        return preferences!!.getString("statuslist", "")
    }

    var deviceId: String?
        get() {
            return preferences!!.getString("deviceId", "")
        }
        set(deviceId) {
            editor!!.putString("deviceId", deviceId)
            editor!!.apply()
        }
    var deviceToken: String?
        get() {
            return preferences!!.getString("deviceToken", "")
        }
        set(deviceToken) {
            editor!!.putString("deviceToken", deviceToken)
            editor!!.apply()
        }
    var userLoggedIn: String?
        get() {
            return preferences!!.getString("userLoggedIn", "")
        }
        set(userLoggedIn) {
            editor!!.putString("userLoggedIn", userLoggedIn)
            editor!!.apply()
        }
    val isWalkThrough: Boolean
        get() {
            return preferences!!.getBoolean("isWalk", false)
        }

    fun isWalkThrough(isWalk: Boolean) {
        editor!!.putBoolean("isWalk", isWalk)
        editor!!.apply()
    }

    val isPopupShow: Boolean
        get() {
            return preferences!!.getBoolean("isPopup", false)
        }

    fun isPopupShow(isPopup: Boolean) {
        editor!!.putBoolean("isPopup", isPopup)
        editor!!.apply()
    }

    var userOnBoard: String?
        get() {
            return preferences!!.getString("userOnBoard", "")
        }
        set(userOnBoard) {
            editor!!.putString("userOnBoard", userOnBoard)
            editor!!.apply()
        }
    var userMobileNumber: String?
        get() {
            return preferences!!.getString("userMobileNumber", "")
        }
        set(userMobileNumber) {
            editor!!.putString("userMobileNumber", userMobileNumber)
            editor!!.apply()
        }
    var currentDate: String?
        get() {
            return preferences!!.getString("currentDate", "")
        }
        set(currentDate) {
            editor!!.putString("currentDate", currentDate)
            editor!!.apply()
        }
    var sugarDialogCount: Int
        get() {
            return preferences!!.getInt("openSugarDialogCount", 0)
        }
        set(openSugarDialogCount) {
            editor!!.putInt("openSugarDialogCount", openSugarDialogCount)
            editor!!.apply()
        }
    var foodDiaryDialogCount: Int
        get() {
            return preferences!!.getInt("openFoodDiaryDialogCount", 0)
        }
        set(openFoodDiaryDialogCount) {
            editor!!.putInt("openFoodDiaryDialogCount", openFoodDiaryDialogCount)
            editor!!.apply()
        }
    var stepCounterDialogCount: Int
        get() {
            return preferences!!.getInt("openStepCounterDialogCount", 0)
        }
        set(openStepCounterDialogCount) {
            editor!!.putInt("openStepCounterDialogCount", openStepCounterDialogCount)
            editor!!.apply()
        }
    var medicineDialogCount: Int
        get() {
            return preferences!!.getInt("openMedicineDialogCount", 0)
        }
        set(openMedicineDialogCount) {
            editor!!.putInt("openMedicineDialogCount", openMedicineDialogCount)
            editor!!.apply()
        }
    val sugarDialogPopup: Boolean
        get() {
            return preferences!!.getBoolean("isSugarDialogPopup", false)
        }

    fun isSugarDialogPopup(isPopup: Boolean) {
        editor!!.putBoolean("isSugarDialogPopup", isPopup)
        editor!!.apply()
    }

    val foodDiaryDialogPopup: Boolean
        get() {
            return preferences!!.getBoolean("isFoodDiaryPopup", false)
        }

    fun isFoodDiaryDialogPopup(isPopup: Boolean) {
        editor!!.putBoolean("isFoodDiaryPopup", isPopup)
        editor!!.apply()
    }

    val medicineDialogPopup: Boolean
        get() {
            return preferences!!.getBoolean("isMedicinePopup", false)
        }

    fun isMedicineDialogPopup(isPopup: Boolean) {
        editor!!.putBoolean("isMedicinePopup", isPopup)
        editor!!.apply()
    }


    val feedbackDialogPopup: Boolean
        get() {
            return preferences!!.getBoolean("isFeedbackDialogPopup", false)
        }

    fun isFeedbackDialogPopup(isPopup: Boolean) {
        editor!!.putBoolean("isFeedbackDialogPopup", isPopup)
        editor!!.apply()
    }

    val stepCountDialogPopup: Boolean
        get() {
            return preferences!!.getBoolean("isStepCountPopup", false)
        }

    fun isStepCountDialogPopup(isPopup: Boolean) {
        editor!!.putBoolean("isStepCountPopup", isPopup)
        editor!!.apply()
    }

    var heightFeet: String?
        get() {
            return preferences!!.getString("heightFeet", "")
        }
        set(heightFeet) {
            editor!!.putString("heightFeet", heightFeet)
            editor!!.apply()
        }
    var heightInch: String?
        get() {
            return preferences!!.getString("heightInch", "")
        }
        set(heightInch) {
            editor!!.putString("heightInch", heightInch)
            editor!!.apply()
        }

    var weight: String?
        get() {
            return preferences!!.getString("weight", "")
        }
        set(weight) {
            editor!!.putString("weight", weight)
            editor!!.apply()
        }

    var paymentStatus: String?
        get() {
            return preferences!!.getString("paymentStatus", "")
        }
        set(paymentStatus) {
            editor!!.putString("paymentStatus", paymentStatus)
            editor!!.apply()
        }

    var token: String?
        get() {
            return preferences!!.getString("token", "")
        }
        set(token) {
            editor!!.putString("token", token)
            editor!!.apply()
        }

    companion object {
        private var preferences: SharedPreferences? = null
        private var editor: SharedPreferences.Editor? = null
        private var preferencesData: AppSharedPreferences? = null
        private const val PREF_NAME = "encrypted_preferences"
        private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        fun getInstance(context: Context?): AppSharedPreferences? {
            if (context != null) {

                preferences = EncryptedSharedPreferences.create(
                    PREF_NAME,
                    masterKeyAlias,
                    context!!,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )

//                preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                editor = preferences!!.edit()
            }
            if (preferencesData == null) {
                preferencesData = AppSharedPreferences()

            }
            return preferencesData
        }
    }
}