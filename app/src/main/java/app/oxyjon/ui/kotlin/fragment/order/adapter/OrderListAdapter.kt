package app.oxyjon.ui.kotlin.fragment.order.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.oxyjon.R

class OrderListAdapter(val mContext: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {
    private val items: ArrayList<String>? = arrayListOf()
    lateinit var onclick: onClickListner

    interface onClickListner {
        fun onSelectFileFolderOption(position: Int, type: String)
    }

    fun setClickListner(onclick: onClickListner) {
        this.onclick = onclick;
    }

    fun setData(item: ArrayList<String>) {
        items!!.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_order_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return 4
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val tvStoreName: TextView? = view.findViewById(R.id.tvStoreName)
        val tvStoreAddress: TextView? = view.findViewById(R.id.tvStoreAddress)
    }


}

