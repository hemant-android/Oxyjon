package app.oxyjon.retrofit.response.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class CheckMemberShipData constructor() {
    @SerializedName("customerId")
    @Expose
    var customerId: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("startDate")
    @Expose
    var startDate: String? = null

    @SerializedName("expiryDate")
    @Expose
    var expiryDate: String? = null

    @SerializedName("plan")
    @Expose
    var plan: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null
}