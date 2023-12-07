package app.oxyjon.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Diastolic constructor() {
    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("diastolic")
    @Expose
    var diastolic: String? = null
}