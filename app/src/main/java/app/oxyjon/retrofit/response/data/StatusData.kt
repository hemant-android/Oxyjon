package app.oxyjon.retrofit.response.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class StatusData constructor() {
    @SerializedName("customerId")
    @Expose
    var customerId: String? = null

    @SerializedName("profileStatus")
    @Expose
    var profileStatus: ArrayList<Profilestatus>? = null
}