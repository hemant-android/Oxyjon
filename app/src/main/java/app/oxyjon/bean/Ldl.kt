package app.oxyjon.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Ldl constructor() {
    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("ldl")
    @Expose
    var ldl: String? = null
}