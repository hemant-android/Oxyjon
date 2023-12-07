package app.oxyjon.ui.kotlin.activity.adapter

import app.oxyjon.R
import butterknife.BindView
import android.widget.TextView
import butterknife.ButterKnife
import android.app.*
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.bean.MyMeasurementResponse
import kotlin.collections.ArrayList

class MeasurementAdapter(private val context: Activity) :
    RecyclerView.Adapter<MeasurementAdapter.ViewHolder>() {
    private val items = ArrayList<MyMeasurementResponse.Data>()
    var onclick: ClickListener? = null

    interface ClickListener {
        fun onReviewClickrClick(medicineId: Int, pos: Int)
    }

    fun setClickListener(clickListener: ClickListener?) {
        onclick = clickListener
    }

    fun setData(item: ArrayList<MyMeasurementResponse.Data>) {
        items.clear()
        items.addAll(item!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.row_measurement_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.tvType!!.text = items[pos].measurement_unit
        holder.tvValue!!.text = items[pos].measurement_quantity
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @kotlin.jvm.JvmField
        @BindView(R.id.tvType)
        var tvType: TextView? = null

        @kotlin.jvm.JvmField
        @BindView(R.id.tvValue)
        var tvValue: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}