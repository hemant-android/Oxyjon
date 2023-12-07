package app.oxyjon.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.JobIntentService
import app.oxyjon.R
import app.oxyjon.ui.kotlin.activity.DashboardActivity


class NotifJobIntentService : JobIntentService() {
    private var context: Context? = null
    var medicineName: String? = ""
    var medicineTime: String? = ""
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }

    override fun onHandleWork(intent: Intent) {
        context = applicationContext
        Log.d(TAG, "OnHandleWork")
        medicineName = intent.getStringExtra("medicineName")
        medicineTime = intent.getStringExtra("medicineTime")
        intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
        val intent1 = Intent(context, DashboardActivity::class.java)
        val pIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val builder: Notification.Builder = Notification.Builder(context)
            .setTicker("Notification")
            .setContentTitle(context!!.resources.getString(R.string.app_name))
            .setContentText("Take your medicine $medicineTime")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pIntent)
        val notificationManager: NotificationManager = context!!.getSystemService(
            NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId: String = "Your_channel_id"
            val channel: NotificationChannel = NotificationChannel(
                channelId,
                "Reminder to remind to review your notes",
                NotificationManager.IMPORTANCE_HIGH)
            channel.description = "Hello Dear friends" //this is to test what this is
            notificationManager.createNotificationChannel(channel)
            builder.setChannelId(channelId)
        }
        val notification: Notification = builder.build()
        notification.flags = Notification.FLAG_AUTO_CANCEL
        notificationManager.notify(0, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun onStopCurrentWork(): Boolean {
        Log.d(TAG, "onStopCurrentWork")
        return false
    }

    companion object {
        private const val TAG: String = "NotifJobIntentService"
        fun enqueueWork(context: Context?, intent: Intent?) {
            enqueueWork((context)!!, NotifJobIntentService::class.java, 123, (intent)!!)
        }
    }
}