package app.oxyjon.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class PSAReport protected constructor(`in`: Parcel) : Parcelable {
    @SerializedName("date")
    @Expose
    var date: String?

    @SerializedName("psa")
    @Expose
    var psa: String?

    init {
        date = `in`.readString()
        psa = `in`.readString()
    }

    public override fun describeContents(): Int {
        return 0
    }

    public override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(date)
        dest.writeString(psa)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PSAReport?> = object : Parcelable.Creator<PSAReport?> {
            public override fun createFromParcel(`in`: Parcel): PSAReport? {
                return PSAReport(`in`)
            }

            public override fun newArray(size: Int): Array<PSAReport?> {
                return arrayOfNulls(size)
            }
        }
    }
}