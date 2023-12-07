package app.oxyjon.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Systolic constructor() {
    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("systolic")
    @Expose
    var systolic: String? = null
}