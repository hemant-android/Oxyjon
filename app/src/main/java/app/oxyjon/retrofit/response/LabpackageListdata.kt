package app.oxyjon.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class LabpackageListdata constructor() {
    var isSelected: Boolean = false

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

    @SerializedName("city")
    @Expose
    var city: String? = null

    @SerializedName("gender")
    @Expose
    var gender: String? = null

    @SerializedName("age")
    @Expose
    var age: String? = null

    @SerializedName("lab_test")
    @Expose
    var labTest: String? = null
}