package app.oxyjon.ui.kotlin.activity

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.databinding.ActivityStaticWebPageBinding
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.utils.FunctionHelper

class StaticWebPageActivity : BaseActivity() {
    lateinit var binding: ActivityStaticWebPageBinding

    private var url: String? = ""
    private var title: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaticWebPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MainApplication.currentActivity = this

        binding.imgBack.setOnClickListener {
            finish()
        }

        FunctionHelper.disable_user_Intration(this, getString(R.string.loading))

        if (intent?.extras != null) {
            val bundle: Bundle? = intent.extras
            if (bundle != null) {
                url = bundle.getString("url") ?: ""
                title = bundle.getString("title") ?: ""
            }
        }

        binding.tvTitle.text = title

        binding.webview.settings.javaScriptEnabled = true
        binding.webview.loadUrl(url!!)
        binding.webview.webViewClient = Mybrowser()
    }

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this
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