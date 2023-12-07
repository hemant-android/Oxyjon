package app.oxyjon.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class UploadDocumentsData constructor(
    @field:Expose @field:SerializedName("filename") var image: String,
    @field:Expose @field:SerializedName(
        "documentName") var fileName: String,
    @field:Expose @field:SerializedName("documentType") var documentType: String, var type: String
) {

    @SerializedName("documentId")
    @Expose
    var documentId: String? = null

}