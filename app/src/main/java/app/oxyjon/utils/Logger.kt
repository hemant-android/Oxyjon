package app.oxyjon.utils

import android.util.Log


object Logger {
    fun show(tag: String?, mess: String?) {
        Log.e(tag, (mess)!!)
    }
}