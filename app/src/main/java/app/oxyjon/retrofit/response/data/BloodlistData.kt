package app.oxyjon.retrofit.response.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class BloodlistData constructor() {
    @SerializedName("bloodGroupId")
    @Expose
    var bloodGroupId: String? = null

    @SerializedName("bloodGroupType")
    @Expose
    var bloodGroupType: String? = null
}