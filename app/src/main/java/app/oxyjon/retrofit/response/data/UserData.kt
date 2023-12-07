package app.oxyjon.retrofit.response.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class UserData constructor() {
    @SerializedName("profileId")
    @Expose
    var profileId: String? = null

    @SerializedName("customerId")
    @Expose
    var customerId: String? = null

    @SerializedName("fullName")
    @Expose
    var fullName: String? = null

    @SerializedName("userName")
    @Expose
    var userName: String? = null

    @SerializedName("emailAddress")
    @Expose
    var emailAddress: String? = null

    @SerializedName("countryCode")
    @Expose
    var countryCode: String? = null

    @SerializedName("mobileNumber")
    @Expose
    var mobileNumber: String? = null

    @SerializedName("city")
    @Expose
    var city: String? = null

    @SerializedName("getProfileStatus")
    @Expose
    var getProfileStatus: ArrayList<Profilestatus>? = null

    @SerializedName("image")
    @Expose
    var image: String? = null
}