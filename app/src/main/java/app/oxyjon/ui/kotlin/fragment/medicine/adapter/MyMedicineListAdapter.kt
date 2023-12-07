package app.oxyjon.ui.kotlin.fragment.medicine.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.R
import app.oxyjon.bean.MyMedicineResponse
import butterknife.BindView
import butterknife.ButterKnife

class MyMedicineListAdapter(private val context: Context) :
    RecyclerView.Adapter<MyMedicineListAdapter.SelectTimeViewHolder>() {
    private val items = ArrayList<MyMedicineResponse.Data.Medicineitem>()
    var onclick: ClickListener? = null

    interface ClickListener {
        fun onSelectMedicineClick(medicineId: Int, pos: Int)
    }

    fun setClickListener(clickListener: ClickListener?) {
        onclick = clickListener
    }

    fun setData(item: ArrayList<MyMedicineResponse.Data.Medicineitem>?) {
        items.clear()
        items.addAll(item!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int,
    ): SelectTimeViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.raw_my_medicine_list, viewGroup, false)
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
        holder.imgClose!!.setOnClickListener {
            onclick!!.onSelectMedicineClick(items[position].id, position)
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size)
        }
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