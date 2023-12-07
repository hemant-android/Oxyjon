package app.oxyjon.ui.kotlin.fragment.dashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.oxyjon.R
import app.oxyjon.bean.DashboardResponse
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class TopBannerAdapter(val mContext: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<TopBannerAdapter.ViewHolder>() {
    private val items: ArrayList<DashboardResponse.Data.TopBanner>? = arrayListOf()
    lateinit var onclick: onClickListner

    interface onClickListner {
        fun onSelectTopBanner(planId: String?, type: String?, imageLink: String?)
    }

    fun setClickListner(onclick: onClickListner) {
        this.onclick = onclick;
    }

    fun setData(item: ArrayList<DashboardResponse.Data.TopBanner>) {
        items!!.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(mContext).load(items!![position].image_url)
            .placeholder(R.drawable.progress_animation).into(holder.imgBanner!!)

        holder.itemView.setOnClickListener {
            onclick.onSelectTopBanner(items!![position].plan_id, items!![position].banner_type, items!![position].image_link)
        }
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_top_banner,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val imgBanner: ShapeableImageView? = view.findViewById(R.id.imgBanner)
    }
}

