package app.oxyjon.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.R
import app.oxyjon.bean.GetMostSearchFoodItemResponse
import butterknife.BindView
import butterknife.ButterKnife


class MostSearchFoodItemAdapter constructor(private val context: Context) :
    RecyclerView.Adapter<MostSearchFoodItemAdapter.SelectTimeViewHolder>() {
    private val items: ArrayList<GetMostSearchFoodItemResponse.Datum> = ArrayList()
    var onclick: ClickListener? = null

    open interface ClickListener {
        fun onSelectMostSearchFoodItemClick(datum: GetMostSearchFoodItemResponse.Datum?, pos: Int)
    }

    fun setClickListener(clickListener: ClickListener?) {
        onclick = clickListener
    }

    fun setData(item: ArrayList<GetMostSearchFoodItemResponse.Datum>?) {
        items.clear()
        items.addAll((item)!!)
        notifyDataSetChanged()
    }

    public override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SelectTimeViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.raw_selected_food_item, viewGroup, false)
        return SelectTimeViewHolder(view)
    }

    public override fun onBindViewHolder(
        holder: SelectTimeViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.tvSelectFood!!.text = items[position].foodItemName
        if (items[position].quantityPrimary != null && !TextUtils.isEmpty(items[position].quantityPrimary)
        ) {
            holder.tvSelectFoodQuantity!!.visibility = View.GONE
            if (items[position].quantitySecondary != null && !TextUtils.isEmpty(items[position].quantitySecondary)
            ) {
                holder.tvSelectFoodQuantity!!.text = "Quantity: " + items[position]
                    .quantityPrimary + " " + items[position]
                    .quantityUnitPrimary + " = " + items[position]
                    .quantitySecondary + " " + items[position].quantityUnitSecondary
            } else {
                holder.tvSelectFoodQuantity!!.text = "Quantity: " + items[position]
                    .quantityPrimary + " " + items[position].quantityUnitPrimary
            }
        } else {
            holder.tvSelectFoodQuantity!!.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            val newPosition: Int = holder.adapterPosition
            onclick!!.onSelectMostSearchFoodItemClick(items[position], newPosition)
        }
    }

    inner class SelectTimeViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        @JvmField
        @BindView(R.id.tvSelectFood)
        var tvSelectFood: TextView? = null

        @JvmField
        @BindView(R.id.imgCheck)
        var imgCheck: ImageView? = null

        @JvmField
        @BindView(R.id.tvSelectFoodQuantity)
        var tvSelectFoodQuantity: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    public override fun getItemCount(): Int {
        return items.size
    }
}