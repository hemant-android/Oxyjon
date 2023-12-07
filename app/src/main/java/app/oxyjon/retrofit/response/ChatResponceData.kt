package app.oxyjon.retrofit.response

import app.oxyjon.retrofit.response.data.ChatList
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ChatResponceData {
    @SerializedName("errorCode")
    @Expose
    var errorCode: String? = null

    @SerializedName("errorMsg")
    @Expose
    var errorMsg: String? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<ChatList>? = null
}