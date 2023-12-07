package app.oxyjon.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class UpdateSteptwodata constructor() {
    @SerializedName("profileId")
    @Expose
    var profileId: String? = null

    @SerializedName("customerId")
    @Expose
    var customerId: String? = null

    @SerializedName("contactName")
    @Expose
    var contactName: String? = null

    @SerializedName("contactNumber")
    @Expose
    var contactNumber: String? = null

    @SerializedName("emailId")
    @Expose
    var emailId: String? = null

    @SerializedName("relationship")
    @Expose
    var relationship: String? = null
}