package app.oxyjon.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class LocalityList constructor() {
    @SerializedName("address")
    @Expose
    var address: String? = null
}