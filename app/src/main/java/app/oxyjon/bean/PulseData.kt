package app.oxyjon.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PulseData : Parcelable {
    @SerializedName("time")
    @Expose
    var hour: String?

    @SerializedName("minute")
    @Expose
    var minute: String?

    @SerializedName("ampm")
    @Expose
    var amh: String?

    @SerializedName("pulseRate")
    @Expose
    var pulserate: String?

    @SerializedName("date")
    @Expose
    var date: String?

    constructor(hour: String?, minute: String?, amh: String?, pulserate: String?, date: String?) {
        this.hour = hour
        this.minute = minute
        this.amh = amh
        this.pulserate = pulserate
        this.date = date
    }

    protected constructor(`in`: Parcel) {
        hour = `in`.readString()
        minute = `in`.readString()
        amh = `in`.readString()
        pulserate = `in`.readString()
        date = `in`.readString()
    }

    public override fun describeContents(): Int {
        return 0
    }

    public override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(hour)
        dest.writeString(minute)
        dest.writeString(amh)
        dest.writeString(pulserate)
        dest.writeString(date)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PulseData?> = object : Parcelable.Creator<PulseData?> {
            public override fun createFromParcel(`in`: Parcel): PulseData? {
                return PulseData(`in`)
            }

            public override fun newArray(size: Int): Array<PulseData?> {
                return arrayOfNulls(size)
            }
        }
    }
}