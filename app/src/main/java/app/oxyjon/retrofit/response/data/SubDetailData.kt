package app.oxyjon.retrofit.response.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class SubDetailData constructor() {
    @SerializedName("severity")
    @Expose
    var severity: String? = null

    @SerializedName("subDate")
    @Expose
    var date: String? = null

    @SerializedName("time")
    @Expose
    var time: String? = null

    @SerializedName("ampm")
    @Expose
    var am: String? = null
}