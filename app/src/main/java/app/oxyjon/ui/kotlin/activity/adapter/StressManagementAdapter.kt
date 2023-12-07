package app.oxyjon.ui.kotlin.activity.adapter

import app.oxyjon.R
import android.widget.TextView
import android.app.*
import android.text.Html
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.bean.StressManagementResponse
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import kotlin.collections.ArrayList

class StressManagementAdapter(private val context: Activity) :
    RecyclerView.Adapter<StressManagementAdapter.ViewHolder>() {
    private val items = ArrayList<StressManagementResponse.StressManagemnet>()
    var onclick: ClickListener? = null

    interface ClickListener {
        fun onSelectItem(id: String?, type: String?,url: String?,heading: String?)
    }

    fun setClickListener(clickListener: ClickListener?) {
        onclick = clickListener
    }

    fun setData(item: ArrayList<StressManagementResponse.StressManagemnet>) {
        items.clear()
        items.addAll(item!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.row_stress_management, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (items!![position].contenttype == "video") {
            Glide.with(context).load(items!![position].image_url)
                .centerCrop()
                .placeholder(R.drawable.ic_preview)
                .into(holder.imgContent!!)
        } else {
            Glide.with(context).load(items!![position].image_url)
                .centerCrop()
                .placeholder(R.drawable.ic_preview)
                .into(holder.imgContent!!)
        }

        holder.tvContentTitle!!.text = items!![position].heading
        holder.tvContentDesc!!.text = Html.fromHtml(items!![position].details)

        holder.itemView.setOnClickListener {
            if (items!![position].contenttype == "video") {
                onclick!!.onSelectItem(
                    items!![position].id,
                    items!![position].contenttype,
                    items!![position].video_url,
                    items!![position].heading
                )
            } else {
                onclick!!.onSelectItem(
                    items!![position].id,
                    items!![position].contenttype,
                    items!![position].detail_url,
                    items!![position].heading
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgContent: ShapeableImageView? = view.findViewById(R.id.imgContent)
        val tvContentTitle: TextView? = view.findViewById(R.id.tvContentTitle)
        val tvContentDesc: TextView? = view.findViewById(R.id.tvContentDesc)
    }
}