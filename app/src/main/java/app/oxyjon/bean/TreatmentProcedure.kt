package app.oxyjon.bean

import app.oxyjon.retrofit.response.data.ProcedureData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TreatmentProcedure constructor() {
    @SerializedName("pDate")
    @Expose
    var proceduredate: String? = null

    @SerializedName("pCost")
    @Expose
    var procedurecost: String? = null
    var procedureData: ArrayList<ProcedureData>? = null

    @SerializedName("procedureType")
    @Expose
    var procedureType: String? = null

    @SerializedName("procedureId")
    @Expose
    var id: String? = null
}