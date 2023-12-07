package app.oxyjon.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class CallNow {
    @SerializedName("action_name")
    @Expose
    var actionName: String? = null

    @SerializedName("button_status")
    @Expose
    var buttonStatus: Boolean? = null
}