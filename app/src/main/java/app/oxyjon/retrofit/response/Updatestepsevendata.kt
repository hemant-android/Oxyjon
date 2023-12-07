package app.oxyjon.retrofit.response

import app.oxyjon.bean.UploadDocumentsData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Updatestepsevendata constructor() {
    @SerializedName("profileId")
    @Expose
    var profileId: String? = null

    @SerializedName("documentData")
    @Expose
    var documentData: ArrayList<UploadDocumentsData>? = null
}