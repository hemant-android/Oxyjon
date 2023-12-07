package app.oxyjon.ui.kotlin.activity

import android.content.Intent
import android.os.Bundle
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityStepsRecordedBinding
import app.oxyjon.ui.activity.BaseActivity

class StepsRecordedActivity : BaseActivity() {
    lateinit var binding: ActivityStepsRecordedBinding
    var preferences: AppSharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStepsRecordedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppSharedPreferences.getInstance(this)

        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.tvContinue.setOnClickListener {

            preferences!!.isStepCountDialogPopup(true)
            preferences!!.userOnBoard = "2"

            Intent(this, DashboardActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
    }
}