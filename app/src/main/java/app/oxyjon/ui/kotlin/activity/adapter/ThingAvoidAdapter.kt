package app.oxyjon.ui.kotlin.activity.adapter

import app.oxyjon.R
import android.widget.TextView
import android.app.*
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.bean.ThingsToDoAvoidResponse
import kotlin.collections.ArrayList

class ThingAvoidAdapter(private val context: Activity) :
    RecyclerView.Adapter<ThingAvoidAdapter.ViewHolder>() {
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
            LayoutInflater.from(context).inflate(R.layout.item_things_do_avoid, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvPosition!!.text = ""+(position+1)+"."
        holder.tvName!!.text = items[position]
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPosition: TextView? = view.findViewById(R.id.tvPosition)
        val tvName: TextView? = view.findViewById(R.id.tvName)
    }
}