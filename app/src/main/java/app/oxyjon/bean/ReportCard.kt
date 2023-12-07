package app.oxyjon.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ReportCard constructor() {
    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("bmi")
    @Expose
    var bmi: String? = null

    @SerializedName("body_type")
    @Expose
    var bodyType: String? = null

    @SerializedName("activity_level")
    @Expose
    var activityLevel: String? = null

    @SerializedName("calories_intake")
    @Expose
    var caloriesIntake: String? = null

    @SerializedName("action")
    @Expose
    var action: String? = null
}