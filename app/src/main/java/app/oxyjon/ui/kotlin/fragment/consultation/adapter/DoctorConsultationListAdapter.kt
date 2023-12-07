package app.oxyjon.ui.kotlin.fragment.consultation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import app.oxyjon.R
import app.oxyjon.ui.kotlin.fragment.dashboard.adapter.ActionBoxAdapter

class DoctorConsultationListAdapter(val mContext: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<DoctorConsultationListAdapter.ViewHolder>() {
    private val items: ArrayList<String>? = arrayListOf()
    lateinit var onclick: onClickListner

    interface onClickListner {
        fun onSelectDoctor()
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
        holder.itemView!!.setOnClickListener {
            onclick.onSelectDoctor()
        }
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_doctor_consultation,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return 8
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val llMain: LinearLayout? = view.findViewById(R.id.llMain)
        val tvDrName: TextView? = view.findViewById(R.id.tvDrName)
        val tvDrSpecialist: TextView? = view.findViewById(R.id.tvDrSpecialist)
        val ratingBar: AppCompatRatingBar? = view.findViewById(R.id.ratingBar)
    }
}

