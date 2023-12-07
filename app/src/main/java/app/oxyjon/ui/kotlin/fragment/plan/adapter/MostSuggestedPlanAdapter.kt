package app.oxyjon.ui.kotlin.fragment.plan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.oxyjon.R
import app.oxyjon.bean.PlanListResponse
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class MostSuggestedPlanAdapter(val mContext: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<MostSuggestedPlanAdapter.ViewHolder>() {
    private val items: ArrayList<PlanListResponse.Seggested>? = arrayListOf()
    lateinit var onclick: onClickListner

    interface onClickListner {
        fun onSelectMostSuggestedPlan(id: Int?, planType: String?)
    }

    fun setClickListner(onclick: onClickListner) {
        this.onclick = onclick;
    }

    fun setData(item: ArrayList<PlanListResponse.Seggested>) {
        items!!.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(mContext).load(items!![position].banner_url)
            .placeholder(R.drawable.progress_animation).into(holder.imgSuggested!!)

        holder.itemView!!.setOnClickListener {
            onclick.onSelectMostSuggestedPlan(items!![position].health_plan_id,
                items!![position].plan_type)
        }
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_suggested_plan,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val imgSuggested: ShapeableImageView? = view.findViewById(R.id.imgSuggested)
    }
}

