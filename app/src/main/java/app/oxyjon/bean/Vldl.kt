package app.oxyjon.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Vldl constructor() {
    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("vldl")
    @Expose
    var vldl: String? = null
}