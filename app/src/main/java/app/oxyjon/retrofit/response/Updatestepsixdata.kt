package app.oxyjon.retrofit.response

import app.oxyjon.bean.TreatmentData
import app.oxyjon.bean.TreatmentProcedure
import app.oxyjon.bean.TreatmentProvider
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Updatestepsixdata constructor() {
    @SerializedName("profileId")
    @Expose
    var profileId: String? = null

    @SerializedName("treatment")
    @Expose
    var treatment: ArrayList<TreatmentData>? = null

    @SerializedName("procedure")
    @Expose
    var procedure: ArrayList<TreatmentProcedure>? = null

    @SerializedName("treatmentProvider")
    @Expose
    var treatmentProvider: ArrayList<TreatmentProvider>? = null

    @SerializedName("nonMedicalIntervention")
    @Expose
    var nonMedicalIntervention: String? = null
}