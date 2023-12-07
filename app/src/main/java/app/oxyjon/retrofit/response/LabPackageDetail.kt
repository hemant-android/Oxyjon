package app.oxyjon.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class LabPackageDetail constructor() {
    @SerializedName("labId")
    @Expose
    var labId: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("price")
    @Expose
    var price: String? = null

    @SerializedName("hospitalName")
    @Expose
    var hospitalName: String? = null

    @SerializedName("phone")
    @Expose
    var phone: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("packageFor")
    @Expose
    var packageFor: String? = null

    @SerializedName("packageCategory")
    @Expose
    var packageCategory: String? = null

    @SerializedName("availableLabTest")
    @Expose
    var availableLabTest: String? = null
}