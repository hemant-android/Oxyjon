package app.oxyjon.ui.kotlin.fragment.dashboard.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import app.oxyjon.R
import app.oxyjon.bean.DashboardResponse
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class ActionBoxAdapter(val mContext: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<ActionBoxAdapter.ViewHolder>() {
    private val items: ArrayList<DashboardResponse.Data.ActionBox>? = arrayListOf()
    lateinit var onclick: onClickListner

    interface onClickListner {
        fun onSelectAction(action: String?)
    }

    fun setClickListner(onclick: onClickListner) {
        this.onclick = onclick;
    }

    fun setData(item: ArrayList<DashboardResponse.Data.ActionBox>) {
        items!!.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val param = holder.llAction!!.layoutParams as ViewGroup.MarginLayoutParams
        if (position == 0) {
            param.setMargins(0, 0, 10, 0)
            holder.llAction!!.layoutParams = param
        } else if (position == 1) {
            param.setMargins(10, 0, 0, 0)
            holder.llAction!!.layoutParams = param
        }

        if (items!![position].ui_icons != null && !TextUtils.isEmpty(items!![position].ui_icons)) {
            Glide.with(mContext).load(items!![position].ui_icons)
                .placeholder(R.drawable.progress_animation).into(holder.imgAction!!)
        }

        holder.tvActionName!!.text = items!![position].action_name
        holder.tvActionValue!!.text = items!![position].action_details

        holder.itemView!!.setOnClickListener {
            onclick.onSelectAction("" + items!![position].action_name)
        }
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_action_box_dashboard,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val llAction: LinearLayout? = view.findViewById(R.id.llAction)
        val imgAction: ShapeableImageView? = view.findViewById(R.id.imgAction)
        val tvActionName: TextView? = view.findViewById(R.id.tvActionName)
        val tvActionValue: TextView? = view.findViewById(R.id.tvActionValue)

    }
}

