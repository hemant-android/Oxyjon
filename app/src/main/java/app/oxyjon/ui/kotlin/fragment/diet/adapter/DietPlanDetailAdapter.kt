package app.oxyjon.ui.kotlin.fragment.diet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.oxyjon.R
import app.oxyjon.bean.MyDietPlanResponse

class DietPlanDetailAdapter(val mContext: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<DietPlanDetailAdapter.ViewHolder>() {
    private val items: ArrayList<MyDietPlanResponse.Data.MealData>? = arrayListOf()
    lateinit var onclick: onClickListner

    interface onClickListner {
        fun onSelectFileFolderOption(position: Int, type: String)
    }

    fun setClickListner(onclick: onClickListner) {
        this.onclick = onclick;
    }

    fun setData(item: ArrayList<MyDietPlanResponse.Data.MealData>?) {
        items!!.clear()
        items.addAll(item!!)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvPosition!!.text = ""+(position+1)+"."
        holder.tvFoodName!!.text = items!![position].food_name
        holder.tvCal!!.text = items!![position].calories.split(" ")[0]
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_my_diet_plan,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val tvPosition: TextView? = view.findViewById(R.id.tvPosition)
        val tvFoodName: TextView? = view.findViewById(R.id.tvFoodName)
        val tvCal: TextView? = view.findViewById(R.id.tvCal)
    }


}

