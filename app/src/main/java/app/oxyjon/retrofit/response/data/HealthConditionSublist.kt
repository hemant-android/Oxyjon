package app.oxyjon.retrofit.response.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HealthConditionSublist constructor() {
    var isChecked: Boolean = false

    @SerializedName("symptomsId")
    @Expose
    var subId: String? = null

    @SerializedName("symptoms")
    @Expose
    var symptoms: String? = null

    @SerializedName("subSymptoms")
    @Expose
    private var subDetail: ArrayList<SubDetailData>? = null
    fun getSubDetail(): ArrayList<SubDetailData> {
        if (subDetail == null) {
            subDetail = ArrayList()
            subDetail!!.add(SubDetailData())
        }
        return subDetail!!
    }

    fun setSubDetail(subDetail: ArrayList<SubDetailData>?) {
        this.subDetail = subDetail
    }
}