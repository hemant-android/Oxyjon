package app.oxyjon.bean
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import android.os.Parcelable
import android.os.Parcel
import android.os.Parcelable.Creator

class BloodPressureData : Parcelable {
    @SerializedName("date")
    @Expose
    var date: String?

    @SerializedName("systolic")
    @Expose
    var systolic: String?

    @SerializedName("diastolic")
    @Expose
    var diastolic: String?

    @SerializedName("time")
    @Expose
    var hour: String?

    @SerializedName("minute")
    @Expose
    var minute: String?

    @SerializedName("ampm")
    @Expose
    var am: String?

    constructor(
        date: String?,
        systolic: String?,
        diastolic: String?,
        hour: String?,
        minute: String?,
        am: String?
    ) {
        this.date = date
        this.systolic = systolic
        this.diastolic = diastolic
        this.hour = hour
        this.minute = minute
        this.am = am
    }

    protected constructor(`in`: Parcel) {
        date = `in`.readString()
        systolic = `in`.readString()
        diastolic = `in`.readString()
        hour = `in`.readString()
        minute = `in`.readString()
        am = `in`.readString()
    }

    public override fun describeContents(): Int {
        return 0
    }

    public override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(date)
        dest.writeString(systolic)
        dest.writeString(diastolic)
        dest.writeString(hour)
        dest.writeString(minute)
        dest.writeString(am)
    }

    companion object {
        @JvmField
        val CREATOR: Creator<BloodPressureData?> = object : Creator<BloodPressureData?> {
            public override fun createFromParcel(`in`: Parcel): BloodPressureData? {
                return BloodPressureData(`in`)
            }

            public override fun newArray(size: Int): Array<BloodPressureData?> {
                return arrayOfNulls(size)
            }
        }
    }
}