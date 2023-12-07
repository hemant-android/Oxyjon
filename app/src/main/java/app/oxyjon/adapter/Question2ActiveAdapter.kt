package app.oxyjon.adapter

import android.annotation.SuppressLint
import android.content.Context
import app.oxyjon.R
import butterknife.BindView
import android.widget.TextView
import butterknife.ButterKnife
import app.oxyjon.adapter.Question2ActiveAdapter.Question2ViewHolder
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.bean.OnBoardingResponse
import java.util.ArrayList

class Question2ActiveAdapter constructor(
    var clickListener: ClickListener,
    private val context: Context,
    private val items: ArrayList<OnBoardingResponse.Data.OnboardScreenQuest2.Activity>
) : RecyclerView.Adapter<Question2ViewHolder>() {
    private var selectedItemPos: Int = 0

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): Question2ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.raw_question_2_active, viewGroup, false)
        return Question2ViewHolder(view)
    }

     override fun onBindViewHolder(
        holder: Question2ViewHolder,
        @SuppressLint("RecyclerView") position: Int
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