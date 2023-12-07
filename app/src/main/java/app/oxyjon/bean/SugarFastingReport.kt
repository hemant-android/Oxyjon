package app.oxyjon.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class SugarFastingReport protected constructor(`in`: Parcel) : Parcelable {
    @SerializedName("date")
    @Expose
    var date: String?

    @SerializedName("sugarLevel")
    @Expose
    var sugarLevel: String?

    init {
        date = `in`.readString()
        sugarLevel = `in`.readString()
    }

    public override fun describeContents(): Int {
        return 0
    }

    public override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(date)
        dest.writeString(sugarLevel)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SugarFastingReport?> = object :
            Parcelable.Creator<SugarFastingReport?> {
            public override fun createFromParcel(`in`: Parcel): SugarFastingReport? {
                return SugarFastingReport(`in`)
            }

            public override fun newArray(size: Int): Array<SugarFastingReport?> {
                return arrayOfNulls(size)
            }
        }
    }
}