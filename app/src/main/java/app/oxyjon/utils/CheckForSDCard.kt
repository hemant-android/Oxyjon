package app.oxyjon.utils

import android.os.Environment


object CheckForSDCard {
    val isSDCardPresent: Boolean
        get() {
            if ((Environment.getExternalStorageState() ==
                        Environment.MEDIA_MOUNTED)
            ) {
                return true
            }
            return false
        }
}