package app.oxyjon.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class CholesterolReport protected constructor(`in`: Parcel?) : Parcelable {
    @SerializedName("hdl")
    @Expose
    var hdl: ArrayList<Hdl>? = null

    @SerializedName("ldl")
    @Expose
    var ldl: ArrayList<Ldl>? = null

    @SerializedName("vldl")
    @Expose
    var vldl: ArrayList<Vldl>? = null

    @SerializedName("triglycerides")
    @Expose
    var triglycerides: ArrayList<Triglyceride>? = null
    public override fun describeContents(): Int {
        return 0
    }

    public override fun writeToParcel(dest: Parcel, flags: Int) {}

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<CholesterolReport?> = object : Parcelable.Creator<CholesterolReport?> {
            public override fun createFromParcel(`in`: Parcel): CholesterolReport? {
                return CholesterolReport(`in`)
            }

            public override fun newArray(size: Int): Array<CholesterolReport?> {
                return arrayOfNulls(size)
            }
        }
    }
}