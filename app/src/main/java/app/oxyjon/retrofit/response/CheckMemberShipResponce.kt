package app.oxyjon.retrofit.response

import app.oxyjon.retrofit.response.data.CheckMemberShipData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class CheckMemberShipResponce constructor() {
    @SerializedName("errorCode")
    @Expose
    var errorCode: String? = null

    @SerializedName("errorMsg")
    @Expose
    var errorMsg: String? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<CheckMemberShipData>? = null
}