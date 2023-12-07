package app.oxyjon.ui.kotlin.activity

import android.os.Bundle
import app.oxyjon.databinding.ActivityNotificationListBinding
import app.oxyjon.ui.activity.BaseActivity

class NotificationListActivity : BaseActivity() {
    lateinit var binding: ActivityNotificationListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBack.setOnClickListener {
            finish()
        }
    }
}