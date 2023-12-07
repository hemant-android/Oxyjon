package app.oxyjon.retrofit.response

import app.oxyjon.retrofit.response.data.FilteredList
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class FilteredCityListResponce constructor() {
    @SerializedName("errorCode")
    @Expose
    var errorCode: String? = null

    @SerializedName("errorMsg")
    @Expose
    var errorMsg: String? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<FilteredList>? = null
}