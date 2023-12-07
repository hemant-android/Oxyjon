package app.oxyjon.ui.kotlin.activity.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.oxyjon.R
import app.oxyjon.bean.DocumentReportResponse
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat

class DoctorPrescriptionAdapter(val mContext: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<DoctorPrescriptionAdapter.ViewHolder>() {
    private val items: ArrayList<DocumentReportResponse.Data.MedicalPrescription>? =
        arrayListOf()
    lateinit var onclick: onClickListner

    interface onClickListner {
        fun onSelectItem(fileUrl: String?,fileName: String?)
    }

    fun setClickListner(onclick: onClickListner) {
        this.onclick = onclick;
    }

    fun setData(item: ArrayList<DocumentReportResponse.Data.MedicalPrescription>) {
        items!!.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (items!![position].document_url != null && !TextUtils.isEmpty(items!![position].document_url)) {
            holder.imgUpload!!.visibility = View.GONE
            if (items!![position].document_url.contains(".pdf"))
            {
                Glide.with(mContext).load(R.drawable.pdf_logo).into(holder.imgUpload)
            }else{
                Glide.with(mContext).load(items!![position].document_url)
                    .placeholder(R.drawable.progress_animation)
                    .into(holder.imgUpload)
            }

        }
        holder.tvFileName!!.text = items!![position].filename
//        holder.tvType!!.text = "Document Type: " + "Medical prescription"

        val startDate: String = items!![position].uploaded_date

        val inputPattern = "yyyy-MM-dd HH:mm:ss"
        val outputPattern = "MMM yyyy"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)
        var date = inputFormat.parse(startDate)
       var strDate = outputFormat.format(date)

        holder.tvType!!.text = strDate

        holder.itemView.setOnClickListener {
            onclick.onSelectItem(items!![position].document_url,items!![position].filename)
        }
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.upload_document_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val imgUpload: ShapeableImageView? = view.findViewById(R.id.img_upload)
        val tvFileName: TextView? = view.findViewById(R.id.tv_file_name)
        val tvType: TextView? = view.findViewById(R.id.tv_type)
    }
}

