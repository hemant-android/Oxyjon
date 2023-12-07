package app.oxyjon.ui.kotlin.activity.adapter

import android.content.Context
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import app.oxyjon.R
import butterknife.BindView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.bean.MyMedicineListResponse
import butterknife.ButterKnife
import java.util.ArrayList

class MedicineListAdapter(private val context: Context) :
    RecyclerView.Adapter<MedicineListAdapter.SelectTimeViewHolder>() {
    private val items = ArrayList<MyMedicineListResponse.Data>()
    var onclick: ClickListener? = null

    interface ClickListener {
        fun onSelectMedicineClick(position: Int)
    }

    fun setClickListener(clickListener: ClickListener?) {
        onclick = clickListener
    }

    fun setData(item: ArrayList<MyMedicineListResponse.Data>?) {
        items.clear()
        items.addAll(item!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): SelectTimeViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.raw_get_medicine_list, viewGroup, false)
        return SelectTimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: SelectTimeViewHolder, position: Int) {
        if (items[position].medicine != null) {
            holder.tvMedicineName!!.text = items[position].medicine
        } else {
            holder.tvMedicineName!!.visibility = View.GONE
        }
        if (items[position].dose != null) {
            holder.tvMedicineDose!!.text = items[position].dose
        } else {
            holder.tvMedicineDose!!.visibility = View.GONE
        }
        if (items[position].time_slot != null) {
            val replaceString = items[position].time_slot.replace(",", ", ")
            holder.tvMedicineTimeSlot!!.text = replaceString
        } else {
            holder.tvMedicineTimeSlot!!.visibility = View.GONE
        }
        holder.imgClose!!.setOnClickListener { onclick!!.onSelectMedicineClick(holder.adapterPosition) }
    }

    inner class SelectTimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @JvmField
        @BindView(R.id.tvMedicineName)
        var tvMedicineName: TextView? = null

        @JvmField
        @BindView(R.id.tvMedicineDose)
        var tvMedicineDose: TextView? = null

        @JvmField
        @BindView(R.id.tvMedicineTimeSlot)
        var tvMedicineTimeSlot: TextView? = null

        @JvmField
        @BindView(R.id.imgClose)
        var imgClose: ImageView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}