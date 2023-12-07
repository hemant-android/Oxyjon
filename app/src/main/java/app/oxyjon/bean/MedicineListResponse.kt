package app.oxyjon.bean

import java.io.Serializable

data class MedicineListResponse(
    val errorCode: String,
    val errorMsg: String,
    val medicinelist: Medicinelist,
    val medicinelist_action: MedicinelistAction
) : Serializable {
    data class Medicinelist(
        val current_page: Int,
        val `data`: ArrayList<Data>,
        val from: Int,
        val last_page: Int,
        val next_page_url: String,
        val path: String,
        val per_page: Int,
        val prev_page_url: Any,
        val to: Int,
        val total: Int
    ) : Serializable {
        data class Data(
            val created_at: String,
            val id: Int,
            val medicine_category: String,
            val medicine_form: String,
            val medicine_name: String
        ) : Serializable
    }

    data class MedicinelistAction(
        val last_update: String,
        val last_update_completion_status: String,
        val sync_id: String,
        val update_data: String
    ) : Serializable
}