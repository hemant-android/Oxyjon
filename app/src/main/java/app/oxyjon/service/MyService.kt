package app.oxyjon.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import app.oxyjon.bean.MedicineListResponse

class MyService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        onTaskRemoved(intent)
        Log.e("Background Service","This is a Service running in Background")
//        Toast.makeText(applicationContext, "This is a Service running in Background",Toast.LENGTH_SHORT).show()

        var bundle = intent!!.extras?.getSerializable("medicine") as ArrayList<MedicineListResponse.Medicinelist.Data>

        return START_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")

    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)

    }
}