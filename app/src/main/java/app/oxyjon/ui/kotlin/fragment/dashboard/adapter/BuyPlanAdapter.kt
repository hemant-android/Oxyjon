package app.oxyjon.ui.kotlin.fragment.dashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import app.oxyjon.R
import app.oxyjon.bean.DashboardResponse

class BuyPlanAdapter(val mContext: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<BuyPlanAdapter.ViewHolder>() {
    private val items: ArrayList<DashboardResponse.Data.Healthplan>? = arrayListOf()
    lateinit var onclick: onClickListner

    interface onClickListner {
        fun onSelectPlanAction(action: Int?, type: String?)
    }

    fun setClickListner(onclick: onClickListner) {
        this.onclick = onclick;
    }

    fun setData(item: ArrayList<DashboardResponse.Data.Healthplan>) {
        items!!.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvHealthPlan!!.text = items!![position].healthplan_name
        if (items!![position].plan_type == "doctor_consultation" || items!![position].plan_type == "educator_consultation") {
            holder.tvHealthPlanPrice!!.text = "₹" + items!![position].healthplan_s_price + " Only"
        } else {
            holder.tvHealthPlanPrice!!.text =
                "₹" + items!![position].healthplan_s_price + " for " + (items!![position].healthplan_active_no_of_days / 30) + " Month"
        }


        holder.itemView.setOnClickListener {
            onclick.onSelectPlanAction(items!![position].id, items!![position].plan_type)
        }

    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_buy_plan,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val tvHealthPlan: TextView? = view.findViewById(R.id.tvHealthPlan)
        val tvHealthPlanPrice: TextView? = view.findViewById(R.id.tvHealthPlanPrice)
        val rlBuyNow: RelativeLayout? = view.findViewById(R.id.rlBuyNow)

    }
}

