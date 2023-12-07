package app.oxyjon.bean

data class DocumentReportResponse(
    val `data`: Data,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val diet_plan: ArrayList<DietPlan>,
        val lab_test_report: ArrayList<LabTestReport>,
        val medical_prescription: ArrayList<MedicalPrescription>
    ) {
        data class DietPlan(
            val document_type: String,
            val document_url: String,
            val filename: String,
            val uploaded_date: String
        )

        data class LabTestReport(
            val document_type: String,
            val document_url: String,
            val filename: String,
            val uploaded_date: String
        )

        data class MedicalPrescription(
            val document_type: String,
            val document_url: String,
            val filename: String,
            val uploaded_date: String
        )
    }
}