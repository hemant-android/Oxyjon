package app.oxyjon.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class TreatmentData constructor() {
    @SerializedName("medicine")
    @Expose
    var medicine: String? = null

    @SerializedName("startDate")
    @Expose
    var startdate: String? = null

    @SerializedName("endDate")
    @Expose
    var enddate: String? = null

    @SerializedName("frequency")
    @Expose
    var frequency: String? = null
}