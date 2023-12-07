package app.oxyjon.ui.kotlin.activity

import android.content.Intent
import android.os.Bundle
import app.oxyjon.databinding.ActivitySetAlarmBinding
import app.oxyjon.ui.activity.AddMedicineActivity
import app.oxyjon.ui.activity.BaseActivity

class SaveAlarmActivity : BaseActivity() {
    lateinit var binding: ActivitySetAlarmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvAddAnotherReminder.setOnClickListener {

             Intent(this, AddMedicineActivity::class.java).also {
//                 it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                 startActivity(it)
                 finish()

             }
        }
    }
}