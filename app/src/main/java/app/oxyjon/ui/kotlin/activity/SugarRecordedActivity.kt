package app.oxyjon.ui.kotlin.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivitySugarRecordedBinding
import app.oxyjon.ui.activity.BaseActivity

class SugarRecordedActivity : BaseActivity() {
    var preferences: AppSharedPreferences? = null
    lateinit var binding: ActivitySugarRecordedBinding

    private var errMessage: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySugarRecordedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppSharedPreferences.getInstance(this)

        val bundle = intent.extras
        if (bundle != null) {
            errMessage = bundle.getString("message")!!
        }

        if (errMessage != null && !TextUtils.isEmpty(errMessage)) {
            binding.tvDesc.text = errMessage
        }
        binding.tvContinue.setOnClickListener {

            preferences!!.isSugarDialogPopup(true)

            val intent = Intent(this@SugarRecordedActivity, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}