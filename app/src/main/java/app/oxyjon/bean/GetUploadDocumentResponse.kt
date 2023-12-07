package app.oxyjon.bean

data class GetUploadDocumentResponse(
    val `data`: ArrayList<Data>,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val document_type: String,
        val document_url: String,
        val filename: String,
        val uploaded_date: String
    )
}