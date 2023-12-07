package app.oxyjon.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Bloodlinelist constructor() {
    @SerializedName("city")
    @Expose
    var city: String? = null

    @SerializedName("city_name")
    @Expose
    var cityName: String? = null
}