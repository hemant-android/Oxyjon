package app.oxyjon.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.R
import app.oxyjon.bean.GetMyFoodDiaryResponse
import app.oxyjon.ui.kotlin.activity.AddDiaryActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper.trackEvent


class MyFoodDiaryListAdapter constructor(private val context: Context) :
    RecyclerView.Adapter<MyFoodDiaryListAdapter.SelectTimeViewHolder>(),
    MyFoodDiaryItemAdapter.ClickListener {
    private val items: ArrayList<GetMyFoodDiaryResponse.Datum?> = ArrayList()
    var onclick: ClickListener? = null
    private var selectedDate: String? = null
    private var selectedItemPos: Int = 0
    public override fun onSelectFoodItemClick(fooId: Int) {
        onclick!!.onSelectFoodItemClick(fooId)
    }

    open interface ClickListener {
        fun onSelectFoodItemClick(position: Int)
    }

    fun setClickListener(clickListener: ClickListener?) {
        onclick = clickListener
    }

    fun setData(item: java.util.ArrayList<GetMyFoodDiaryResponse.Datum>?, selectedDate: String?) {
        items.clear()
        items.addAll((item)!!)
        this.selectedDate = selectedDate
        notifyDataSetChanged()
    }

    public override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SelectTimeViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.raw_my_food_diary_list, viewGroup, false)
        return SelectTimeViewHolder(view)
    }

    public override fun onBindViewHolder(
        holder: SelectTimeViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        if (items[position]!!.name != null) {
            holder.tvHeading!!.text = items[position]!!.name
        }
        holder.tvCalorie!!.text = items[position]!!.totalCalorieIntake + " of " + items[position]!!.totalCalorie + " Cal"

        val foodItemAdapter = MyFoodDiaryItemAdapter(context)
        holder.rvFoodItem!!.adapter = foodItemAdapter
        foodItemAdapter.setClickListener(this)
        foodItemAdapter.setData(items[position]!!.fooditems)
        if (items[position]!!.getOpen() != null && items[position]!!.getOpen()!!) {
            holder.rvFoodItem!!.visibility = View.VISIBLE
        } else {
            holder.rvFoodItem!!.visibility = View.GONE
        }
        holder.tvHeading!!.setOnClickListener { selectClickedAndUnselectPrevious(position) }
        holder.imgAdd!!.setOnClickListener {
            val intent = Intent(context, AddDiaryActivity::class.java)
            intent.putExtra("navigationType", "addFoodItem")
            intent.putExtra("foodTime", items[position]!!.name)
            intent.putExtra("selectDate", selectedDate)
            context.startActivity(intent)
            val properties: Properties = Properties()
            properties.addAttribute("foodTime", items[position]!!.name)
            properties.addAttribute("selectedDate", selectedDate)
            properties.addAttribute("isClick", true)
            trackEvent(context, "AddFood", properties)
        }
    }

    inner class SelectTimeViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        @JvmField
        @BindView(R.id.tvHeading)
        var tvHeading: TextView? = null

        @JvmField
        @BindView(R.id.tvCalorie)
        var tvCalorie: TextView? = null

        @JvmField
        @BindView(R.id.imgAdd)
        var imgAdd: ImageView? = null

        @JvmField
        @BindView(R.id.rvFoodItem)
        var rvFoodItem: RecyclerView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    public override fun getItemCount(): Int {
        return items.size
    }

    private fun selectClickedAndUnselectPrevious(position: Int) {
        items[selectedItemPos]!!.setOpen(false)
        items[position]!!.setOpen(true)
        notifyItemChanged(selectedItemPos)
        notifyItemChanged(position)
        selectedItemPos = position
    }
}