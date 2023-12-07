package app.oxyjon.ui.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.webkit.*
import android.widget.ImageView
import android.widget.TextView
import app.oxyjon.R
import app.oxyjon.utils.Helper
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick


class WebViewActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.tvTitle)
    var tvTitle: TextView? = null

    @JvmField
    @BindView(R.id.webview)
    var webView: WebView? = null

    @JvmField
    @BindView(R.id.imgBack)
    var imgBack: ImageView? = null
    var docUrl: String? = ""
    var docName: String? = ""
    var navType: String? = ""
    var dialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview_dashboard)
        ButterKnife.bind(this)
        dialog = Helper.initProgress(this)
        val bundle = intent.extras
        if (bundle != null) {
            navType = bundle.getString("navType")
            docUrl = bundle.getString("docUrl")
            docName = bundle.getString("docName")
        }
        if (!TextUtils.isEmpty(docUrl)) {
            if (dialog != null && !dialog!!.isShowing) {
                dialog!!.show()
            } else {
                dialog!!.dismiss()
            }
        }
        tvTitle!!.text = docName
        val webSettings = webView!!.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView!!.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        webView!!.isScrollbarFadingEnabled = true
        webView!!.webViewClient = MyBrowser()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView!!.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            webView!!.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        webView!!.loadUrl(docUrl!!)
    }

    @OnClick(R.id.imgBack)
    fun onBack() {
        if (webView!!.canGoBack()) {
            webView!!.goBack()
        } else {
            super.onBackPressed()
        }
        try {
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        if (webView!!.canGoBack()) {
            webView!!.goBack()
        } else {
            super.onBackPressed()
        }
        try {
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private inner class MyBrowser : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            if (view.title == "") {
                view.reload()
            } else {
                try {
                    if (dialog != null && dialog!!.isShowing) {
                        dialog!!.dismiss()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return if (navType.equals("notificationList",
                    ignoreCase = true) || navType.equals("newsFeedList", ignoreCase = true)
            ) {
                if (url.contains("whatsapp://")) {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    true
                } else {
                    webView!!.loadUrl(url)
                    false
                }
            } else {
                if (url.contains("whatsapp://")) {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    true
                } else {
                    webView!!.loadUrl(url)
                    false
                }
            }
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            try {
                if (dialog != null && !dialog!!.isShowing) {
                    dialog!!.show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            super.onReceivedError(view, request, error)
            try {
                if (dialog != null && dialog!!.isShowing) {
                    dialog!!.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}