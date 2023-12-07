package app.oxyjon.firebasenotification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.text.Html
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import app.oxyjon.R
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.ui.kotlin.activity.AddSugarActivity
import app.oxyjon.ui.kotlin.activity.DashboardActivity
import app.oxyjon.ui.kotlin.activity.MedicineListActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.moengage.firebase.MoEFireBaseHelper.Companion.getInstance
import com.moengage.pushbase.MoEPushHelper


class NotificationReceive : FirebaseMessagingService() {
    private val context: Context = this

    @SuppressLint("LongLogTag")
    public override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(remoteMessage);
        Log.d("Notification Message", "From: " + remoteMessage.from)
        // Handle notification from other sources.
        if ((remoteMessage != null) && (remoteMessage.getData() != null) && (remoteMessage.data.isNotEmpty())) {
            if (MoEPushHelper.getInstance().isFromMoEngagePlatform(remoteMessage.data)) {
                getInstance().passPushPayload(applicationContext, remoteMessage.data)
            } else if (MoEPushHelper.getInstance()
                    .isFromMoEngagePlatform(remoteMessage.data) && MoEPushHelper.getInstance()
                    .isSilentPush(remoteMessage.data)
            ) {
                getInstance().passPushPayload(applicationContext, remoteMessage.data)
                return
            } else {
                if (AppSharedPreferences.getInstance(this)!!.userLoggedIn!!.isEmpty()) {
                    /*Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);*/
                    return
                } else {
                    if (remoteMessage.data["notificationtype"].equals("content",
                                ignoreCase = true)
                    ) {
                        sendUserNotification(remoteMessage.data["heading"],
                            remoteMessage.data["details"],
                            remoteMessage.data["url"],
                            remoteMessage.data["contenttype"])
                    } else if (remoteMessage.data["notificationtype"].equals("action",
                                ignoreCase = true)
                    ) {
                        sendUserActionNotification(remoteMessage.data["heading"],
                            remoteMessage.data["details"],
                            remoteMessage.data["url"],
                            remoteMessage.data["contenttype"])
                    }
                }
            }
        }
    }

    override fun handleIntent(intent: Intent) {
        try {
            if (intent.extras != null) {
                val builder: RemoteMessage.Builder =
                    RemoteMessage.Builder("MyFirebaseMessagingService")
                for (key: String? in intent.extras!!.keySet()) {
                    builder.addData((key)!!, intent.extras!!.get(key).toString())
                }
                onMessageReceived(builder.build())
            } else {
                super.handleIntent(intent)
            }
        } catch (e: Exception) {
            super.handleIntent(intent)
        }
    }

    private fun sendUserNotification(
        title: String?,
        mess: String?,
        url: String?,
        contentType: String?,
    ) {
        val notifyID: Int = 1
        val intent: Intent
        val mChannel: NotificationChannel
        intent = Intent(context, DashboardActivity::class.java)
        //        intent = new Intent(context, PushNotificationActivity.class);
        intent.putExtra("title", title)
        intent.putExtra("detail", mess)
        intent.putExtra("url", url)
        intent.putExtra("type", contentType)
        intent.putExtra("navigation", "pushNotification")
        //        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val CHANNEL_ID: String = context.packageName // The id of the channel.
        val name: CharSequence = "Sample one" // The user-visible name of the channel.
        var importance: Int = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH
        }
        val contentView = RemoteViews(packageName, R.layout.custom_push)
        contentView.setImageViewResource(R.id.image, R.mipmap.app_icon)
        contentView.setTextViewText(R.id.title, title)
        contentView.setTextViewText(R.id.text, Html.fromHtml(mess))
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, CHANNEL_ID)
        //        notificationBuilder.setContentTitle(title);
        notificationBuilder.setAutoCancel(true)
        notificationBuilder.setContent(contentView)
        notificationBuilder.setSmallIcon(R.mipmap.app_icon)
        notificationBuilder.priority = Notification.PRIORITY_HIGH
        notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        //        notificationBuilder.setContentIntent(pendingIntent);
        contentView.setOnClickPendingIntent(R.id.layout, pendingIntent)
        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE)
        notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder))
        val notify: Notification = notificationBuilder.build()
        notify.flags = notify.flags or Notification.FLAG_AUTO_CANCEL
        val notificationManager: NotificationManager? =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationManager!!.createNotificationChannel(mChannel)
        }
        notificationManager?.notify(notifyID /* ID of notification */, notify)
    }

    private fun sendUserActionNotification(
        title: String?,
        mess: String?,
        url: String?,
        contentType: String?,
    ) {
        val notifyID: Int = 1
        var intent: Intent? = null
        val mChannel: NotificationChannel
        Log.e("contentType: ", (contentType)!!)
        if (contentType.equals("updateSugarValue",
                ignoreCase = true) || contentType.equals("updateHbacc", ignoreCase = true)
        ) {
            intent = Intent(context, AddSugarActivity::class.java)
        } else if (contentType.equals("updateMedicines", ignoreCase = true)) {
            intent = Intent(context, MedicineListActivity::class.java)
            intent.putExtra("navigationType", "medicines")
        } else if (contentType.equals("connectStepcounter", ignoreCase = true)) {
            intent = Intent(context, DashboardActivity::class.java)
            intent.putExtra("navigationType", "addMedicine")
        } else {
            intent = Intent(context, DashboardActivity::class.java)
            //            intent = new Intent(context, PushNotificationActivity.class);
//            intent.putExtra("navigation", "pushNotification");
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val CHANNEL_ID: String = context.packageName // The id of the channel.
        val name: CharSequence = "Sample one" // The user-visible name of the channel.
        var importance: Int = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH
        }
        val contentView = RemoteViews(packageName, R.layout.custom_push)
        contentView.setImageViewResource(R.id.image, R.mipmap.app_icon)
        contentView.setTextViewText(R.id.title, title)
        if (mess != null) {
            contentView.setTextViewText(R.id.text, Html.fromHtml(mess))
        }
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, CHANNEL_ID)
        //        notificationBuilder.setContentTitle(title);
        notificationBuilder.setAutoCancel(true)
        notificationBuilder.setContent(contentView)
        notificationBuilder.setSmallIcon(R.mipmap.app_icon)
        notificationBuilder.priority = Notification.PRIORITY_HIGH
        notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        //        notificationBuilder.setContentIntent(pendingIntent);
        contentView.setOnClickPendingIntent(R.id.layout, pendingIntent)
        //        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(mess));
//        notificationBuilder.setContentText(Html.fromHtml(mess));
        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE)
        notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder))
        val notify: Notification = notificationBuilder.build()
        notify.flags = notify.flags or Notification.FLAG_AUTO_CANCEL
        val notificationManager: NotificationManager? =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationManager!!.createNotificationChannel(mChannel)
        }
        notificationManager?.notify(notifyID /* ID of notification */, notify)
    }

    private fun getNotificationIcon(notificationBuilder: NotificationCompat.Builder): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val color: Int = 0x036085
            notificationBuilder.color = color
        }
        return R.mipmap.app_icon
    }

    public override fun onNewToken(s: String) {
        super.onNewToken(s)
        if (s.isNotEmpty()) {
            AppSharedPreferences.getInstance(this)!!.deviceToken = s
            Log.e("NEW_TOKEN", s)
            getInstance().passPushToken(applicationContext, s)
        }
    }
}