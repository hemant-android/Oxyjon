package app.oxyjon.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class SendOtpResponse constructor() {
    @SerializedName("errorCode")
    @Expose
    var errorCode: String? = null

    @SerializedName("errorMsg")
    @Expose
    var errorMsg: String? = null

    @SerializedName("data")
    @Expose
    var data: MobileData? = null

    inner class MobileData constructor() {
        @SerializedName("new_user")
        @Expose
        var newUser: String? = null

        @SerializedName("customer_id")
        @Expose
        var customerId: String? = null

        @SerializedName("mobile_no")
        @Expose
        var mobileNo: String? = null

        @SerializedName("profileId")
        @Expose
        var profileId: String? = null

        @SerializedName("fullName")
        @Expose
        var fullName: String? = null

        @SerializedName("profile_image")
        @Expose
        var profileImage: String? = null

        @SerializedName("screen_quest1")
        @Expose
        var screen_quest1: Boolean? = null

        @SerializedName("screen_quest2")
        @Expose
        var screen_quest2: Boolean? = null

        @SerializedName("screen_quest3")
        @Expose
        var screen_quest3: Boolean? = null

        @SerializedName("profile_type")
        @Expose
        var profile_type: String? = null

        @SerializedName("payment_status")
        @Expose
        var payment_status: String? = ""

        @SerializedName("is_hp_select_ui_active")
        @Expose
        var is_hp_select_ui_active: String? = ""

        @SerializedName("auth_token")
        @Expose
        var token: String? = ""
    }
}