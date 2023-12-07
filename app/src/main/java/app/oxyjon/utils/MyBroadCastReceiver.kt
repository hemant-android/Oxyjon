package app.oxyjon.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class MyBroadCastReceiver constructor() : BroadcastReceiver() {
    public override fun onReceive(context: Context, intent: Intent) {
        try {
            Log.d("TAG", "onReceive() called")
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}