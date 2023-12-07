package app.oxyjon.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import app.oxyjon.R


class AlarmBroadcastReceiver : BroadcastReceiver() {
    var alarmType: Int = 0
    var medicineName: String? = ""
    var medicineTime: String? = ""
    var context: Context? = null
    var mp: MediaPlayer? = null
    override fun onReceive(context: Context, intent: Intent) {
        this.context = context
        if (mp != null && mp!!.isPlaying) {
            mp!!.stop()
            mp!!.release()
        }
        mp = null
        mp = MediaPlayer.create(context, R.raw.medicine_reminder)
        mp!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mp!!.start()
        val vib: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vib.vibrate(3000)
        mp!!.setOnCompletionListener { mp ->
            try {
                if (mp.isPlaying) {
                    mp.stop()
                }
                mp.release()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
        if (intent != null) {
            NotifJobIntentService.enqueueWork(context, intent)
            alarmType = intent.getIntExtra("alarmValue", 0)
            medicineName = intent.getStringExtra("medicineName")
            medicineTime = intent.getStringExtra("medicineTime")
        }
        Log.e("alarmValue is: ", alarmType.toString())
        Toast.makeText(context, "Alarm...", Toast.LENGTH_LONG).show()
    }
}