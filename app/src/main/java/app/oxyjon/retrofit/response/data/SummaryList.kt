package app.oxyjon.retrofit.response.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class SummaryList constructor() {
    @SerializedName("profileId")
    @Expose
    var profileId: String? = null

    @SerializedName("profileLink")
    @Expose
    var profileLink: String? = null

    @SerializedName("pkey")
    @Expose
    var pkey: String? = null
}