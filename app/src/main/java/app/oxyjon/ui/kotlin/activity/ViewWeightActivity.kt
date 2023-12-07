package app.oxyjon.ui.kotlin.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import app.oxyjon.R
import app.oxyjon.bean.WeightResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityViewWeightBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.ui.kotlin.activity.adapter.WeightViewAdapter
import app.oxyjon.utils.CheckConnection.isConnection
import app.oxyjon.utils.FunctionHelper
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper
import retrofit2.Response

class ViewWeightActivity : BaseActivity(), IApiCallback {
    lateinit var binding: ActivityViewWeightBinding
    var preferences: AppSharedPreferences? = null

    private val mWeightAdapter: WeightViewAdapter by lazy { WeightViewAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewWeightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppSharedPreferences.getInstance(this)

        binding.rvWeight.adapter = mWeightAdapter

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.tvAdd.setOnClickListener {
            val intent = Intent(this, AddWeightActivity::class.java)
            startActivity(intent)

            val properties = Properties()
            properties.addAttribute("isClickAddWeight", true)
            MoEAnalyticsHelper.trackEvent(this, "ClickAddWeight", properties)
        }

        if (isConnection(this)) {
            FunctionHelper.disable_user_Intration(this, resources.getString(R.string.loading))
            ApiCall.instance.getWeightDetail(preferences!!.getprofileid(), this)
        } else {
            Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "WeightViewDetail") {
            val response = data as Response<WeightResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {
                    if (response.body()!!.data != null && response.body()!!.data.isNotEmpty()) {
                        binding.llWeightData.visibility = View.VISIBLE
                        binding.tvNoRecordFound.visibility = View.GONE
                        mWeightAdapter.setData(response.body()!!.data)
                    } else {
                        binding.llWeightData.visibility = View.GONE
                        binding.tvNoRecordFound.visibility = View.VISIBLE
                    }
                } else {
                    binding.llWeightData.visibility = View.GONE
                    binding.tvNoRecordFound.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }
}