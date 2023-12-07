package app.oxyjon.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WeightData : Parcelable {
    @SerializedName("date")
    @Expose
    var date: String?

    @SerializedName("weight")
    @Expose
    var enterlevel: String?

    @SerializedName("unit")
    @Expose
    var kgs: String?

    constructor(date: String?, entrylevel: String?, kgs: String?) {
        this.date = date
        enterlevel = entrylevel
        this.kgs = kgs
    }

    protected constructor(`in`: Parcel) {
        date = `in`.readString()
        enterlevel = `in`.readString()
        kgs = `in`.readString()
    }

    public override fun describeContents(): Int {
        return 0
    }

    public override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(date)
        dest.writeString(enterlevel)
        dest.writeString(kgs)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<WeightData?> = object : Parcelable.Creator<WeightData?> {
            public override fun createFromParcel(`in`: Parcel): WeightData? {
                return WeightData(`in`)
            }

            public override fun newArray(size: Int): Array<WeightData?> {
                return arrayOfNulls(size)
            }
        }
    }
}