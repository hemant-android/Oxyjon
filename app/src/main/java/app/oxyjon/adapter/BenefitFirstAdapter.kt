package app.oxyjon.adapter

import android.app.Activity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.R
import app.oxyjon.bean.BenefitResponse
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide

class BenefitFirstAdapter constructor(private val context: Activity) :
    RecyclerView.Adapter<BenefitFirstAdapter.ViewHolder>() {
    var items: ArrayList<BenefitResponse.Data.Plan> = ArrayList()
    private var listener: OnClickListener? = null

    open interface OnClickListener {
        fun onPlanClick(planType: String, planId: String)
    }

    fun setClickListener(listener: OnClickListener?) {
        this.listener = listener
    }

    fun setData(items: ArrayList<BenefitResponse.Data.Plan>?) {
        this.items.clear()
        this.items.addAll((items)!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, pos: Int): ViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_benefit_new, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder, pos: Int,
    ) {
        if (items[pos].banner_url != null && !TextUtils.isEmpty(items[pos].banner_url)) {
            Glide.with(context).load(items[pos].banner_url)
                .placeholder(R.drawable.progress_animation).into(
                    (holder!!.imgBanner)!!)
        }
        holder!!.tvTitle!!.text = items[pos].plan_name
        holder!!.tvPrice!!.text = items[pos].plan_price

        var adapter = BenefitWhatSeeOfferAdapter(context)
        holder.rvPlanBenefit!!.adapter = adapter

        adapter.setData(items[pos].what_we_offer)


        holder!!.itemView.setOnClickListener {
            listener!!.onPlanClick(items[pos].plan_type,
                items[pos].plan_id)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @JvmField
        @BindView(R.id.tvTitle)
        var tvTitle: TextView? = null

        @JvmField
        @BindView(R.id.tvPrice)
        var tvPrice: TextView? = null

        @JvmField
        @BindView(R.id.imgBanner)
        var imgBanner: ImageView? = null

        @JvmField
        @BindView(R.id.rvPlanBenefit)
        var rvPlanBenefit: RecyclerView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}