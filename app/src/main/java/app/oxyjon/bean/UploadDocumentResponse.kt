package app.oxyjon.bean

data class UploadDocumentResponse(
    val `data`: Data,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val finalSize: String,
        val profileId: String
    )
}