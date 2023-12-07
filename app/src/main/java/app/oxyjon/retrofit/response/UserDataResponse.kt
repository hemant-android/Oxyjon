package app.oxyjon.retrofit.response

import app.oxyjon.retrofit.response.data.UserData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class UserDataResponse constructor() {
    @SerializedName("errorCode")
    @Expose
    var errorCode: String? = null

    @SerializedName("errorMsg")
    @Expose
    var errorMsg: String? = null

    @SerializedName("analytics_message")
    @Expose
    var analyticMessage: String? = null

    @SerializedName("data")
    @Expose
    var data: List<UserData>? = null
}