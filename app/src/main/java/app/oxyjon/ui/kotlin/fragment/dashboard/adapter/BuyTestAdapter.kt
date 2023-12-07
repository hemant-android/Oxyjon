package app.oxyjon.ui.kotlin.fragment.dashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import app.oxyjon.R
import app.oxyjon.bean.DashboardResponse

class BuyTestAdapter(val mContext: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<BuyTestAdapter.ViewHolder>() {
    private val items: ArrayList<DashboardResponse.Data.Bloodtestplan>? = arrayListOf()
    lateinit var onclick: onClickListner

    interface onClickListner {
        fun onSelectTestAction(action: Int?)
    }

    fun setClickListner(onclick: onClickListner) {
        this.onclick = onclick;
    }

    fun setData(item: ArrayList<DashboardResponse.Data.Bloodtestplan>) {
        items!!.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvHealthPlan!!.text = items!![position].bloodtestname
        holder.tvHealthPlanPrice!!.text = "₹"+items!![position].s_price

        holder.itemView.setOnClickListener {
            onclick.onSelectTestAction(items!![position].id)
        }

    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_buy_test,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val rlBuyNow: RelativeLayout? = view.findViewById(R.id.rlBuyNow)
        val tvHealthPlan: TextView? = view.findViewById(R.id.tvHealthPlan)
        val tvHealthPlanPrice: TextView? = view.findViewById(R.id.tvHealthPlanPrice)

    }
}

