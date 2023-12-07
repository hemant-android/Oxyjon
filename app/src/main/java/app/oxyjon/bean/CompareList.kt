package app.oxyjon.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class CompareList constructor() {
    @SerializedName("labId")
    @Expose
    var labId: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("price")
    @Expose
    var price: String? = null

    @SerializedName("lab_test")
    @Expose
    var labTest: String? = null

    @SerializedName("uniqueTests")
    @Expose
    var uniqueTests: String? = null

    @SerializedName("commonTests")
    @Expose
    var commonTests: String? = null
}