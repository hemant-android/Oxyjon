package app.oxyjon.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class AgeListdata constructor() {
    @SerializedName("ageId")
    @Expose
    var ageId: String? = null

    @SerializedName("age")
    @Expose
    var age: String? = null
}