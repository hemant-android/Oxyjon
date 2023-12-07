package app.oxyjon.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class CholesterolData : Parcelable {
    @SerializedName("date")
    @Expose
    var date: String?

    @SerializedName("hdl")
    @Expose
    var hdl: String?

    @SerializedName("ldl")
    @Expose
    var ldl: String?

    @SerializedName("vldl")
    @Expose
    var vldl: String?

    @SerializedName("triglycerides")
    @Expose
    var triglycerides: String?

    @SerializedName("totalCholesterol")
    @Expose
    var totalCholesterol: String?

    constructor(
        date: String?,
        hdl: String?,
        ldl: String?,
        vldl: String?,
        triglycerides: String?,
        totalCholesterol: String?
    ) {
        this.date = date
        this.hdl = hdl
        this.ldl = ldl
        this.vldl = vldl
        this.triglycerides = triglycerides
        this.totalCholesterol = totalCholesterol
    }

    protected constructor(`in`: Parcel) {
        date = `in`.readString()
        hdl = `in`.readString()
        ldl = `in`.readString()
        vldl = `in`.readString()
        triglycerides = `in`.readString()
        totalCholesterol = `in`.readString()
    }

    public override fun describeContents(): Int {
        return 0
    }

    public override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(date)
        dest.writeString(hdl)
        dest.writeString(ldl)
        dest.writeString(vldl)
        dest.writeString(triglycerides)
        dest.writeString(totalCholesterol)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<CholesterolData?> = object : Parcelable.Creator<CholesterolData?> {
            public override fun createFromParcel(`in`: Parcel): CholesterolData? {
                return CholesterolData(`in`)
            }

            public override fun newArray(size: Int): Array<CholesterolData?> {
                return arrayOfNulls(size)
            }
        }
    }
}