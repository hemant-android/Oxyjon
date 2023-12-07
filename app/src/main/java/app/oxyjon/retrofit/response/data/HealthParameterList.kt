package app.oxyjon.retrofit.response.data

import app.oxyjon.bean.TreatmentProvider
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class HealthParameterList constructor() {
    @SerializedName("profileId")
    @Expose
    var profileId: String? = null

    @SerializedName("hearthParameter")
    @Expose
    var hearthParameter: ArrayList<HearthParameterData>? = null

    @SerializedName("treatmentProvider")
    @Expose
    var treatmentProvider: ArrayList<TreatmentProvider>? = null

    @SerializedName("tprovider_id")
    @Expose
    var tprovider_id: String? = null

    @SerializedName("frequency")
    @Expose
    var frequency: String? = null
}