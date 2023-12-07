package app.oxyjon.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DashboardList constructor() {
    @SerializedName("bloodPressure")
    @Expose
    var bloodPressure: ArrayList<BloodPressure>? = null

    @SerializedName("sugarFastingReport")
    @Expose
    var sugarFastingReport: ArrayList<SugarFastingReport>? = null

    @SerializedName("sugarPPReport")
    @Expose
    var sugarPPReport: ArrayList<SugarPPReport>? = null

    @SerializedName("sugarRandomReport")
    @Expose
    var sugarRandomReport: ArrayList<SugarRandomReport>? = null

    @SerializedName("sugarBeforeLunch")
    @Expose
    var sugarBeforeLunch: ArrayList<SugarFastingReport>? = null

    @SerializedName("sugarAfterLunch")
    @Expose
    var sugarAfterLunch: ArrayList<SugarFastingReport>? = null

    @SerializedName("sugarBeforeDinner")
    @Expose
    var sugarBeforeDinner: ArrayList<SugarFastingReport>? = null

    @SerializedName("sugarAfterDinner")
    @Expose
    var sugarAfterDinner: ArrayList<SugarFastingReport>? = null

    @SerializedName("sugarMidnight")
    @Expose
    var sugarMidnight: ArrayList<SugarFastingReport>? = null

    @SerializedName("pulseReport")
    @Expose
    var pulseReport: ArrayList<PulseReport>? = null

    @SerializedName("weightReport")
    @Expose
    var weightReport: ArrayList<WeightReport>? = null

    @SerializedName("cholesterolReport")
    @Expose
    var cholesterolReport: ArrayList<CholesterolReport>? = null

    @SerializedName("PSAReport")
    @Expose
    private var pSAReport: ArrayList<PSAReport>? = null

    @SerializedName("HBA1CReport")
    @Expose
    private var hBA1CReport: ArrayList<HBA1CReport>? = null

    @SerializedName("TSHReport")
    @Expose
    private var tSHReport: ArrayList<TSHReport>? = null

    @SerializedName("reportCard")
    @Expose
    var reportCard: ReportCard? = null

    @SerializedName("medicalReports")
    @Expose
    var medicalReports: ArrayList<MedicalReports>? = null

    @SerializedName("callnow")
    @Expose
    var callNow: CallNow? = null

    @SerializedName("healthplan")
    @Expose
    var healthplan: Healthplan? = null

    @SerializedName("bookbloodtest")
    @Expose
    var bookbloodtest: BookBloodTest? = null

    @SerializedName("newsfeed")
    @Expose
    var newsfeed: ArrayList<NewsFeed>? = null

    @SerializedName("is_membershipenable")
    @Expose
    var isMemberShip: String? = null

    @SerializedName("member_ship_label")
    @Expose
    var membeShipLabel: String? = null
    fun getpSAReport(): ArrayList<PSAReport>? {
        return pSAReport
    }

    fun setpSAReport(pSAReport: ArrayList<PSAReport>?) {
        this.pSAReport = pSAReport
    }

    fun gethBA1CReport(): ArrayList<HBA1CReport>? {
        return hBA1CReport
    }

    fun sethBA1CReport(hBA1CReport: ArrayList<HBA1CReport>?) {
        this.hBA1CReport = hBA1CReport
    }

    fun gettSHReport(): ArrayList<TSHReport>? {
        return tSHReport
    }

    fun settSHReport(tSHReport: ArrayList<TSHReport>?) {
        this.tSHReport = tSHReport
    }
}