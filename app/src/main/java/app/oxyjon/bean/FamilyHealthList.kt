package app.oxyjon.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FamilyHealthList constructor() {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("relationship")
    @Expose
    var relationship: String? = null

    @SerializedName("hConditionId")
    @Expose
    private var hConditionId: String? = null

    @SerializedName("age")
    @Expose
    var age: String? = null

    @SerializedName("hType")
    @Expose
    private var hType: String? = null
    fun gethType(): String? {
        return hType
    }

    fun sethType(hType: String?) {
        this.hType = hType
    }

    fun gethConditionId(): String? {
        return hConditionId
    }

    fun sethConditionId(hConditionId: String?) {
        this.hConditionId = hConditionId
    }
}