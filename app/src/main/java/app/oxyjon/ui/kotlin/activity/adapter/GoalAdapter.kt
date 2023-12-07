package app.oxyjon.ui.kotlin.activity.adapter

import app.oxyjon.R
import butterknife.BindView
import android.widget.TextView
import butterknife.ButterKnife
import android.app.*
import android.text.Html
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList

class GoalAdapter(private val context: Activity) :
    RecyclerView.Adapter<GoalAdapter.ViewHolder>() {
    private val items = ArrayList<String>()
    var onclick: ClickListener? = null

    interface ClickListener {
        fun onReviewClickrClick(medicineId: Int, pos: Int)
    }

    fun setClickListener(clickListener: ClickListener?) {
        onclick = clickListener
    }

    fun setData(item: ArrayList<String>) {
        items.clear()
        items.addAll(item!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.row_goal_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.tvPosition!!.text = "" + (pos + 1) + "."
        holder.tvValue!!.text = /*Html.fromHtml(items[pos])*/items[pos]
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @kotlin.jvm.JvmField
        @BindView(R.id.tvPosition)
        var tvPosition: TextView? = null

        @kotlin.jvm.JvmField
        @BindView(R.id.tvValue)
        var tvValue: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}