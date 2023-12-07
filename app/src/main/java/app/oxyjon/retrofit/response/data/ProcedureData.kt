package app.oxyjon.retrofit.response.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ProcedureData constructor() {
    @SerializedName("procedureId")
    @Expose
    var procedureId: String? = null

    @SerializedName("procedureType")
    @Expose
    var procedureType: String? = null
}