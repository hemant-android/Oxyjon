package app.oxyjon.bean

import app.oxyjon.retrofit.response.data.DoctorSpecialitylist
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TreatmentProvider constructor() {
    @SerializedName("tprovider_id")
    @Expose
    var tproviderId: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("mobileNumber")
    @Expose
    var mobilenumber: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null
    var doctorSpecialitylists: ArrayList<DoctorSpecialitylist>? = null

    @SerializedName("doctorRecommend")
    @Expose
    var recommendedoctor: String? = null

    @SerializedName("doctorSpeciality")
    @Expose
    var specialistid: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null
    var specialist: String? = null

    @SerializedName("specialityType")
    @Expose
    var specialityType: String? = null
}