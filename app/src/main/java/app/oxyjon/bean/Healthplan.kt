package app.oxyjon.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Healthplan constructor() {
    @SerializedName("action_name")
    @Expose
    var actionName: String? = null

    @SerializedName("button_status")
    @Expose
    var buttonStatus: Boolean? = null
}