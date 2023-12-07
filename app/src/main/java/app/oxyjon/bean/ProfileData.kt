package app.oxyjon.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ProfileData constructor() {
    @SerializedName("customerId")
    @Expose
    var customerId: String? = null

    @SerializedName("fullName")
    @Expose
    var fullName: String? = null

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

    @SerializedName("cityName")
    @Expose
    var cityName: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("test_suggestion")
    @Expose
    var testSuggestion: String? = null
}