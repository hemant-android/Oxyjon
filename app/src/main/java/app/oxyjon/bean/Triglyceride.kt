package app.oxyjon.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Triglyceride constructor() {
    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("triglycerides")
    @Expose
    var triglycerides: String? = null
}