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
import app.oxyjon.database.FoodDiary
import butterknife.BindView
import butterknife.ButterKnife

class FoodSelectedAdapter constructor(private val context: Context) :
    RecyclerView.Adapter<FoodSelectedAdapter.SelectTimeViewHolder>() {
    private val items: ArrayList<FoodDiary> = ArrayList()
    var onclick: ClickListener? = null

    open interface ClickListener {
        fun onSelectFoodItemClick(position: Int)
    }

    fun setClickListener(clickListener: ClickListener?) {
        onclick = clickListener
    }

    fun setData(item: ArrayList<FoodDiary>?) {
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
        if (items[holder.adapterPosition].select) {
            holder.imgCheck!!.setImageResource(R.drawable.ic_close_pink)
        } else {
            holder.imgCheck!!.setImageResource(R.drawable.ic_add)
        }
        if (items.get(position).quantityPrimary != null && !TextUtils.isEmpty(items.get(
                position).quantityPrimary)
        ) {
            holder.tvSelectFoodQuantity!!.visibility = View.VISIBLE
            if (items.get(position).quantitySecondary != null && !TextUtils.isEmpty(items[position].quantitySecondary)
            ) {
                holder.tvSelectFoodQuantity!!.text = "Quantity: " + items.get(position)
                    .quantityPrimary + " " + items[position]
                    .quantityUnitPrimary + " = " + items[position]
                    .quantitySecondary + " " + items[position].quantityUnitSecondary
            } else {
                holder.tvSelectFoodQuantity!!.text = "Quantity: " + items[position]
                    .quantityPrimary + " " + items[position].quantityUnitPrimary
            }
        } else {
            holder.tvSelectFoodQuantity!!.setVisibility(View.GONE)
        }
        holder.imgCheck!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View?) {
                if (items.get(holder.adapterPosition).select) {
//                    items.get(holder.getAdapterPosition()).setSelect(false);
//                    holder.imgCheck.setImageResource(R.drawable.ic_add);
                    val newPosition: Int = holder.adapterPosition
                    onclick!!.onSelectFoodItemClick(newPosition)
                    items.removeAt(newPosition)
                    notifyItemRemoved(newPosition)
                    notifyItemRangeChanged(newPosition, items.size)
                } /*else {
                    items.get(holder.getAdapterPosition()).setSelect(true);
                    holder.imgCheck.setImageResource(R.drawable.ic_close_pink);
                }*/
            }
        })
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