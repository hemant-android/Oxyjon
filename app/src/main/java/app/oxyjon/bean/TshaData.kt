package app.oxyjon.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class TshaData : Parcelable {
    @SerializedName("date")
    @Expose
    var date: String?

    @SerializedName("tsh")
    @Expose
    var entrylevel: String?

    constructor(date: String?, entrylevel: String?) {
        this.date = date
        this.entrylevel = entrylevel
    }

    protected constructor(`in`: Parcel) {
        date = `in`.readString()
        entrylevel = `in`.readString()
    }

    public override fun describeContents(): Int {
        return 0
    }

    public override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(date)
        dest.writeString(entrylevel)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TshaData?> = object : Parcelable.Creator<TshaData?> {
            public override fun createFromParcel(`in`: Parcel): TshaData? {
                return TshaData(`in`)
            }

            public override fun newArray(size: Int): Array<TshaData?> {
                return arrayOfNulls(size)
            }
        }
    }
}