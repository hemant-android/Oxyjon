package app.oxyjon.adapter.viewholder

import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.R
import app.oxyjon.interfaces.IRecyclerClickListener
import butterknife.BindView
import butterknife.ButterKnife

class AllergyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @JvmField
    @BindView(R.id.rl_item)
    var itemLayout: RelativeLayout? = null

    @JvmField
    @BindView(R.id.tv_allergic)
    var tv_allergic: TextView? = null
    var clickListener: IRecyclerClickListener? = null

    init {
        ButterKnife.bind(this, itemView)
    }
}