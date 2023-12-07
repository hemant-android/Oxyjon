package app.oxyjon.retrofit.response.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class AllergyData constructor() {
    var isIschecked: Boolean = false

    @SerializedName("allergyId")
    @Expose
    var allergyId: String? = null

    @SerializedName("allergyType")
    @Expose
    var allergyType: String? = null

    @SerializedName("isSelected")
    @Expose
    var isSelected: String? = null
}