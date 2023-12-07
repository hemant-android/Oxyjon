package app.oxyjon.ui.kotlin.activity.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.R
import app.oxyjon.bean.TestBookDetailResponse
import butterknife.BindView
import butterknife.ButterKnife

class TestNameAdapter(private val context: Activity) :
    RecyclerView.Adapter<TestNameAdapter.ViewHolder>() {
    private val items = ArrayList<TestBookDetailResponse.BloodtestDetails.Test>()
    fun setData(item: ArrayList<TestBookDetailResponse.BloodtestDetails.Test>?) {
        items.clear()
        items.addAll(item!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.raw_test_name, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.tvTestName!!.text = items[pos].name
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @kotlin.jvm.JvmField
        @BindView(R.id.tvTestName)
        var tvTestName: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}