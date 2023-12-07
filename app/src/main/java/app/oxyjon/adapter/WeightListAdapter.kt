package app.oxyjon.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.R
import butterknife.BindView
import butterknife.ButterKnife


class WeightListAdapter constructor(private val context: Context) :
    RecyclerView.Adapter<WeightListAdapter.StepDetailViewHolder>() {
    var listener: OnClickListener? = null
    var selectedItemPos: Int = -1
    var selectedValue: String = ""

    open interface OnClickListener {
        fun onWeightItemClick(weight: String, pos: Int)
    }

    fun setClickListener(listener: OnClickListener?) {
        this.listener = listener
    }

    fun setSelectedData(selectedValue: String) {
        this.selectedValue = selectedValue
        notifyDataSetChanged()
    }

    public override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): StepDetailViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.row_weight_list, viewGroup, false)
        return StepDetailViewHolder(view)
    }

    public override fun onBindViewHolder(holder: StepDetailViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.tvAge!!.text = "" + (10 + position)
        if (!TextUtils.isEmpty(selectedValue)) {
            if (position == (selectedValue.toInt() - 10)) {
                holder.tvAge!!.setBackgroundResource(R.drawable.bg_blue_circle)
                holder.tvAge!!.setTextColor(ContextCompat.getColor(context, R.color.white))
                selectedValue = ""
            }
        } else {
            if (position == selectedItemPos) {
                holder.tvAge!!.setBackgroundResource(R.drawable.bg_blue_circle)
                holder.tvAge!!.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                holder.tvAge!!.setBackgroundResource(R.drawable.bg_white_circle)
                holder.tvAge!!.setTextColor(ContextCompat.getColor(context, R.color.blueDark))
            }
        }
        holder.tvAge!!.setOnClickListener {
            selectedItemPos = position
            listener!!.onWeightItemClick(holder.tvAge!!.text.toString(), position)
            notifyDataSetChanged()
        }
    }

    inner class StepDetailViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        @JvmField
        @BindView(R.id.tvAge)
        var tvAge: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    public override fun getItemCount(): Int {
        return 141
    }
}