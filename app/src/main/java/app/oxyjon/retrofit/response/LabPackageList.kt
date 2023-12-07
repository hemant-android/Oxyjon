package app.oxyjon.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class LabPackageList constructor() {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("package_name")
    @Expose
    var packageName: String? = null
}