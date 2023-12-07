package app.oxyjon.ui.kotlin.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.bean.DocumentReportResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityDocumentListBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.ui.activity.WebViewActivity
import app.oxyjon.ui.kotlin.activity.adapter.DoctorPrescriptionAdapter
import app.oxyjon.ui.kotlin.activity.adapter.TestReportAdapter
import app.oxyjon.utils.FunctionHelper
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper
import com.moengage.inapp.MoEInAppHelper
import retrofit2.Response

class DocumentListActivity : BaseActivity(), IApiCallback, DoctorPrescriptionAdapter.onClickListner,
    TestReportAdapter.onClickListner {

    private lateinit var mReportData: DocumentReportResponse.Data
    lateinit var binding: ActivityDocumentListBinding
    var preferences: AppSharedPreferences? = null

    private var arrMedicalPrescription: ArrayList<DocumentReportResponse.Data.MedicalPrescription>? =
        ArrayList()

    private val mDoctorPrescriptionAdapter = DoctorPrescriptionAdapter(this)
    private val mTestReportAdapter = TestReportAdapter(this)

    override fun onStart() {
        super.onStart()
        MoEInAppHelper.getInstance().showInApp(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        MoEInAppHelper.getInstance().onConfigurationChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = AppSharedPreferences.getInstance(this)

        MainApplication.currentActivity = this

        binding.rvDoctorPrescription.adapter = mDoctorPrescriptionAdapter
        binding.rvTestReports.adapter = mTestReportAdapter

        mDoctorPrescriptionAdapter.setClickListner(this)
        mTestReportAdapter.setClickListner(this)

        binding.rvDoctorPrescription.visibility = View.VISIBLE
        binding.rvTestReports.visibility = View.GONE

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.tvAddNewDocument!!.setOnClickListener {

            val properties = Properties()
            properties.addAttribute("isClick", true)
            MoEAnalyticsHelper.trackEvent(this, "ClickAddNewDocumentButton", properties)
            callIntent(Intent(this@DocumentListActivity, UploadDocumentActivity::class.java))
        }

        binding.llDoctorPrescription.setOnClickListener {
            binding.rvDoctorPrescription.visibility = View.VISIBLE
            binding.rvTestReports.visibility = View.GONE

            binding.tvDoctorPrescription.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.tvTestReports.setTextColor(ContextCompat.getColor(this, R.color.gray))

            binding.viewOne.setBackgroundResource(R.color.orange)
            binding.viewTwo.setBackgroundResource(R.color.gray)
        }
        binding.llTestReports.setOnClickListener {
            binding.rvDoctorPrescription.visibility = View.GONE
            binding.rvTestReports.visibility = View.VISIBLE

            binding.tvDoctorPrescription.setTextColor(ContextCompat.getColor(this, R.color.gray))
            binding.tvTestReports.setTextColor(ContextCompat.getColor(this, R.color.black))

            binding.viewOne.setBackgroundResource(R.color.gray)
            binding.viewTwo.setBackgroundResource(R.color.orange)
        }
    }

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this

        if (isConnection(this@DocumentListActivity)) {
            FunctionHelper.disable_user_Intration(this, resources.getString(R.string.loading))
            ApiCall.instance.getMyDocumentList(preferences!!.getprofileid(), this)
        } else {
            Toast.makeText(
                this@DocumentListActivity,
                "please check your internet connection",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "myDocumentList") {
            val response = data as Response<DocumentReportResponse>
            if (response.isSuccessful && response.body()!!.errorCode == "0") {
                if (response.body()!!.data != null) {

                    mReportData = response.body()!!.data
                    if (arrMedicalPrescription?.size!! > 0) {
                        arrMedicalPrescription!!.clear()
                    }

                    if (response.body()!!.data.medical_prescription?.size!! > 0) {
                        mDoctorPrescriptionAdapter.setData(response.body()!!.data.medical_prescription)
                    }

                    if (response.body()!!.data.lab_test_report?.size!! > 0) {
                        mTestReportAdapter.setData(response.body()!!.data.lab_test_report)
                    }

                } else {
                }
            } else {
                if (arrMedicalPrescription?.size!! > 0) {
                    arrMedicalPrescription!!.clear()
                }
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }

    fun callIntent(intent: Intent) {
        startActivity(intent)
    }


    companion object {
        fun isConnection(ctx: Context): Boolean {
            val connectivityManager =
                ctx.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val ni = connectivityManager.activeNetworkInfo
            return ni != null && ni.isAvailable && ni.isConnected
        }

    }

    override fun onSelectItem(fileUrl: String?, fileName: String?) {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("navType", "Document")
        intent.putExtra("docUrl", fileUrl)
        intent.putExtra("docName", fileName)
        startActivity(intent)

        val properties = Properties()
        properties.addAttribute("isCLickDoctorPrescription", true)
        properties.addAttribute("fileName", fileName)
        MoEAnalyticsHelper.trackEvent(this, "CLickDoctorPrescription", properties)
    }

    override fun onSelectTestReportItem(fileUrl: String?, fileName: String?) {

        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("navType", "Document")
        intent.putExtra("docUrl", fileUrl)
        intent.putExtra("docName", fileName)
        startActivity(intent)

        val properties = Properties()
        properties.addAttribute("isCLickTestReport", true)
        properties.addAttribute("fileName", fileName)
        MoEAnalyticsHelper.trackEvent(this, "CLickCLickTestReport", properties)
    }


}