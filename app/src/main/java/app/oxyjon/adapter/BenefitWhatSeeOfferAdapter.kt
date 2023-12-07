package app.oxyjon.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.R
import app.oxyjon.bean.BenefitResponse
import butterknife.BindView
import butterknife.ButterKnife

class BenefitWhatSeeOfferAdapter constructor(private val context: Activity) :
    RecyclerView.Adapter<BenefitWhatSeeOfferAdapter.ViewHolder>() {
    var items: ArrayList<String> = ArrayList()
    private var listener: OnClickListener? = null

    open interface OnClickListener {
        fun onPlanClick(planType: String, planId: String)
    }

    fun setClickListener(listener: OnClickListener?) {
        this.listener = listener
    }

    fun setData(items: ArrayList<String>) {
        this.items.clear()
        this.items.addAll((items)!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, pos: Int): ViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_benefit_offer, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder, pos: Int,
    ) {

        holder!!.tvOfferName!!.text = items[pos]

    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @JvmField
        @BindView(R.id.tvOfferName)
        var tvOfferName: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}