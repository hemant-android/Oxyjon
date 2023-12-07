package app.oxyjon.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class PulseReport protected constructor(`in`: Parcel) : Parcelable {
    @SerializedName("date")
    @Expose
    var date: String?

    @SerializedName("pulseRate")
    @Expose
    var pulseRate: String?

    init {
        date = `in`.readString()
        pulseRate = `in`.readString()
    }

    public override fun describeContents(): Int {
        return 0
    }

    public override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(date)
        dest.writeString(pulseRate)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PulseReport?> = object : Parcelable.Creator<PulseReport?> {
            public override fun createFromParcel(`in`: Parcel): PulseReport? {
                return PulseReport(`in`)
            }

            public override fun newArray(size: Int): Array<PulseReport?> {
                return arrayOfNulls(size)
            }
        }
    }
}