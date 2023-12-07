package app.oxyjon.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class NewsFeed constructor() {
    @SerializedName("contenttype")
    @Expose
    var contentType: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("heading")
    @Expose
    var heading: String? = null

    @SerializedName("details")
    @Expose
    var details: String? = null

    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("detail_url")
    @Expose
    var detailUrl: String? = null
}