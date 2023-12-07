package app.oxyjon.ui.kotlin.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import app.oxyjon.R
import app.oxyjon.bean.HealthPlanDetailResponse

class HealthPlanDetailConsultationAdapter(val mContext: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<HealthPlanDetailConsultationAdapter.ViewHolder>() {
    private val items: ArrayList<HealthPlanDetailResponse.HealthplanDetails.Consultation>? =
        arrayListOf()
    lateinit var onclick: onClickListner

    interface onClickListner {
        fun onSelectDoctor()
    }

    fun setClickListner(onclick: onClickListner) {
        this.onclick = onclick;
    }

    fun setData(item: ArrayList<HealthPlanDetailResponse.HealthplanDetails.Consultation>) {
        items!!.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTitle!!.text = items!![position].name
        holder.tvCount!!.text = items!![position].count

        if (items!![position].type == "video") {
            holder.imgVideoAudioConsult!!.setImageResource(R.drawable.ic_video_consult)
        } else {
            holder.imgVideoAudioConsult!!.setImageResource(R.drawable.ic_audio_consult)
        }
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_health_plan_detail_consultation,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val imgVideoAudioConsult: ImageView? = view.findViewById(R.id.imgVideoAudioConsult)
        val tvCount: TextView? = view.findViewById(R.id.tvCount)
        val tvTitle: TextView? = view.findViewById(R.id.tvTitle)
    }
}

