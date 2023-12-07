package app.oxyjon.utils

import android.app.Activity
import android.content.pm.PackageManager

import androidx.core.app.ActivityCompat
import android.os.Build
import android.content.*

class PermissionUtil constructor() {
    fun checkMarshMellowPermission(): Boolean {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
    }

    companion object {
        val galleryPermissions: Array<String> = arrayOf(
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
        )
        var contactPermissions: Array<String> = arrayOf(
            "android.permission.READ_CONTACTS"
        )
        var readSmsPermissions: Array<String> = arrayOf(
            "android.permission.READ_SMS"
        )
        var readLocationPermissions: Array<String> = arrayOf(
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION"
        )
        var readFitnessPermissions: Array<String?> = arrayOf(
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACTIVITY_RECOGNITION",
            "android.permission.BODY_SENSORS"
        )
        val cameraPermissions: Array<String?> = arrayOf(
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
        )

        fun readSmsPermissions(): Array<String> {
            return readSmsPermissions
        }

        fun readLocationPermissions(): Array<String> {
            return readLocationPermissions
        }

        fun readFitnessPermissions(): Array<String?> {
            return readFitnessPermissions
        }

        fun verifyPermissions(context: Context?, grantResults: Array<String?>): Boolean {
            for (result: String? in grantResults) {
                if (ActivityCompat.checkSelfPermission((context)!!,
                        (result)!!) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
            return true
        }

        fun requestPermission(grantResults: Array<String?>, activity: Activity?) {
            ActivityCompat.requestPermissions((activity)!!, (grantResults)!!, 1000)
        }
    }
}