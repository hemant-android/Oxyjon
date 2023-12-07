package app.oxyjon.retrofit.response

import app.oxyjon.retrofit.response.data.BloodlistData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Bloodgrouplistresponce {
    @SerializedName("errorCode")
    @Expose
    var errorCode: String? = null

    @SerializedName("errorMsg")
    @Expose
    var errorMsg: String? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<BloodlistData>? = null
}