package app.oxyjon.ui.kotlin.activity

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityAddFamilyMemberBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.retrofit.response.CommonResponse
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.utils.CheckConnection
import app.oxyjon.utils.FunctionHelper
import retrofit2.Response
class AddFamilyMemberActivity : BaseActivity(), IApiCallback {
    lateinit var binding: ActivityAddFamilyMemberBinding
    var preferences: AppSharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFamilyMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)


        preferences = AppSharedPreferences.getInstance(this)

        MainApplication.currentActivity = this

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.tvAddMember.setOnClickListener {

            if (TextUtils.isEmpty(binding.edtName.text.toString().trim())) {
                binding.edtName.requestFocus()
                Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(binding.edtRelationShip.text.toString().trim())) {
                binding.edtRelationShip.requestFocus()
                Toast.makeText(this, "Please enter relationship", Toast.LENGTH_SHORT)
                    .show()
            } else if (TextUtils.isEmpty(binding.edtMobileNumber.text.toString().trim())) {
                binding.edtMobileNumber.requestFocus()
                Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT)
                    .show()
            }  else {
                if (CheckConnection.isConnection(MainApplication.currentActivity!!)) {
                    FunctionHelper.disable_user_Intration(
                        this,
                        resources.getString(R.string.loading)
                    )
                    ApiCall.instance.addFamilyMember(preferences!!.getprofileid(),
                        binding.edtName.text.toString().trim(),
                        binding.edtRelationShip.text.toString().trim(),
                        binding.edtMobileNumber.text.toString().trim(),
                        "",
                        this)
                } else {
                    Toast.makeText(this,
                        "please check your internet connection",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "addFamilyMember") {
            val response = data as Response<CommonResponse>
            if (response.isSuccessful) {
                if (response.isSuccessful && response.body()!!.errorCode == "1") {
                    finish()
                }
            }

        }
    }
    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }
}