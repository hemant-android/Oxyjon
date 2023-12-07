package app.oxyjon.ui.kotlin.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import app.oxyjon.R
import app.oxyjon.bean.StressManagementResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityStressManagementBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.ui.kotlin.activity.adapter.StressManagementAdapter
import app.oxyjon.utils.CheckConnection.isConnection
import app.oxyjon.utils.FunctionHelper
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import retrofit2.Response

class StressManagementActivity : BaseActivity(), IApiCallback,
    StressManagementAdapter.ClickListener {
    lateinit var binding: ActivityStressManagementBinding
    var preferences: AppSharedPreferences? = null

    private val mAdapter: StressManagementAdapter by lazy { StressManagementAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStressManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppSharedPreferences.getInstance(this)

        binding.rvStressManagement.adapter = mAdapter
        mAdapter.setClickListener(this)

        binding.imgBack.setOnClickListener {
            finish()
        }

        if (isConnection(this)) {
            FunctionHelper.disable_user_Intration(this, resources.getString(R.string.loading))
            ApiCall.instance.geStressManagementDetail(this)
        } else {
            Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "StressManagementResponse") {
            val response = data as Response<StressManagementResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {
                    if (response.body()!!.stress_managemnet != null && response.body()!!.stress_managemnet?.size!! > 0) {

                        mAdapter.setData(response.body()!!.stress_managemnet!!)
                    }
                }
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }

    override fun onSelectItem(id: String?, type: String?, url: String?, heading: String?) {
        if (type == "video") {
            if (url!!.contains("v=")) {
                dialogForYoutubePlayer(url)
            }else{
                Toast.makeText(this@StressManagementActivity,"Youtube url not found",Toast.LENGTH_SHORT).show()
            }
        } else {
            val intent = Intent(this, BlogDetailActivity::class.java)
            intent.putExtra("navType", type)
            intent.putExtra("blogId", id)
            startActivity(intent)
        }

        val properties = Properties()
        properties.addAttribute("Name", heading)
        properties.addAttribute("id", id)
        properties.addAttribute("type", type)
        MoEAnalyticsHelper.trackEvent(this, "clickStressType", properties)
    }

    private fun dialogForYoutubePlayer(url: String?) {
        var dialog = Dialog(this, R.style.DialogSlideAnim)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        val window: Window? = dialog.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        window.attributes = wlp

        dialog.setContentView(R.layout.dialog_youtube_player)

        val youTubePlayerView = dialog.findViewById(R.id.videoPlayer) as YouTubePlayerView
        val imgClose = dialog.findViewById(R.id.imgClose) as ImageView

        imgClose.setOnClickListener {
            if (dialog != null && dialog.isShowing) {
                dialog.dismiss()
            }
        }

        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.getPlayerUiController()

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                // loading the selected video into the YouTube Player
                if (url!!.contains("v=")) {
                    youTubePlayer.loadVideo(url!!.split("v=")[1], 0F)
                }
            }

            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                // this method is called if video has ended,
                super.onStateChange(youTubePlayer, state)
            }
        })

        if (dialog != null && dialog.isShowing) {
            dialog.dismiss()
        } else {
            dialog.show()
        }

    }
}