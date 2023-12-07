package app.oxyjon.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.R
import app.oxyjon.bean.DoctorConsultationResponse
import app.oxyjon.bean.PlanDetailResponse
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide


class ReviewDoctorAdapter constructor(private val context: Activity) :
    RecyclerView.Adapter<ReviewDoctorAdapter.ViewHolder>() {
    private val width: Int
    private val height: Int
    var items: ArrayList<DoctorConsultationResponse.Data.Review> = ArrayList()
    private var listener: IRecyclerClickListener? = null

    open interface IRecyclerClickListener {
        fun onPlanClick(data: PlanDetailResponse.Data.Benefit?)
    }

    fun setClickListener(listener: IRecyclerClickListener?) {
        this.listener = listener
    }

    init {
        val displayMetrics = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
        height = displayMetrics.heightPixels
    }

    fun setData(items: ArrayList<DoctorConsultationResponse.Data.Review>?) {
        this.items.clear()
        this.items.addAll((items)!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, pos: Int): ViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_review, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        @SuppressLint("RecyclerView") pos: Int,
    ) {
        val mWidth: Int = (width / 1.4).toInt()
        holder.itemView.layoutParams = RecyclerView.LayoutParams(mWidth,
            RecyclerView.LayoutParams.WRAP_CONTENT)
        if (items[pos].image_url != null && !TextUtils.isEmpty(items[pos].image_url)) {
            holder.imgProfile!!.visibility = View.VISIBLE
            Glide.with(context).load(items[pos].image_url)
                .placeholder(R.drawable.progress_animation).centerCrop().into(
                    (holder.imgProfile)!!)
        } else {
            holder.imgProfile!!.visibility = View.GONE
        }
        holder.tvTitle!!.text = items[pos].user_name
        holder.tvDesc!!.text = items[pos].review
        if (items[pos].review_star != null && !TextUtils.isEmpty(items[pos].review_star)) {
            holder.ratingBar!!.rating = items[pos].review_star.toFloat()
        } else {
            holder.ratingBar!!.rating = "0".toFloat()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @JvmField
        @BindView(R.id.tvTitle)
        var tvTitle: TextView? = null

        @JvmField
        @BindView(R.id.tvDesc)
        var tvDesc: TextView? = null

        @JvmField
        @BindView(R.id.imgProfile)
        var imgProfile: ImageView? = null

        @JvmField
        @BindView(R.id.ratingBar)
        var ratingBar: AppCompatRatingBar? = null

        @JvmField
        @BindView(R.id.cardView)
        var cardView: CardView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}