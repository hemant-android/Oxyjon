package app.oxyjon.ui.kotlin.activity.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.R
import app.oxyjon.bean.GetProfileResponse
import butterknife.BindView
import butterknife.ButterKnife

class PhysicalActiveAdapter constructor(
    var clickListener: ClickListener,
    private val context: Context,
    private val items: ArrayList<GetProfileResponse.Data.Activity>,
    private val activityScore: String,
) : RecyclerView.Adapter<PhysicalActiveAdapter.Question2ViewHolder>() {
    private var selectedItemPos: Int = 0

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): Question2ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.raw_question_2_active, viewGroup, false)
        return Question2ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: Question2ViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        holder.tvTime!!.text = items[position].value
        if (items[position].selectedOption) {
            holder.tvTime!!.setBackgroundResource(R.drawable.bg_rectangle_fill)
            holder.tvTime!!.setTextColor(context.resources.getColor(R.color.black))
        } else {
            holder.tvTime!!.setBackgroundResource(R.drawable.bg_rectangle_gray)
            holder.tvTime!!.setTextColor(context.resources.getColor(R.color.black))
        }
        holder.itemView.setOnClickListener {
            clickListener.onRecyclerItemClick(items[position].key)
            selectClickedAndUnselectPrevious(holder)
        }
    }

    inner class Question2ViewHolder constructor(itemView: View) :
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

    open interface ClickListener {
        fun onRecyclerItemClick(pos: String)
    }

    private fun selectClickedAndUnselectPrevious(holder: Question2ViewHolder) {
        items[selectedItemPos].selectedOption = false
        items[holder.adapterPosition].selectedOption = true
        notifyItemChanged(selectedItemPos)
        notifyItemChanged(holder.adapterPosition)
        selectedItemPos = holder.adapterPosition
    }
}