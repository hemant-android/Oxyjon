package app.oxyjon.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class TSHReport protected constructor(`in`: Parcel) : Parcelable {
    @SerializedName("date")
    @Expose
    var date: String?

    @SerializedName("tsh")
    @Expose
    var tsh: String?

    init {
        date = `in`.readString()
        tsh = `in`.readString()
    }

    public override fun describeContents(): Int {
        return 0
    }

    public override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(date)
        dest.writeString(tsh)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TSHReport?> = object : Parcelable.Creator<TSHReport?> {
            public override fun createFromParcel(`in`: Parcel): TSHReport? {
                return TSHReport(`in`)
            }

            public override fun newArray(size: Int): Array<TSHReport?> {
                return arrayOfNulls(size)
            }
        }
    }
}