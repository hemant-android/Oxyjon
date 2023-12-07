package app.oxyjon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.R
import app.oxyjon.bean.StepsCaloriesData
import butterknife.BindView
import butterknife.ButterKnife

class StepDetailAdapter constructor(
    private val context: Context,
    private val itemSteps: ArrayList<StepsCaloriesData>,
    private val itemCal: ArrayList<StepsCaloriesData>?,
) : RecyclerView.Adapter<StepDetailAdapter.StepDetailViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): StepDetailViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.row_step_details, viewGroup, false)
        return StepDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepDetailViewHolder, position: Int) {
        if (itemCal != null && itemCal.size > 0) {
            for (cals: StepsCaloriesData in itemCal) {
                if (itemSteps[position].dateTime
                        .equals(cals.dateTime, ignoreCase = true)
                ) {
//                    holder.rlCalories.setVisibility(View.VISIBLE);
                    holder.tvCalories!!.text = cals.roundedValue
                } else {
//                    holder.rlCalories.setVisibility(View.GONE);
                }
            }
        } else {
            holder.rlCalories!!.visibility = View.GONE
        }
        holder.tvStep!!.text = itemSteps[position].roundedValue
        holder.tvDateTime!!.text = itemSteps[position].dateTime
    }

    inner class StepDetailViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        @JvmField
        @BindView(R.id.tvStep)
        var tvStep: TextView? = null

        @JvmField
        @BindView(R.id.tvDateTime)
        var tvDateTime: TextView? = null

        @JvmField
        @BindView(R.id.tvCalories)
        var tvCalories: TextView? = null

        @JvmField
        @BindView(R.id.rlCalories)
        var rlCalories: RelativeLayout? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    public override fun getItemCount(): Int {
        return itemSteps.size
    }
}