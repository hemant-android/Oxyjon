package app.oxyjon.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BloodPressure protected constructor(`in`: Parcel?) : Parcelable {
    @SerializedName("systolic")
    @Expose
    var systolic: ArrayList<Systolic>? = null

    @SerializedName("diastolic")
    @Expose
    var diastolic: ArrayList<Diastolic>? = null
    public override fun describeContents(): Int {
        return 0
    }

    public override fun writeToParcel(dest: Parcel, flags: Int) {

    }

    companion object {
        val CREATOR: Parcelable.Creator<BloodPressure?> = object : Parcelable.Creator<BloodPressure?> {
            public override fun createFromParcel(`in`: Parcel): BloodPressure? {
                return BloodPressure(`in`)
            }

            public override fun newArray(size: Int): Array<BloodPressure?> {
                return arrayOfNulls(size)
            }
        }
    }

    init {

    }

    object CREATOR : Parcelable.Creator<BloodPressure> {
        override fun createFromParcel(parcel: Parcel): BloodPressure {
            return BloodPressure(parcel)
        }

        override fun newArray(size: Int): Array<BloodPressure?> {
            return arrayOfNulls(size)
        }
    }
}