package app.oxyjon.ui.kotlin.activity.adapter

import android.app.Activity
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.R
import app.oxyjon.bean.WeightResponse
import butterknife.BindView
import butterknife.ButterKnife


class WeightViewAdapter(private val context: Activity) :
    RecyclerView.Adapter<WeightViewAdapter.ViewHolder>() {
    private val items = ArrayList<WeightResponse.Data>()
    var onclick: ClickListener? = null

    interface ClickListener {
        fun onReviewClickrClick(medicineId: Int, pos: Int)
    }

    fun setClickListener(clickListener: ClickListener?) {
        onclick = clickListener
    }

    fun setData(item: ArrayList<WeightResponse.Data>?) {
        items.clear()
        items.addAll(item!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.row_weight_view, viewGroup, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.tvType!!.text = items[pos].weight
        holder.tvValue!!.text = items[pos].unit
        try {
            val strDate = items[pos].date
            //current date format
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val objDate = dateFormat.parse(strDate)
            //Expected date format
            val dateFormat2 = SimpleDateFormat("dd MMM yyyy")
            val finalDate = dateFormat2.format(objDate)
            holder.tvDateTime!!.text = finalDate
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

        @kotlin.jvm.JvmField
        @BindView(R.id.tvDateTime)
        var tvDateTime: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}