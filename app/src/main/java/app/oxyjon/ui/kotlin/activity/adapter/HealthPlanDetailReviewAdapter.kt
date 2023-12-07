package app.oxyjon.ui.kotlin.activity.adapter

import app.oxyjon.R
import butterknife.BindView
import butterknife.ButterKnife
import android.util.DisplayMetrics
import com.bumptech.glide.Glide
import androidx.cardview.widget.CardView
import android.app.*
import app.oxyjon.bean.HealthPlanDetailResponse
import android.text.*
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class HealthPlanDetailReviewAdapter(private val context: Activity) :
    RecyclerView.Adapter<HealthPlanDetailReviewAdapter.ViewHolder>() {
    private val items = ArrayList<HealthPlanDetailResponse.HealthplanDetails.Review>()
    var onclick: ClickListener? = null
    private val width: Int

    interface ClickListener {
        fun onReviewClickrClick(medicineId: Int, pos: Int)
    }

    fun setClickListener(clickListener: ClickListener?) {
        onclick = clickListener
    }

    init {
        val displayMetrics = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
    }

    fun setData(item: ArrayList<HealthPlanDetailResponse.HealthplanDetails.Review>?) {
        items.clear()
        items.addAll(item!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_review, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val mWidth = (width / 1.4).toInt()
        holder.itemView.layoutParams = RecyclerView.LayoutParams(mWidth,
            RecyclerView.LayoutParams.WRAP_CONTENT)
        if (items[pos].image_url != null && !TextUtils.isEmpty(items[pos].image_url)) {
            holder.imgProfile!!.visibility = View.VISIBLE
            Glide.with(context).load(items[pos].image_url)
                .placeholder(R.drawable.progress_animation).centerCrop().into(
                holder.imgProfile!!)
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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @kotlin.jvm.JvmField
        @BindView(R.id.tvTitle)
        var tvTitle: TextView? = null

        @kotlin.jvm.JvmField
        @BindView(R.id.tvDesc)
        var tvDesc: TextView? = null

        @kotlin.jvm.JvmField
        @BindView(R.id.imgProfile)
        var imgProfile: ImageView? = null

        @kotlin.jvm.JvmField
        @BindView(R.id.ratingBar)
        var ratingBar: AppCompatRatingBar? = null

        @kotlin.jvm.JvmField
        @BindView(R.id.cardView)
        var cardView: CardView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}