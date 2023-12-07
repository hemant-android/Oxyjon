package app.oxyjon.bean

import java.io.Serializable

data class MyMedicineResponse(
    val `data`: ArrayList<Data>,
    val errorCode: String,
    val errorMsg: String,
) : Serializable {
    data class Data(
        val medicineitems: ArrayList<Medicineitem>,
        val name: String,
    ) : Serializable {
        data class Medicineitem(
            val dose: String,
            val end_date: String,
            val id: Int,
            val medicine: String,
            val medicine_id: Int,
            val profile_id: Int,
            val start_date: String,
            val time_slot: String,
        ) : Serializable
    }
}