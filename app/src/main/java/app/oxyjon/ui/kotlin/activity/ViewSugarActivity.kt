package app.oxyjon.ui.kotlin.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import app.oxyjon.R
import app.oxyjon.adapter.SelectMedicineTimeAdapter
import app.oxyjon.bean.MedicineTimingModel
import app.oxyjon.bean.SugarDetailResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityViewSugarBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.ui.kotlin.activity.adapter.SugarViewAdapter
import app.oxyjon.utils.CheckConnection.isConnection
import app.oxyjon.utils.FunctionHelper
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper
import retrofit2.Response

class ViewSugarActivity : BaseActivity(), IApiCallback, SelectMedicineTimeAdapter.ClickListener {
    private var arrSugarData: java.util.ArrayList<SugarDetailResponse.Data>? = arrayListOf()
    lateinit var binding: ActivityViewSugarBinding
    var preferences: AppSharedPreferences? = null

    private val mSugarAdapter: SugarViewAdapter by lazy { SugarViewAdapter(this) }
    private val mTimeAdapter: SelectMedicineTimeAdapter by lazy { SelectMedicineTimeAdapter(this) }

    private val mTimingArr = ArrayList<MedicineTimingModel>()
    private val addMedicineArr = ArrayList<String>()
    private var selectTime: String? = ""

    var checkedItemNoOfDays: Int = 1
    var noOfDays: Array<String?> = arrayOf("15", "30", "60", "90")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewSugarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppSharedPreferences.getInstance(this)

        addMedicineArr.add("All")
        addMedicineArr.add("Fasting")
        addMedicineArr.add("Post- Breakfast")
        addMedicineArr.add("Pre- Lunch")
        addMedicineArr.add("Post Lunch")
        addMedicineArr.add("Pre Dinner")
        addMedicineArr.add("Post Dinner")
        addMedicineArr.add("3 AM")
        addMedicineArr.add("Random")

        binding.rvTimeFilter.adapter = mTimeAdapter
        binding.rvSugar.adapter = mSugarAdapter

        mTimeAdapter.setClickListener(this)

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.tvAdd.setOnClickListener {
            val intent = Intent(this, AddSugarActivity::class.java)
            startActivity(intent)

            val properties = Properties()
            properties.addAttribute("isClickAddSugar", true)
            MoEAnalyticsHelper.trackEvent(this, "ClickAddSugarDiary", properties)
        }

        binding.tvFilter.setOnClickListener {
            selectDays()
        }


        if (isConnection(this)) {
            FunctionHelper.disable_user_Intration(this, resources.getString(R.string.loading))
            ApiCall.instance.getSugarDetail("30", this)
        } else {
            Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "sugarViewDetail") {
            val response = data as Response<SugarDetailResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {
                    if (arrSugarData?.size!! > 0) {
                        arrSugarData!!.clear()
                    }

                    if (mTimingArr?.size!! > 0) {
                        mTimingArr.clear()
                    }

                    for (i in addMedicineArr.indices) {
                        val model = MedicineTimingModel()
                        model.medicineTime = addMedicineArr[i]
                        model.select = model.medicineTime == "All"
                        mTimingArr.add(model)
                    }

                    mTimeAdapter.setData(mTimingArr!!)

                    if (response.body()!!.data != null && response.body()!!.data.isNotEmpty()) {
                        binding.llSugarData.visibility = View.VISIBLE
                        binding.tvNoRecordFound.visibility = View.GONE

                        arrSugarData = response.body()!!.data
                        mSugarAdapter.setData(arrSugarData)
                    } else {
                        binding.llSugarData.visibility = View.GONE
                        binding.tvNoRecordFound.visibility = View.VISIBLE
                    }
                } else {
                    binding.llSugarData.visibility = View.GONE
                    binding.tvNoRecordFound.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }

    override fun onSelectTimeClick(selectedItem: String?, position: Int) {
        when (selectedItem) {
            "All" -> {
                mSugarAdapter.setData(arrSugarData)
            }

            "Fasting" -> {
                selectTime = "Fasting"
                filter(selectTime!!)
            }

            "Post- Breakfast" -> {
                selectTime = "PP"
                filter(selectTime!!)
            }

            "Pre- Lunch" -> {
                selectTime = "BeforeLunch"
                filter(selectTime!!)
            }

            "Post Lunch" -> {
                selectTime = "AfterLunch"
                filter(selectTime!!)
            }

            "Pre Dinner" -> {
                selectTime = "BeforeDinner"
                filter(selectTime!!)
            }

            "Post Dinner" -> {
                selectTime = "AfterDinner"
                filter(selectTime!!)
            }

            "3 AM" -> {
                selectTime = "Midnight"
                filter(selectTime!!)
            }

            "Random" -> {
                selectTime = "Random"
                filter(selectTime!!)
            }
        }

        for (i in mTimingArr.indices) {
            mTimingArr[i].select = mTimingArr[i].medicineTime == selectedItem
        }
        mTimeAdapter.notifyDataSetChanged()
    }

    private fun filter(text: String) {
        val temp: MutableList<SugarDetailResponse.Data> =
            ArrayList()
        if (arrSugarData!!.isNotEmpty()) {
            for (d in arrSugarData!!) {
                if (d.type!!.lowercase().contains(text.lowercase())) {
                    temp!!.add(d)
                }
            }
            mSugarAdapter.setData(temp as ArrayList<SugarDetailResponse.Data>)
        }
    }

    private fun selectDays() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@ViewSugarActivity)
        alertDialog.setTitle("Choose an days")
        alertDialog.setSingleChoiceItems(noOfDays,
            checkedItemNoOfDays,
            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                checkedItemNoOfDays = which
                binding.tvFilter!!.text = noOfDays[which] + " Days"
                dialog.dismiss()

                if (isConnection(this)) {
                    FunctionHelper.disable_user_Intration(
                        this,
                        resources.getString(R.string.loading)
                    )
                    ApiCall.instance.getSugarDetail(noOfDays[which], this)
                } else {
                    Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT)
                        .show()
                }
            })
        alertDialog.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int -> })
        val customAlertDialog: AlertDialog = alertDialog.create()
        customAlertDialog.show()
    }
}