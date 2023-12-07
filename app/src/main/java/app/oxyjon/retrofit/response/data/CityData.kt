package app.oxyjon.retrofit.response.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class CityData constructor() {
    @SerializedName("cityId")
    @Expose
    var cityId: String? = null

    @SerializedName("cityName")
    @Expose
    var cityName: String? = null
}