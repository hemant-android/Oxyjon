package app.oxyjon.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo


object CheckConnection {
    fun isConnection(ctx: Context): Boolean {
        val connectivityManager: ConnectivityManager =
            ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val ni: NetworkInfo? = connectivityManager.activeNetworkInfo
        return (ni != null) && ni.isAvailable && ni.isConnected
    }
}