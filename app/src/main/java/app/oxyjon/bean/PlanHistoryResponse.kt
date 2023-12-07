package app.oxyjon.bean

import java.io.Serializable

data class PlanHistoryResponse(
    val errorCode: String,
    val errorMsg: String,
    val myhealthplan: ArrayList<Myhealthplan>
) : Serializable {
    data class Myhealthplan(
        val date: String,
        val id: Int,
        val plan_details: PlanDetails,
        val plan_name: String
    ) : Serializable {
        data class PlanDetails(
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
}