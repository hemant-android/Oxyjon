package app.oxyjon.ui.kotlin.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import app.oxyjon.R
import app.oxyjon.bean.BPResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityViewBpBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.ui.kotlin.activity.adapter.BPViewAdapter
import app.oxyjon.utils.CheckConnection.isConnection
import app.oxyjon.utils.FunctionHelper
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper
import retrofit2.Response

class ViewBPActivity : BaseActivity(), IApiCallback {
    lateinit var binding: ActivityViewBpBinding
    var preferences: AppSharedPreferences? = null

    private val mBPViewAdapter: BPViewAdapter by lazy { BPViewAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppSharedPreferences.getInstance(this)

        binding.rvBP.adapter = mBPViewAdapter

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.tvAdd.setOnClickListener {
            val intent = Intent(this, AddBloodPressureActivity::class.java)
            startActivity(intent)

            val properties = Properties()
            properties.addAttribute("isClickAddBP", true)
            MoEAnalyticsHelper.trackEvent(this, "ClickAddBP", properties)
        }

        if (isConnection(this)) {
            FunctionHelper.disable_user_Intration(this, resources.getString(R.string.loading))
            ApiCall.instance.getBPDetail(preferences!!.getprofileid(), this)
        } else {
            Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "BPViewDetail") {
            val response = data as Response<BPResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {
                    if (response.body()!!.data != null && response.body()!!.data.isNotEmpty()) {
                        binding.llBPData.visibility = View.VISIBLE
                        binding.tvNoRecordFound.visibility = View.GONE
                        mBPViewAdapter.setData(response.body()!!.data)
                    } else {
                        binding.llBPData.visibility = View.GONE
                        binding.tvNoRecordFound.visibility = View.VISIBLE
                    }
                }else{
                    binding.llBPData.visibility = View.GONE
                    binding.tvNoRecordFound.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }
}