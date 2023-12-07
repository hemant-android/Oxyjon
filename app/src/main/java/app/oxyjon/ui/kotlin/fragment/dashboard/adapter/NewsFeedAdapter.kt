package app.oxyjon.ui.kotlin.fragment.dashboard.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.oxyjon.R
import app.oxyjon.bean.DashboardResponse
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class NewsFeedAdapter(val mContext: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<NewsFeedAdapter.ViewHolder>() {
    private val items: ArrayList<DashboardResponse.Data.Newsfeed>? = arrayListOf()
    lateinit var onclick: onClickListner

    interface onClickListner {
        fun onSelectBlogAction(id: String?, type: String?,url: String?,heading: String?)
    }

    fun setClickListner(onclick: onClickListner) {
        this.onclick = onclick;
    }

    fun setData(item: ArrayList<DashboardResponse.Data.Newsfeed>) {
        items!!.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(mContext).load(items!![position].url).placeholder(R.drawable.dr)
            .into(holder.imgContent!!)

        holder.tvContentTitle!!.text = items!![position].heading
        holder.tvContentDesc!!.text = Html.fromHtml(items!![position].details)

        holder.itemView.setOnClickListener {
            onclick.onSelectBlogAction(items!![position].id, items!![position].contenttype,items!![position].detail_url,items!![position].heading)
        }
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_news_feed,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val tvContentTitle: TextView? = view.findViewById(R.id.tvContentTitle)
        val tvContentDesc: TextView? = view.findViewById(R.id.tvContentDesc)
        val imgContent: ShapeableImageView? = view.findViewById(R.id.imgContent)

    }
}

