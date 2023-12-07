package app.oxyjon.ui.kotlin.activity

import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.adapter.AllergyAdapter
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityAllergyBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.retrofit.response.AllergyListResponce
import app.oxyjon.retrofit.response.UpdateStepthreeResponce
import app.oxyjon.retrofit.response.UserDataResponse
import app.oxyjon.retrofit.response.data.AllergyData
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.utils.FunctionHelper
import app.oxyjon.utils.Helper
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import retrofit2.Response
class AllergyActivity : BaseActivity(), IApiCallback {
    lateinit var binding: ActivityAllergyBinding
    var preferences: AppSharedPreferences? = null

    var allergyAdapter: AllergyAdapter? = null
    var list = ArrayList<AllergyData?>()
    var selecteexperties = HashMap<String?, AllergyData?>()
    var listdata = ArrayList<String>()
    var progressDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllergyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MainApplication.currentActivity = this
        preferences = AppSharedPreferences.getInstance(this)

        progressDialog = Helper.initProgress(this)

        setRecyclerView()
        val handler = Handler()
        handler.postDelayed({ getupdatestepthreedata() }, 100)


        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.tvSave.setOnClickListener {
            selectedData
        }

    }

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "getlist") {
            val response = data as Response<AllergyListResponce>
            if (response.isSuccessful) {
                list.clear()
                val lists = response.body()!!.data
                if (lists == null) {
                    allergyAdapter!!.notifyDataSetChanged()
                } else {
                    list.clear()
                    list.addAll(lists)
                    allergyAdapter!!.notifyDataSetChanged()
                }
            }
        } else if (type == "updatethree") {
            val response = data as Response<UserDataResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {
                    Toast.makeText(this, response.body()!!.errorMsg, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, response.body()!!.errorMsg, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else if (type == "getupdatestepthree") {
            progressDialog!!.dismiss()
            val response = data as Response<UpdateStepthreeResponce>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {
                    val updateddata = response.body()!!.data
                    if (updateddata!!.isEmpty()) {
                    } else {
                        list.clear()
                        val listdata = response.body()!!.data?.get(0)!!.allergyData
                        list.addAll(listdata!!)
                        allergyAdapter!!.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }

    private fun getupdatestepthreedata() {
        if (isConnection(this)) {
            ApiCall.instance.getupdatestepthree(this)
        } else {
            Toast.makeText(this, "please check your internet connection", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setRecyclerView() {
        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.justifyContent = JustifyContent.FLEX_START
        binding.rvAllergy!!.layoutManager = layoutManager
        allergyAdapter = AllergyAdapter(this, list, listdata, selecteexperties)
        binding.rvAllergy!!.adapter = allergyAdapter
    }

    private val selectedData: Unit
        get() {
            if (isConnection(this)) {
                var addId = ""
                val serviceName = ""
                var currentString = ""
                val data = allergyAdapter!!.selecteddata()
                val size = data!!.size
                if (data.size == 0) {
                    FunctionHelper.disable_user_Intration(
                        this,
                        resources.getString(R.string.loading)
                    )
                    ApiCall.instance
                        .getprofileUpdateStepthree(currentString, this)
                } else {
                    for ((_, value) in data) {
                        val id = value!!.allergyId
                        addId = "$addId,$id"
                    }
                    currentString = addId
                    currentString = currentString.substring(1)
                    FunctionHelper.disable_user_Intration(
                        this,
                        resources.getString(R.string.loading)
                    )
                    ApiCall.instance
                        .getprofileUpdateStepthree(currentString, this)
                }
            } else {
                Toast.makeText(this, "please check your internet connection", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    fun isConnection(ctx: Context?): Boolean {
        val connectivityManager =
            ctx!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val ni = connectivityManager.activeNetworkInfo
        return ni != null && ni.isAvailable && ni.isConnected
    }
}