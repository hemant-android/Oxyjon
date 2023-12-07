package app.oxyjon.bean

import java.io.Serializable

data class PlanDetailResponse(
    val `data`: ArrayList<Data>,
    val errorCode: String,
    val errorMsg: String
) : Serializable {
    data class Data(
        val benefits: ArrayList<Benefit>,
        val consultation: ArrayList<Consultation>,
        val id: String,
        val plan_details: String,
        val plan_title: String,
        val price: String
    ) : Serializable {
        data class Benefit(
            val isavailable: String,
            val name: String
        ) : Serializable

        data class Consultation(
            val count: String,
            val name: String
        ) : Serializable
    }
}