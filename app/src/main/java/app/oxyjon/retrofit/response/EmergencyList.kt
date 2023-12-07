package app.oxyjon.retrofit.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class EmergencyList protected constructor(`in`: Parcel) : Parcelable {
    @SerializedName("name")
    @Expose
    var name: String?

    @SerializedName("address")
    @Expose
    var address: String?

    @SerializedName("latitude")
    @Expose
    var latitude: String?

    @SerializedName("longitude")
    @Expose
    var longitude: String?

    @SerializedName("phone")
    @Expose
    var phone: String?

    @SerializedName("isBloodbank")
    @Expose
    var isBloodbank: String?

    @SerializedName("isHospital")
    @Expose
    var isHospital: String?

    @SerializedName("isChemist")
    @Expose
    var isChemist: String?

    @SerializedName("isAmbulance")
    @Expose
    var isAmbulance: String?

    @SerializedName("chemistClosingTime")
    @Expose
    var chemistClosingTime: String?

    @SerializedName("km")
    @Expose
    var km: String?

    init {
        name = `in`.readString()
        address = `in`.readString()
        latitude = `in`.readString()
        longitude = `in`.readString()
        phone = `in`.readString()
        isBloodbank = `in`.readString()
        isHospital = `in`.readString()
        isChemist = `in`.readString()
        isAmbulance = `in`.readString()
        chemistClosingTime = `in`.readString()
        km = `in`.readString()
    }

    public override fun describeContents(): Int {
        return 0
    }

    public override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(address)
        dest.writeString(latitude)
        dest.writeString(longitude)
        dest.writeString(phone)
        dest.writeString(isBloodbank)
        dest.writeString(isHospital)
        dest.writeString(isChemist)
        dest.writeString(isAmbulance)
        dest.writeString(chemistClosingTime)
        dest.writeString(km)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<EmergencyList?> = object :
            Parcelable.Creator<EmergencyList?> {
            override fun createFromParcel(`in`: Parcel): EmergencyList? {
                return EmergencyList(`in`)
            }

            override fun newArray(size: Int): Array<EmergencyList?> {
                return arrayOfNulls(size)
            }
        }
    }
}