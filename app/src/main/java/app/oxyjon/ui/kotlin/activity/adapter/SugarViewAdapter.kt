package app.oxyjon.ui.kotlin.activity.adapter

import app.oxyjon.R
import butterknife.BindView
import android.widget.TextView
import butterknife.ButterKnife
import android.app.*
import app.oxyjon.bean.SugarDetailResponse
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.ArrayList

class SugarViewAdapter(private val context: Activity) :
    RecyclerView.Adapter<SugarViewAdapter.ViewHolder>() {
    private val items = ArrayList<SugarDetailResponse.Data>()
    var onclick: ClickListener? = null

    interface ClickListener {
        fun onReviewClickrClick(medicineId: Int, pos: Int)
    }

    fun setClickListener(clickListener: ClickListener?) {
        onclick = clickListener
    }

    fun setData(item: ArrayList<SugarDetailResponse.Data>?) {
        items.clear()
        items.addAll(item!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.row_sugar_view, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.tvValue!!.text = items[pos].sugar_level

        when (items[pos].type) {
            "PP" -> {
                holder.tvType!!.text = "Post- Breakfast"
            }

            "Fasting" -> {
                holder.tvType!!.text = "Fasting"
            }

            "BeforeLunch" -> {
                holder.tvType!!.text = "Pre- Lunch"
            }

            "AfterLunch" -> {
                holder.tvType!!.text = "Post Lunch"
            }

            "BeforeDinner" -> {
                holder.tvType!!.text = "Pre Dinner"
            }

            "AfterDinner" -> {
                holder.tvType!!.text = "Post Dinner"
            }

            "Midnight" -> {
                holder.tvType!!.text = "3 AM"
            }

            "Random" -> {
                holder.tvType!!.text = "Random"
            }

            else -> {
                holder.tvType!!.text = items[pos].type
            }
        }
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