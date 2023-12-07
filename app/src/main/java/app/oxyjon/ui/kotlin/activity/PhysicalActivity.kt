package app.oxyjon.ui.kotlin.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ExpandableListView.OnGroupExpandListener
import android.widget.ImageView
import android.widget.Toast
import app.oxyjon.R
import app.oxyjon.bean.PhysicalActivityResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityPhysicalBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.ui.kotlin.activity.adapter.ExpandableExercisePlanAdapter
import app.oxyjon.utils.CheckConnection.isConnection
import app.oxyjon.utils.FunctionHelper
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import retrofit2.Response


class PhysicalActivity : BaseActivity(), IApiCallback {
    lateinit var binding: ActivityPhysicalBinding
    var preferences: AppSharedPreferences? = null

    private var popularCompetitionAdapter: ExpandableExercisePlanAdapter? = null
    private var arrExercisePlan: ArrayList<PhysicalActivityResponse.Data>? = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhysicalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppSharedPreferences.getInstance(this)


        binding.imgBack.setOnClickListener {
            finish()
        }

        val prevExpandPosition = intArrayOf(-1)
        binding.expendablePhysical!!.setOnGroupExpandListener { groupPosition ->
            if (prevExpandPosition[0] >= 0 && prevExpandPosition[0] != groupPosition) {
                binding.expendablePhysical!!.collapseGroup(prevExpandPosition[0])
            }
            prevExpandPosition[0] = groupPosition
        }


        binding.expendablePhysical!!.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            var blogId =
                (arrExercisePlan as ArrayList<PhysicalActivityResponse.Data>)[groupPosition]!!.activity_data[childPosition].id
            var type =
                (arrExercisePlan as ArrayList<PhysicalActivityResponse.Data>)[groupPosition]!!.activity_data[childPosition].content_type
            var url =
                (arrExercisePlan as ArrayList<PhysicalActivityResponse.Data>)[groupPosition]!!.activity_data[childPosition].activity_video_url

            if (type == "video") {
                if (url!!.contains("v=")) {
                    dialogForYoutubePlayer(url)
                } else {
                    Toast.makeText(
                        this@PhysicalActivity,
                        "Youtube url not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val intent = Intent(this, BlogDetailActivity::class.java)
                intent.putExtra("navType", type)
                intent.putExtra("blogId", blogId)
                startActivity(intent)
            }
            false
        }

        if (isConnection(this)) {
            FunctionHelper.disable_user_Intration(this, resources.getString(R.string.loading))
            ApiCall.instance.getPhysicalActivityDetail(this)
        } else {
            Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "PhysicalActivityResponse") {
            val response = data as Response<PhysicalActivityResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "0") {

                    if (arrExercisePlan != null && arrExercisePlan!!.size > 0) {
                        arrExercisePlan!!.clear()
                    }

                    if (!TextUtils.isEmpty(response.body()!!.activity_remarks)) {
                        binding.llRemark.visibility = View.VISIBLE
                        binding.tvRemark.text = response.body()!!.activity_remarks
                    } else {
                        binding.llRemark.visibility = View.GONE
                    }
                    if (response.body()!!.data?.size!! > 0) {

                        binding.tvNoRecordFound.visibility = View.GONE

                        arrExercisePlan = response.body()!!.data

                        popularCompetitionAdapter = ExpandableExercisePlanAdapter(
                            this,
                            arrExercisePlan!!
                        )
                        binding.expendablePhysical.setAdapter(popularCompetitionAdapter)

                        binding.expendablePhysical.expandGroup(0)
                    } else {
                        binding.tvNoRecordFound.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
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

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
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