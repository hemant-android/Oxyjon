package app.oxyjon.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class HBA1CReport protected constructor(`in`: Parcel) : Parcelable {
    @SerializedName("date")
    @Expose
    var date: String?

    @SerializedName("hbac")
    @Expose
    var hbac: String?

    init {
        date = `in`.readString()
        hbac = `in`.readString()
    }

    public override fun describeContents(): Int {
        return 0
    }

    public override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(date)
        dest.writeString(hbac)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<HBA1CReport?> = object : Parcelable.Creator<HBA1CReport?> {
            public override fun createFromParcel(`in`: Parcel): HBA1CReport? {
                return HBA1CReport(`in`)
            }

            public override fun newArray(size: Int): Array<HBA1CReport?> {
                return arrayOfNulls(size)
            }
        }
    }
}