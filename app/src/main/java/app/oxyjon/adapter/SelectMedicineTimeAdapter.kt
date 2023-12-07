package app.oxyjon.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.R
import app.oxyjon.bean.MedicineTimingModel
import butterknife.BindView
import butterknife.ButterKnife


class SelectMedicineTimeAdapter constructor(private val context: Context) :
    RecyclerView.Adapter<SelectMedicineTimeAdapter.SelectTimeViewHolder>() {
    private val items: ArrayList<MedicineTimingModel> = ArrayList()
    var onclick: ClickListener? = null

    open interface ClickListener {
        fun onSelectTimeClick(medicineTime: String?, position: Int)
    }

    fun setClickListener(clickListener: ClickListener?) {
        onclick = clickListener
    }

    fun setData(item: ArrayList<MedicineTimingModel>?) {
        items.clear()
        items.addAll((item)!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SelectTimeViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.raw_select_time, viewGroup, false)
        return SelectTimeViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SelectTimeViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        if (items[position].select) {
            holder.tvTime!!.setBackgroundResource(R.drawable.bg_rectangle_fill)
            holder.tvTime!!.setTextColor(context.resources.getColor(R.color.black))
        } else {
            holder.tvTime!!.setBackgroundResource(R.drawable.bg_rectangle_gray)
            holder.tvTime!!.setTextColor(context.resources.getColor(R.color.black))
        }
        holder.tvTime!!.text = items[position].medicineTime

        holder.itemView.setOnClickListener {
            onclick!!.onSelectTimeClick(items[position].medicineTime,
                holder.adapterPosition)
        }
    }

    inner class SelectTimeViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        @JvmField
        @BindView(R.id.tvTime)
        var tvTime: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}