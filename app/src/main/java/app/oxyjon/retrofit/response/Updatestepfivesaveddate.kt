package app.oxyjon.retrofit.response

import app.oxyjon.bean.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Updatestepfivesaveddate constructor() {
    @SerializedName("profileId")
    @Expose
    var profileId: String? = null

    @SerializedName("bloodPressure")
    @Expose
    var bloodPressure: ArrayList<BloodPressureData>? = null

    @SerializedName("sugar")
    @Expose
    var sugar: ArrayList<SugerData>? = null

    @SerializedName("pulse")
    @Expose
    var pulse: ArrayList<PulseData>? = null

    @SerializedName("weight")
    @Expose
    var weight: ArrayList<WeightData>? = null

    @SerializedName("cholesterol")
    @Expose
    var cholesterol: ArrayList<CholesterolData>? = null

    @SerializedName("PSA")
    @Expose
    private var pSA: ArrayList<PsaData>? = null

    @SerializedName("HBA1C")
    @Expose
    private var hBA1C: ArrayList<HbacData>? = null

    @SerializedName("TSH")
    @Expose
    private var tSH: ArrayList<TshaData>? = null
    fun getpSA(): ArrayList<PsaData>? {
        return pSA
    }

    fun setpSA(pSA: ArrayList<PsaData>?) {
        this.pSA = pSA
    }

    fun gethBA1C(): ArrayList<HbacData>? {
        return hBA1C
    }

    fun sethBA1C(hBA1C: ArrayList<HbacData>?) {
        this.hBA1C = hBA1C
    }

    fun gettSH(): ArrayList<TshaData>? {
        return tSH
    }

    fun settSH(tSH: ArrayList<TshaData>?) {
        this.tSH = tSH
    }
}