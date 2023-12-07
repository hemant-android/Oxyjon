package app.oxyjon.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class UpdateSteponeData constructor() {
    @SerializedName("profileId")
    @Expose
    var profileId: String? = null

    @SerializedName("customerId")
    @Expose
    var customerId: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("bloodGroup")
    @Expose
    var bloodGroup: String? = null

    @SerializedName("bloodGroupType")
    @Expose
    var bloodGroupType: String? = null

    @SerializedName("birthDate")
    @Expose
    var birthDate: String? = null

    @SerializedName("sex")
    @Expose
    var sex: String? = null

    @SerializedName("residingIn")
    @Expose
    var residingIn: String? = null

    @SerializedName("cityName")
    @Expose
    var cityName: String? = null

    @SerializedName("Address")
    @Expose
    var address: String? = null

    @SerializedName("adharNumber")
    @Expose
    var adharNumber: String? = null

    @SerializedName("insuranceNumber")
    @Expose
    var insuranceNumber: String? = null

    @SerializedName("insuranceProvider")
    @Expose
    var insuranceProvider: String? = null

    @SerializedName("height_ft")
    @Expose
    var heightFt: String? = null

    @SerializedName("height_inches")
    @Expose
    var heightInches: String? = null

    @SerializedName("weight")
    @Expose
    var weight: String? = null
}