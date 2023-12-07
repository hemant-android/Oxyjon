package app.oxyjon.ui.kotlin.activity

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
import android.widget.Toast
import app.oxyjon.R
import app.oxyjon.bean.BlogDetailResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.utils.CheckConnection
import app.oxyjon.utils.FunctionHelper
import app.oxyjon.utils.Helper
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import retrofit2.Response


class BlogDetailActivity : BaseActivity(), IApiCallback {
    @JvmField
    @BindView(R.id.tvTitle)
    var tvTitle: TextView? = null

    @JvmField
    @BindView(R.id.webview)
    var webView: WebView? = null

    @JvmField
    @BindView(R.id.imgBack)
    var imgBack: ImageView? = null

    var blogId: String? = ""
    var docUrl: String? = ""
    var docName: String? = ""
    var navType: String? = ""
    var dialog: ProgressDialog? = null

    var preferences: AppSharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview_dashboard)
        ButterKnife.bind(this)

        preferences = AppSharedPreferences.getInstance(this)

        dialog = Helper.initProgress(this@BlogDetailActivity)

        val bundle = intent.extras
        if (bundle != null) {
            navType = bundle.getString("navType")
            blogId = bundle.getString("blogId")
        }

        if (CheckConnection.isConnection(this)) {
            FunctionHelper.disable_user_Intration(this, resources.getString(R.string.loading))
            ApiCall.instance.getBlogDetail(preferences!!.getprofileid(), blogId, this)
        } else {
            Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show()
        }

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

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "blogDetail") {
            val response = data as Response<BlogDetailResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0" && response.body()!!.data?.size!!>0) {
                    docUrl = response.body()!!.data[0].detail_url
                    docName = response.body()!!.data[0].heading

                    if (!TextUtils.isEmpty(docUrl)) {
                        try {
                            if (dialog != null && !dialog!!.isShowing) {
                                dialog!!.show()
                            } else {
                                dialog!!.dismiss()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    tvTitle!!.text = docName

                    webView!!.loadUrl(docUrl!!)

                }
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }
}