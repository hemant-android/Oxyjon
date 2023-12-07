package app.oxyjon.ui.kotlin.activity.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import app.oxyjon.R
import app.oxyjon.bean.HealthPlanDetailResponse
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class HealthDietPlanDetailCareTeamAdapter(val mContext: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<HealthDietPlanDetailCareTeamAdapter.ViewHolder>() {
    private val items: ArrayList<HealthPlanDetailResponse.HealthplanDetails.Careteam>? =
        arrayListOf()
    lateinit var onclick: onClickListner

    interface onClickListner {
        fun onSelectDoctor()
    }

    fun setClickListner(onclick: onClickListner) {
        this.onclick = onclick;
    }

    fun setData(item: ArrayList<HealthPlanDetailResponse.HealthplanDetails.Careteam>) {
        items!!.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (items!![position].profile_url != null && !TextUtils.isEmpty(items!![position].profile_url)) {
            holder.imgDoctor!!.visibility = View.VISIBLE
            Glide.with(mContext).load(items!![position].profile_url).placeholder(R.drawable.progress_animation).centerCrop()
                .into(holder.imgDoctor)
        }
        holder.tvDrName!!.text = items!![position].name
        holder.tvDrSpecialist!!.text = items!![position].details

        if (items[position].review_star != null && !TextUtils.isEmpty(items[position].review_star)) {
            holder.ratingBar!!.rating = items[position].review_star.toFloat()
        } else {
            holder.ratingBar!!.rating = "0".toFloat()
        }

    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_diet_care_team,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val llMain: LinearLayout? = view.findViewById(R.id.llMain)
        val imgDoctor: ShapeableImageView? = view.findViewById(R.id.imgDoctor)
        val tvDrName: TextView? = view.findViewById(R.id.tvDrName)
        val tvDrSpecialist: TextView? = view.findViewById(R.id.tvDrSpecialist)
        val ratingBar: AppCompatRatingBar? = view.findViewById(R.id.ratingBar)
    }
}

