package app.oxyjon.bean

data class SaveMedicineResponse(
    val `data`: Data,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val dose: String,
        val end_date: Any,
        val id: Int,
        val medicine: String,
        val medicine_id: Int,
        val profile_id: Int,
        val start_date: String,
        val time_slot: String
    )
}