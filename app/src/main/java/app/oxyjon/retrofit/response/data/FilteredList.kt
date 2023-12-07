package app.oxyjon.retrofit.response.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class FilteredList constructor() {
    @SerializedName("cityId")
    @Expose
    var cityId: String? = null

    @SerializedName("cityName")
    @Expose
    var cityName: String? = null
}