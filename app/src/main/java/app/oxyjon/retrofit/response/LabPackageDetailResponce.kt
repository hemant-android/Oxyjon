package app.oxyjon.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class LabPackageDetailResponce constructor() {
    @SerializedName("errorCode")
    @Expose
    var errorCode: String? = null

    @SerializedName("errorMsg")
    @Expose
    var errorMsg: String? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<LabPackageDetail>? = null
}