package app.oxyjon.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class MedicalReports constructor() {
    @SerializedName("document_name")
    @Expose
    var document_name: String? = null

    @SerializedName("filename")
    @Expose
    var filename: String? = null
}