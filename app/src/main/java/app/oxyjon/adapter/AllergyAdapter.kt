package app.oxyjon.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.R
import app.oxyjon.adapter.viewholder.AllergyViewHolder
import app.oxyjon.interfaces.IRecyclerClickListener
import app.oxyjon.retrofit.response.data.AllergyData

class AllergyAdapter constructor(
    private val context: Context?,
    var list: ArrayList<AllergyData?>,
    var selectdata: ArrayList<String>,
    var selectedexperties: HashMap<String?, AllergyData?>,
) : RecyclerView.Adapter<AllergyViewHolder>() {
    var clickListener: IRecyclerClickListener? = null
    public override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): AllergyViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.allergy_layot, viewGroup, false)
        return AllergyViewHolder(view)
    }

    public override fun onBindViewHolder(
        holder: AllergyViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        if (TextUtils.isEmpty(list.get(position)!!.isSelected)) {
        } else if ((list[position]!!.isSelected == "0")) {
            val custList: AllergyData? = list[position]
            list[position]!!.isIschecked = false
            custList!!.allergyId = list[position]!!.allergyId
            holder.tv_allergic!!.text = list[position]!!.allergyType
            list[position] = custList
            selectedexperties.remove(custList.allergyId)
        } else {
            holder.itemLayout!!.isSelected = true
            val custList: AllergyData? = list[position]
            custList!!.allergyId = list[position]!!.allergyId
            list.set(position, custList)
            selectedexperties[custList.allergyId] = custList
        }
        //  holder.itemLayout.setSelected(false);
        holder.tv_allergic!!.text = list[position]!!.allergyType
        holder.itemLayout!!.setOnClickListener { v ->
            v.setSelected(false)
            if (list[position]!!.isIschecked) {
                val custList: AllergyData? = list[position]
                list[position]!!.isIschecked = false
                custList!!.allergyId = list[position]!!.allergyId
                holder.tv_allergic!!.text = list[position]!!.allergyType
                list[position] = custList
                selectedexperties.remove(custList.allergyId)
            } else {
                list[position]!!.isIschecked = true
                v.isSelected = true
                holder.tv_allergic!!.text = list[position]!!.allergyType
                val custList: AllergyData? = list[position]
                custList!!.allergyId = list[position]!!.allergyId
                list[position] = custList
                selectedexperties[custList!!.allergyId] = custList
            }
        }
    }

    fun selecteddata(): HashMap<String?, AllergyData?> {
        return selectedexperties
    }

    public override fun getItemCount(): Int {
        return list.size
    }
}