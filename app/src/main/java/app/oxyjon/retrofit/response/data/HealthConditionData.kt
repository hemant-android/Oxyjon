package app.oxyjon.retrofit.response.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class HealthConditionData constructor() {
    var isSelected: Boolean = false

    @SerializedName("healthId")
    @Expose
    var healthId: String? = null

    @SerializedName("healthType")
    @Expose
    var healthType: String? = null

    @SerializedName("healthDate")
    @Expose
    var healthDate: String? = null

    @SerializedName("symptoms")
    @Expose
    var subCategories: ArrayList<HealthConditionSublist?>? = null
}