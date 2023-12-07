package app.oxyjon.ui.kotlin.activity.adapter

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import app.oxyjon.R
import app.oxyjon.bean.PhysicalActivityResponse
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class ExpandableExercisePlanAdapter(
    private val _context: Context, _listDataHeader: ArrayList<PhysicalActivityResponse.Data>,
) : BaseExpandableListAdapter() {


    private var _listDataHeaderFiltered: ArrayList<PhysicalActivityResponse.Data> =
        _listDataHeader
    private var _listDataHeaderOriginal = ArrayList<PhysicalActivityResponse.Data>()

    init {
        _listDataHeaderOriginal.addAll(_listDataHeader)
    }

    override fun getChild(groupPosition: Int, childPosititon: Int): Any {
        return _listDataHeaderFiltered[groupPosition]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getChildView(
        groupPosition: Int, childPosition: Int,
        isLastChild: Boolean, convertView: View?, parent: ViewGroup,
    ): View {
        var convertView = convertView

        if (convertView == null) {
            val infalInflater = this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = infalInflater.inflate(R.layout.row_exercise_plan_item_inner, null)
        }
        val imgContent = convertView!!.findViewById<ShapeableImageView>(R.id.imgContent)
        val tvContentTitle = convertView!!.findViewById<TextView>(R.id.tvContentTitle)
        val tvContentDesc = convertView!!.findViewById<TextView>(R.id.tvContentDesc)
        val llRemark = convertView!!.findViewById<LinearLayout>(R.id.llRemark)
        val tvRemark = convertView!!.findViewById<TextView>(R.id.tvRemark)

        var mChild = (getChild(groupPosition, childPosition) as PhysicalActivityResponse.Data).activity_data[childPosition]

        var mParent = getGroup(groupPosition) as PhysicalActivityResponse.Data

        if (childPosition == 0) {
            if (!TextUtils.isEmpty(mParent.activity_day_remarks))
            {
                llRemark.visibility = View.VISIBLE
                tvRemark.text = mParent.activity_day_remarks
            }else{

                llRemark.visibility = View.GONE
            }
        } else {
            llRemark.visibility = View.GONE
        }

        tvContentTitle.text = mChild.activity_title
        tvContentDesc.text = Html.fromHtml(mChild.activity_details)
        Glide.with(_context).load(mChild.activity_image)
            .centerCrop()
            .placeholder(R.drawable.ic_preview)
            .into(imgContent)

        return convertView!!
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return _listDataHeaderFiltered[groupPosition].activity_data.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return this._listDataHeaderFiltered[groupPosition]
    }

    override fun getGroupCount(): Int {
        return this._listDataHeaderFiltered.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup,
    ): View {
        var convertView = convertView

        if (convertView == null) {
            val infalInflater =
                this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = infalInflater.inflate(R.layout.row_exercise_plan_item, null)
        }

        val imgNext = convertView!!.findViewById<ImageView>(R.id.imgNext)
        val tvTitle = convertView!!.findViewById<TextView>(R.id.tvTitle)

        var mTitle = getGroup(groupPosition) as PhysicalActivityResponse.Data

        tvTitle.text = mTitle.activity_day


        if (isExpanded) {
            imgNext.rotation = 270F
        } else {
            imgNext.rotation = 90F
        }

        return convertView!!
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}