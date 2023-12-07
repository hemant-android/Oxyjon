package app.oxyjon.ui.kotlin.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivitySummeryBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.retrofit.response.SummaryResponce
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.utils.CheckConnection
import app.oxyjon.utils.FunctionHelper
import retrofit2.Response

class SummeryActivity : BaseActivity(), IApiCallback {
    lateinit var binding: ActivitySummeryBinding
    var preferences: AppSharedPreferences? = null

    private var link: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummeryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppSharedPreferences.getInstance(this)

        MainApplication.currentActivity = this

        binding.imgBack.setOnClickListener {
            finish()
        }

        getSummery()

        binding.webview.settings.javaScriptEnabled = true
        binding.webview.webViewClient = Mybrowser()
        val webSettings: WebSettings = binding.webview.settings
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = false
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        binding.webview.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        binding.webview.isScrollbarFadingEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            binding.webview.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            binding.webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
    }

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        if (type == "summary") {
            val response = data as Response<SummaryResponce>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0" && response.body()!!.data != null) {
                    //                            link = "https://oxyjon.com/api/public/health-summary?pkey=" + response.body().getData().get(0).getPkey();
                    link = response.body()!!.data?.get(0)!!.profileLink
                }

                if (link!!.contains("//")) {
                    link = link!!.replace("//".toRegex(), "/")
                    link = link!!.replace(":/", "://")
                }

                binding.webview.loadUrl(link!!)
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }

    private fun getSummery() {
        if (CheckConnection.isConnection(this)) {
            FunctionHelper.disable_user_Intration(this,
                resources.getString(R.string.loading))
            ApiCall.instance.gethealthSummary(preferences!!.getprofileid()!!, this)
        } else {
            Toast.makeText(this, "please check your internet connection", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private class Mybrowser : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: String): Boolean {
            view.loadUrl(request)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            FunctionHelper.enableUserIntraction()
        }
    }
}