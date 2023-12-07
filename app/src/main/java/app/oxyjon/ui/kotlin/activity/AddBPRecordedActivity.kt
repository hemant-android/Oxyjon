package app.oxyjon.ui.kotlin.activity

import android.content.Intent
import android.os.Bundle
import app.oxyjon.MainApplication
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityAddBpRecordedBinding
import app.oxyjon.ui.activity.BaseActivity

class AddBPRecordedActivity : BaseActivity() {
    var preferences: AppSharedPreferences? = null
    lateinit var binding: ActivityAddBpRecordedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBpRecordedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppSharedPreferences.getInstance(this)

        MainApplication.currentActivity = this

        binding.tvContinue.setOnClickListener {
            val intent = Intent(this@AddBPRecordedActivity, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this
    }
}