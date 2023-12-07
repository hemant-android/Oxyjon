package app.oxyjon.bean

data class BPResponse(
    val `data`: ArrayList<Data>,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val ampm: String,
        val date: String,
        val diastolic: String,
        val id: String,
        val minute: String,
        val profile_id: String,
        val systolic: String,
        val time: String,
        val pulse: String
    )
}