package app.oxyjon.ui.kotlin.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.adapter.WeightListAdapter
import app.oxyjon.bean.GetProfileResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityEditProfileBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.retrofit.response.LoginSignUpResponse
import app.oxyjon.retrofit.response.UserDataResponse
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.ui.kotlin.activity.adapter.PhysicalActiveAdapter
import app.oxyjon.utils.CheckConnection
import app.oxyjon.utils.FunctionHelper
import app.oxyjon.utils.GridSpacingItemDecoration
import com.moengage.core.analytics.MoEAnalyticsHelper
import com.moengage.core.model.UserGender
import retrofit2.Response
import java.text.DecimalFormat

class EditProfileActivity : BaseActivity(), IApiCallback, WeightListAdapter.OnClickListener,
    PhysicalActiveAdapter.ClickListener {
    lateinit var binding: ActivityEditProfileBinding
    var preferences: AppSharedPreferences? = null

    var weightAdapter: WeightListAdapter? = null

    var df = DecimalFormat()
    var checkedItemFeet = -1
    var checkedItemInch = -1

    private var strWeight = ""
    private var gender = ""
    var age = 0.0

    var feet = arrayOf("1", "2", "3", "4", "5", "6")
    var inch = arrayOf("0","1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")

    var adapter: PhysicalActiveAdapter? = null
    var activityKey: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        preferences = AppSharedPreferences.getInstance(this)

        MainApplication.currentActivity = this

        weightAdapter = WeightListAdapter(this)
        binding.rvWeight.adapter = weightAdapter
        weightAdapter!!.setClickListener(this)

        getupdateonelist()

        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.tvFeet.setOnClickListener {

            selectFeet()
        }

        binding.tvInches.setOnClickListener {

            selectInches()
        }

        binding.imgPlus.setOnClickListener {
            if (!TextUtils.isEmpty(binding.edtAge.text.toString().trim { it <= ' ' })) {
                age = binding.edtAge.text.toString().trim { it <= ' ' }.toDouble()
                age += 1
                binding.edtAge.setText("" + df.format(age))
            }
        }

        binding.imgMinius.setOnClickListener {
            if (!TextUtils.isEmpty(binding.edtAge.text.toString().trim { it <= ' ' })) {
                age = binding.edtAge.text.toString().trim { it <= ' ' }.toDouble()
                age -= 1
                if (age > 0) {
                    binding.edtAge.setText("" + df.format(age))
                }
            }
        }

        binding.llMale.setOnClickListener {
            gender = "M"
            binding.tvMale.setTextColor(resources.getColor(R.color.white))
            binding.tvFeMale.setTextColor(resources.getColor(R.color.blueDark))

            binding.llMale.setBackgroundResource(R.drawable.drawable_gender_selected)
            binding.llFeMale.setBackgroundResource(R.drawable.drawable_gender_default)

            binding.imgFeMale.setImageResource(R.drawable.ic_female)
            binding.imgMale.setImageResource(R.drawable.ic_male)

            binding.imgMale.setColorFilter(
                ContextCompat.getColor(
                    this, R.color.white
                ), PorterDuff.Mode.SRC_IN
            )
            binding.imgFeMale.setColorFilter(
                ContextCompat.getColor(
                    this, R.color.blue_color
                ), PorterDuff.Mode.SRC_IN
            )
        }

        binding.llFeMale.setOnClickListener {
            gender = "F"
            binding.tvFeMale.setTextColor(resources.getColor(R.color.white))
            binding.tvMale.setTextColor(resources.getColor(R.color.blueDark))

            binding.llFeMale.setBackgroundResource(R.drawable.drawable_gender_selected)
            binding.llMale.setBackgroundResource(R.drawable.drawable_gender_default)

            binding.imgMale.setImageResource(R.drawable.ic_male)
            binding.imgFeMale.setImageResource(R.drawable.ic_female)

            binding.imgFeMale.setColorFilter(
                ContextCompat.getColor(
                    this, R.color.white
                ), PorterDuff.Mode.SRC_IN
            )
            binding.imgMale.setColorFilter(
                ContextCompat.getColor(
                    this, R.color.blue_color
                ), PorterDuff.Mode.SRC_IN
            )
        }

        binding.tvUpdate.setOnClickListener {

            var strName = binding.edtUserName.text.toString().trim()
            var strAge = binding.edtAge.text.toString().trim()
            var strMail = binding.edtEmail.text.toString().trim()
            val strFeet: String = binding.tvFeet.text.toString().split(" ").toTypedArray()[0]
            val strInches: String = binding.tvInches.text.toString().split(" ").toTypedArray()[0]

            if (TextUtils.isEmpty(strName)) {
                Toast.makeText(this, "please enter your name", Toast.LENGTH_SHORT).show()
                binding.edtUserName.requestFocus()
            } else if (TextUtils.isEmpty(strAge)) {
                Toast.makeText(this, "please enter your age", Toast.LENGTH_SHORT).show()
                binding.edtAge.requestFocus()
            } else if (TextUtils.isEmpty(gender)) {
                Toast.makeText(this, "please select gender", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(strFeet)) {
                Toast.makeText(this, "please select feet", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(strInches)) {
                Toast.makeText(this, "please select inch", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(strWeight)) {
                Toast.makeText(this, "please select weight", Toast.LENGTH_SHORT).show()
            } else {
                FunctionHelper.disable_user_Intration(
                    this, resources.getString(R.string.loading)
                )

                MoEAnalyticsHelper.setFirstName(this,
                    binding.edtUserName.text.toString().trim { it <= ' ' })
                MoEAnalyticsHelper.setUserName(this,
                    binding.edtUserName.text.toString().trim { it <= ' ' })

                if (gender.equals("M", ignoreCase = true)) {
                    MoEAnalyticsHelper.setGender(this, UserGender.MALE)
                } else {
                    MoEAnalyticsHelper.setGender(this, UserGender.FEMALE)
                }

                if (!TextUtils.isEmpty(binding.edtAge.text.toString().trim())) {
                    MoEAnalyticsHelper.setBirthDate(
                        this, binding.edtAge.text.toString().trim()
                    )
                }

                if (!TextUtils.isEmpty(strFeet)) {
                    MoEAnalyticsHelper.setUserAttribute(this, "feet", strFeet.toInt())
                }
                if (!TextUtils.isEmpty(strInches)) {
                    MoEAnalyticsHelper.setUserAttribute(
                        this, "inches", strInches.toInt()
                    )
                }
                if (!TextUtils.isEmpty(strWeight)) {
                    MoEAnalyticsHelper.setUserAttribute(
                        this, "weight", strWeight.toDouble()
                    )
                }

                if (CheckConnection.isConnection(this)) {
                    ApiCall.instance.updateProfile(
                        strName,
                        strAge,
                        gender,
                        strMail,
                        strFeet,
                        strInches,
                        strWeight,
                        activityKey,
                        this
                    )
                } else {
                    Toast.makeText(
                        this, "please check your internet connection", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this
    }

    private fun selectFeet() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Choose an feet")
        alertDialog.setSingleChoiceItems(
            feet,
            checkedItemFeet
        ) { dialog: DialogInterface, which: Int ->
            checkedItemFeet = which
            binding.tvFeet.text = feet[which] + " Feet"
            preferences!!.heightFeet = feet[which] + ""
            dialog.dismiss()
        }
        alertDialog.setNegativeButton(
            "Cancel"
        ) { dialog: DialogInterface?, which: Int -> }
        val customAlertDialog = alertDialog.create()
        customAlertDialog.show()
    }

    private fun selectInches() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Choose an inch")
        alertDialog.setSingleChoiceItems(
            inch,
            checkedItemInch
        ) { dialog: DialogInterface, which: Int ->
            checkedItemInch = which
            binding.tvInches.text = inch[which] + " Inches"
            preferences!!.heightInch = inch[which] + ""
            dialog.dismiss()
        }
        alertDialog.setNegativeButton(
            "Cancel"
        ) { dialog: DialogInterface?, which: Int -> }
        val customAlertDialog = alertDialog.create()
        customAlertDialog.show()
    }

    override fun onWeightItemClick(weight: String, pos: Int) {
        strWeight = weight!!
        binding.rvWeight.scrollToPosition(pos)
        preferences!!.weight = strWeight
    }

    override fun onRecyclerItemClick(pos: String) {
        activityKey = pos
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()

        if (type == "getprofileUpdateStepOne") {
            val response = data as Response<UserDataResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {
                    Toast.makeText(
                        this,
                        response.body()!!.errorMsg,
                        Toast.LENGTH_SHORT
                    ).show()
                    preferences!!.setprofileid(response.body()!!.data!![0].profileId)
                } else {
                    if (response.body()!!.errorMsg != null) {
                        Toast.makeText(
                            this,
                            response.body()!!.errorMsg,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        if (type == "updateProfile") {
            val response = data as Response<GetProfileResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {
                    Toast.makeText(
                        this,
                        response.body()!!.errorMsg,
                        Toast.LENGTH_SHORT
                    ).show()
                    getupdateonelist()
//                    preferences!!.setprofileid(response.body()!!.data[0].profileId)
                } else {
                    Toast.makeText(
                        this,
                        response.body()!!.errorMsg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        if (type == "getcustomerdetails") {
            val response = data as Response<LoginSignUpResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {
                    val loginData = response.body()!!.data
                    if (loginData!!.isNotEmpty()) {
                        Log.d("TAG", "customer details " + loginData?.get(0)!!.userName)
                        binding.edtUserName.setText(loginData[0].userName)
                    }
                } else {
                    if (response.body()!!.errorMsg != null) {
                        Toast.makeText(
                            this,
                            response.body()!!.errorMsg,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        if (type == "getProfile") {
            val response = data as Response<GetProfileResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {
                    val data1 = response.body()!!.data
                    if (data1 != null) {
                        preferences!!.setprofileid(data1.profileId)
                        preferences!!.fullName = data1.name

                        binding.edtUserName.setText(data1.name)

                        binding.edtAge.setText(data1.age)

                        if (data1.mobile != null) {
                            binding.edtMobileNumber.setText(data1.mobile)
                        }
                        if (data1.email != null) {
                            binding.edtEmail.setText(data1.email)
                        }

                        when (data1.gender) {
                            "M" -> {
                                gender = "M"

                                binding.tvMale.setTextColor(resources.getColor(R.color.white))
                                binding.tvFeMale.setTextColor(resources.getColor(R.color.blueDark))

                                binding.llMale.setBackgroundResource(R.drawable.drawable_gender_selected)
                                binding.llFeMale.setBackgroundResource(R.drawable.drawable_gender_default)

                                binding.imgFeMale.setImageResource(R.drawable.ic_female)
                                binding.imgMale.setImageResource(R.drawable.ic_male)

                                binding.imgMale.setColorFilter(
                                    ContextCompat.getColor(
                                        this,
                                        R.color.white
                                    ), PorterDuff.Mode.SRC_IN
                                )
                                binding.imgFeMale.setColorFilter(
                                    ContextCompat.getColor(
                                        this,
                                        R.color.blue_color
                                    ), PorterDuff.Mode.SRC_IN
                                )

                            }

                            "F" -> {
                                gender = "F"
                                binding.tvFeMale.setTextColor(resources.getColor(R.color.white))
                                binding.tvMale.setTextColor(resources.getColor(R.color.blueDark))

                                binding.llFeMale.setBackgroundResource(R.drawable.drawable_gender_selected)
                                binding.llMale.setBackgroundResource(R.drawable.drawable_gender_default)

                                binding.imgMale.setImageResource(R.drawable.ic_male)
                                binding.imgFeMale.setImageResource(R.drawable.ic_female)

                                binding.imgFeMale.setColorFilter(
                                    ContextCompat.getColor(
                                        this,
                                        R.color.white
                                    ), PorterDuff.Mode.SRC_IN
                                )
                                binding.imgMale.setColorFilter(
                                    ContextCompat.getColor(
                                        this,
                                        R.color.blue_color
                                    ), PorterDuff.Mode.SRC_IN
                                )
                            }

                            else -> {
                                gender = ""

                                binding.tvMale.setTextColor(resources.getColor(R.color.blueDark))
                                binding.tvFeMale.setTextColor(resources.getColor(R.color.blueDark))

                                binding.llMale.setBackgroundResource(R.drawable.drawable_gender_default)
                                binding.llFeMale.setBackgroundResource(R.drawable.drawable_gender_default)

                                binding.imgFeMale.setImageResource(R.drawable.ic_female)
                                binding.imgMale.setImageResource(R.drawable.ic_male)

                                binding.imgMale.setColorFilter(
                                    ContextCompat.getColor(
                                        this,
                                        R.color.blue_color
                                    ), PorterDuff.Mode.SRC_IN
                                )
                                binding.imgFeMale.setColorFilter(
                                    ContextCompat.getColor(
                                        this,
                                        R.color.blue_color
                                    ), PorterDuff.Mode.SRC_IN
                                )
                            }
                        }

                        if (data1.height_ft != null && !TextUtils.isEmpty(data1.height_ft)) {
                            binding.tvFeet.text = data1.height_ft + " Feet"

                            for (i in feet.indices) {
                                if (feet[i].equals(
                                        data1.height_ft,
                                        ignoreCase = true
                                    )
                                ) {
                                    checkedItemFeet = i
                                }
                            }
                        }

                        if (data1.height_inches != null && !TextUtils.isEmpty(data1.height_inches)) {
                            binding.tvInches.text = data1.height_inches + " Inches"

                            for (i in inch.indices) {
                                if (inch[i].equals(
                                        data1.height_inches,
                                        ignoreCase = true
                                    )
                                ) {
                                    checkedItemInch = i
                                }
                            }
                        }

                        if (data1.weight != null && !TextUtils.isEmpty(data1.weight)) {
                            if (weightAdapter != null) {
                                weightAdapter!!.setSelectedData(data1.weight)
                                strWeight = data1.weight
                                binding.rvWeight.scrollToPosition(strWeight.toInt() - 10)
                            }
                        }

                        if (data1.activity_list?.size!! > 0) {
                            setActiveRecycler(data1.activity_list, data1.activity_score.toString())
                        }
                    }
                }
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }

    private fun getupdateonelist() {
        if (CheckConnection.isConnection(this)) {
            FunctionHelper.disable_user_Intration(
                this,
                resources.getString(R.string.loading)
            )
            ApiCall.instance.getProfile(this)
        } else {
            Toast.makeText(this, "please check your internet connection", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setActiveRecycler(
        data: ArrayList<GetProfileResponse.Data.Activity>,
        activityScore: String,
    ) {
        for (pos in data.indices) {
            data[pos].selectedOption = data[pos].key == activityScore
        }

        val staggeredGridLayoutManager = GridLayoutManager(this, 2)
        binding.rvPhysicallyActive!!.layoutManager = staggeredGridLayoutManager
        binding.rvPhysicallyActive!!.addItemDecoration(GridSpacingItemDecoration(2, 20, false))
        adapter = PhysicalActiveAdapter(
            this,
            this,
            data,
            activityScore!!
        )
        binding.rvPhysicallyActive!!.adapter = adapter

        activityKey = activityScore
    }
}