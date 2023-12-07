package app.oxyjon.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.R
import app.oxyjon.bean.GetMyFoodDiaryResponse
import app.oxyjon.ui.kotlin.activity.EditDiaryActivity
import butterknife.BindView
import butterknife.ButterKnife
import java.text.DecimalFormat

class MyFoodDiaryItemAdapter constructor(private val context: Context) :
    RecyclerView.Adapter<MyFoodDiaryItemAdapter.SelectTimeViewHolder>() {
    private val items: ArrayList<GetMyFoodDiaryResponse.Datum.FoodItem?> = ArrayList()
    var onclick: ClickListener? = null

    open interface ClickListener {
        fun onSelectFoodItemClick(position: Int)
    }

    fun setClickListener(clickListener: ClickListener?) {
        onclick = clickListener
    }

    fun setData(item: ArrayList<GetMyFoodDiaryResponse.Datum.FoodItem>?) {
        items.clear()
        items.addAll((item)!!)
        notifyDataSetChanged()
    }

    public override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SelectTimeViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.raw_my_food_diary_item, viewGroup, false)
        return SelectTimeViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    public override fun onBindViewHolder(
        holder: SelectTimeViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        if (items[position]!!.foodItemName != null) {
            holder.tvFoodName!!.visibility = View.VISIBLE
            holder.tvFoodName!!.text = items[position]!!.foodItemName
        } else {
            holder.tvFoodName!!.visibility = View.GONE
        }
        if (items[position]!!.foodType != null) {
            holder.tvFoodType!!.visibility = View.VISIBLE
            if (items[position]!!.foodType.equals("veg", ignoreCase = true)) {
                holder.tvFoodType!!.setTextColor(context.resources.getColor(R.color.green))
            } else {
                holder.tvFoodType!!.setTextColor(context.resources.getColor(R.color.red))
            }
            holder.tvFoodType!!.text = items[position]!!.foodType
        } else {
            holder.tvFoodType!!.visibility = View.GONE
        }
        if (items[position]!!.calorieGm != null) {
            holder.tvCalorie!!.visibility = View.VISIBLE
            val parseD: Double = items[position]!!.calorieGm!!.toDouble()
            holder.tvCalorie!!.text = "" + parseD.toInt()
        } else {
            holder.tvCalorie!!.visibility = View.GONE
        }
        if (items[position]!!.mealQuantity != null && !TextUtils.isEmpty(items[position]!!.mealQuantity)
        ) {
            holder.tvFoodQuantity!!.visibility = View.VISIBLE
            val df: DecimalFormat = DecimalFormat()
            df.maximumFractionDigits = 1
            val totalCalorie: Double = items[position]!!.mealQuantity!!.toDouble()
            holder.tvFoodQuantity!!.setText("" + df.format(totalCalorie) + " " + items[position]
            !!.mealQuantityUnit)
        } else {
            holder.tvFoodQuantity!!.visibility = View.GONE
        }
        holder.imgClose!!.setOnClickListener { onclick!!.onSelectFoodItemClick(items[position]!!.id!!) }
        holder.itemView.setOnClickListener {
            val intent: Intent = Intent(context, EditDiaryActivity::class.java)
            intent.putExtra("foodItem", items[position])
            context.startActivity(intent)
        }
    }

    inner class SelectTimeViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        @JvmField
        @BindView(R.id.tvFoodName)
        var tvFoodName: TextView? = null

        @JvmField
        @BindView(R.id.tvFoodTime)
        var tvFoodTime: TextView? = null

        @JvmField
        @BindView(R.id.imgClose)
        var imgClose: ImageView? = null

        @JvmField
        @BindView(R.id.tvCalorie)
        var tvCalorie: TextView? = null

        @JvmField
        @BindView(R.id.tvFoodType)
        var tvFoodType: TextView? = null

        @JvmField
        @BindView(R.id.tvFoodQuantity)
        var tvFoodQuantity: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    public override fun getItemCount(): Int {
        return items.size
    }
}