package app.oxyjon.retrofit.response.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class DoctorSpecialitylist constructor() {
    @SerializedName("specialityId")
    @Expose
    var specialityId: String? = null

    @SerializedName("specialityType")
    @Expose
    var specialityType: String? = null
}