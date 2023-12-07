package app.oxyjon.ui.kotlin.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import app.oxyjon.R
import app.oxyjon.bean.ThingsToDoAvoidResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityThingDoAvoidBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.ui.kotlin.activity.adapter.ThingAdditionalAdapter
import app.oxyjon.ui.kotlin.activity.adapter.ThingAvoidAdapter
import app.oxyjon.ui.kotlin.activity.adapter.ThingDoAdapter
import app.oxyjon.utils.CheckConnection.isConnection
import app.oxyjon.utils.FunctionHelper
import retrofit2.Response

class ThingsAvoidActivity : BaseActivity(), IApiCallback {
    lateinit var binding: ActivityThingDoAvoidBinding
    var preferences: AppSharedPreferences? = null

    private val mThingDoAdapter: ThingDoAdapter by lazy { ThingDoAdapter(this) }
    private val mThingAvoidAdapter: ThingAvoidAdapter by lazy { ThingAvoidAdapter(this) }
    private val mThingAdditionalAdapter: ThingAdditionalAdapter by lazy { ThingAdditionalAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThingDoAvoidBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppSharedPreferences.getInstance(this)

        binding.rvDoData.adapter = mThingDoAdapter
        binding.rvAvoidData.adapter = mThingAvoidAdapter
        binding.rvAdditionalData.adapter = mThingAdditionalAdapter

        binding.imgBack.setOnClickListener {
            finish()
        }

        if (isConnection(this)) {
            FunctionHelper.disable_user_Intration(this, resources.getString(R.string.loading))
            ApiCall.instance.geThingsToDoAvoidDetail(this)
        } else {
            Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "ThingsToDoAvoidResponse") {
            val response = data as Response<ThingsToDoAvoidResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {
                    if (response.body()!!.data != null && response.body()!!.data?.to_do?.size!! > 0) {
                        binding.llDo.visibility = View.VISIBLE

                        mThingDoAdapter.setData(response.body()!!.data?.to_do!!)
                    }else{
                        binding.llDo.visibility = View.GONE
                    }
                    if (response.body()!!.data != null && response.body()!!.data?.to_do_not?.size!! > 0) {
                        binding.llAvoid.visibility = View.VISIBLE
                        mThingAvoidAdapter.setData(response.body()!!.data?.to_do_not!!)
                    }else{
                        binding.llAvoid.visibility = View.GONE
                    }
                    if (response.body()!!.data != null && response.body()!!.data?.others_to_do?.size!! > 0) {
                        binding.llAdditional.visibility = View.VISIBLE
                        mThingAdditionalAdapter.setData(response.body()!!.data?.others_to_do!!)
                    }else{
                        binding.llAdditional.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }
}