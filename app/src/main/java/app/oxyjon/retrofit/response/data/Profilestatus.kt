package app.oxyjon.retrofit.response.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Profilestatus constructor() {
    @SerializedName("status1")
    @Expose
    var status1: String? = null

    @SerializedName("status2")
    @Expose
    var status2: String? = null

    @SerializedName("status3")
    @Expose
    var status3: String? = null

    @SerializedName("status4")
    @Expose
    var status4: String? = null

    @SerializedName("status5")
    @Expose
    var status5: String? = null

    @SerializedName("status6")
    @Expose
    var status6: String? = null

    @SerializedName("status7")
    @Expose
    var status7: String? = null
}