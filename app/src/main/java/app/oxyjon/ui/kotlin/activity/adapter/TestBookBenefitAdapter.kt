package app.oxyjon.ui.kotlin.activity.adapter

import android.app.Activity
import android.text.Html
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.R
import app.oxyjon.bean.TestBookDetailResponse
import butterknife.BindView
import butterknife.ButterKnife

class TestBookBenefitAdapter(private val context: Activity) :
    RecyclerView.Adapter<TestBookBenefitAdapter.ViewHolder>() {
    private val items = ArrayList<TestBookDetailResponse.BloodtestDetails.Benefit>()
    var onclick: ClickListener? = null
    private val width: Int

    interface ClickListener {
        fun onReviewClick(medicineId: Int, pos: Int)
    }

    fun setClickListener(clickListener: ClickListener?) {
        onclick = clickListener
    }

    init {
        val displayMetrics = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
    }

    fun setData(item: ArrayList<TestBookDetailResponse.BloodtestDetails.Benefit>?) {
        items.clear()
        items.addAll(item!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.raw_test_book_benefit, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.tvTitle!!.text = Html.fromHtml(items[pos].name)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @kotlin.jvm.JvmField
        @BindView(R.id.tvTitle)
        var tvTitle: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}