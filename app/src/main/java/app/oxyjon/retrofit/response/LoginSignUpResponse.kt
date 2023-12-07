package app.oxyjon.retrofit.response

import app.oxyjon.retrofit.response.data.LoginData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class LoginSignUpResponse constructor() {
    @SerializedName("errorCode")
    @Expose
    var errorCode: String? = null

    @SerializedName("errorMsg")
    @Expose
    var errorMsg: String? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<LoginData>? = null
}