package app.oxyjon.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Hdl constructor() {
    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("hdl")
    @Expose
    var hdl: String? = null
}