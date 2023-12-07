package app.oxyjon.retrofit.response

import app.oxyjon.retrofit.response.data.AllergyData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Updatestepthreedata constructor() {
    @SerializedName("profileId")
    @Expose
    var profileId: String? = null

    @SerializedName("customerId")
    @Expose
    var customerId: String? = null

    @SerializedName("allergyId")
    @Expose
    var allergyId: ArrayList<String>? = null

    @SerializedName("allergyData")
    @Expose
    var allergyData: ArrayList<AllergyData>? = null
}