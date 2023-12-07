package app.oxyjon.ui.kotlin.fragment.dashboard.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import app.oxyjon.R
import app.oxyjon.bean.DashboardResponse
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class RecipeAdapter(val mContext: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {
    private val items: ArrayList<DashboardResponse.Data.Recipes>? = arrayListOf()
    lateinit var onclick: onClickListner

    interface onClickListner {
        fun onSelectRecipesAction(id: String?, type: String?,url: String?,heading: String?)
    }

    fun setClickListner(onclick: onClickListner) {
        this.onclick = onclick;
    }

    fun setData(item: ArrayList<DashboardResponse.Data.Recipes>) {
        items!!.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tvRecipeName!!.text = items!![position].heading

        if (items!![position].url != null && !TextUtils.isEmpty(items!![position].url)) {
            Glide.with(mContext).load(items!![position].url)
                .placeholder(R.drawable.progress_animation).into(holder.imgRecipe!!)
        }

        holder.itemView.setOnClickListener {
            onclick.onSelectRecipesAction(items!![position].id, items!![position].contenttype,items!![position].detail_url,items!![position].heading)
        }

    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_recipes,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val imgRecipe: ShapeableImageView? = view.findViewById(R.id.imgRecipe)
        val tvRecipeName: TextView? = view.findViewById(R.id.tvRecipeName)

    }
}

