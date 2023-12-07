package app.oxyjon.ui.kotlin.fragment.dashboard.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.oxyjon.R
import app.oxyjon.bean.DashboardResponse
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class PromotionAdapter(val mContext: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<PromotionAdapter.ViewHolder>() {
    private val items: ArrayList<DashboardResponse.Data.PromotionBlock>? = arrayListOf()
    lateinit var onclick: onClickListner

    interface onClickListner {
        fun onSelectPromotionAction(action: String?)
    }

    fun setClickListner(onclick: onClickListner) {
        this.onclick = onclick;
    }

    fun setData(item: ArrayList<DashboardResponse.Data.PromotionBlock>) {
        items!!.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var link: String? = ""
        if (items!![position].image_url != null && !TextUtils.isEmpty(items!![position].image_url)) {
            Glide.with(mContext).load(items!![position].image_url).placeholder(R.drawable.progress_animation).into(holder.imgPromotion!!)
        }

        holder.itemView.setOnClickListener {
            onclick.onSelectPromotionAction(items!![position].image_link)
        }
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_promotion_dashboard,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val imgPromotion: ShapeableImageView? = view.findViewById(R.id.imgPromotion)
    }
}

