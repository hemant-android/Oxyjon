package app.oxyjon.retrofit.response

import app.oxyjon.retrofit.response.data.HealthConditionData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Updatestepfourdata constructor() {
    @SerializedName("profileId")
    @Expose
    var profileId: String? = null

    @SerializedName("customerId")
    @Expose
    var customerId: String? = null

    @SerializedName("healthConditions")
    @Expose
    var healthConditions: ArrayList<HealthConditionData>? = null
}