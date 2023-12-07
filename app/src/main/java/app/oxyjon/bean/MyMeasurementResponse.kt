package app.oxyjon.bean

data class MyMeasurementResponse(
    val `data`: ArrayList<Data>,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val measurement_quantity: String,
        val measurement_unit: String
    )
}