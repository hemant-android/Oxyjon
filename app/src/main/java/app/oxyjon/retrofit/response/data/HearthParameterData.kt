package app.oxyjon.retrofit.response.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class HearthParameterData constructor() {
    @SerializedName("sugar")
    @Expose
    var sugar: String? = null

    @SerializedName("bloodPressure")
    @Expose
    var bloodPressure: String? = null

    @SerializedName("pulse")
    @Expose
    var pulse: String? = null

    @SerializedName("weight")
    @Expose
    var weight: String? = null

    @SerializedName("cholesterol")
    @Expose
    var cholesterol: String? = null
}