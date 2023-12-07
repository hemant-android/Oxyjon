package app.oxyjon.retrofit.response.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ChatList constructor() {
    @SerializedName("supportContentId")
    @Expose
    var supportContentId: String? = null

    @SerializedName("supportId")
    @Expose
    var supportId: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("dateTime")
    @Expose
    var dateTime: String? = null

    @SerializedName("customerType")
    @Expose
    var customerType: String? = null
}