package app.oxyjon.ui.kotlin.fragment.plan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import app.oxyjon.R
import app.oxyjon.bean.PlanListResponse

class SugarReductionPlanAdapter(val mContext: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<SugarReductionPlanAdapter.ViewHolder>() {
    private val items: ArrayList<PlanListResponse.Data>? = arrayListOf()
    lateinit var onclick: onClickListner

    interface onClickListner {
        fun onSelectSugarPlan(id: Int?, planType: String?)
    }

    fun setClickListner(onclick: onClickListner) {
        this.onclick = onclick;
    }

    fun setData(item: ArrayList<PlanListResponse.Data>) {
        items!!.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tvPrice!!.text = "₹" + items!![position].price
        holder.tvTitle!!.text = items!![position].plan_title
        holder.tvDesc!!.text = items!![position].plan_details

        if (items!![position].plan_duration == 90) {
            holder.tvMostlySuggested!!.visibility = View.GONE
        } else {
            holder.tvMostlySuggested!!.visibility = View.GONE
        }

        if (items!![position].plan_type == "doctor_consultation" || items!![position].plan_type == "educator_consultation") {
            holder.tvDuration!!.text = items!![position].plan_duration.toString() + " time"
        } else {
            holder.tvDuration!!.text = "" + (items!![position].plan_duration / 30) + " Month"
        }


        holder.itemView!!.setOnClickListener {
            onclick.onSelectSugarPlan(items!![position].health_plan_id, items!![position].plan_type)
        }
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_plan_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val llMain: RelativeLayout? = view.findViewById(R.id.llMain)
        val tvPrice: TextView? = view.findViewById(R.id.tvPrice)
        val tvTitle: TextView? = view.findViewById(R.id.tvTitle)
        val tvDesc: TextView? = view.findViewById(R.id.tvDesc)
        val tvDuration: TextView? = view.findViewById(R.id.tvDuration)
        val tvMostlySuggested: TextView? = view.findViewById(R.id.tvMostlySuggested)
    }
}

