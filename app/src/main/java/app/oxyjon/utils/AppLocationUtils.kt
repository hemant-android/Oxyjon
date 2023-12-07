package app.oxyjon.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import app.oxyjon.database.AppSharedPreferences
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*


class AppLocationUtils private constructor(context: Activity) : GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private var mContext: Context
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var location: Location? = null
    private var isLocationConfirmation: Boolean = false

    init {
        mContext = context
    }

    @Synchronized
    fun startFetchingLocation(context: Context, displacement: Float) {
        Companion.displacement = displacement
        mContext = context
        mGoogleApiClient = GoogleApiClient.Builder(mContext)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).build()
        // Set defaults, then update using values stored in the Bundle.
//        mResultReceiver = new AddressResultReceiver(new Handler());
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.interval = INTERVAL
        //        mLocationRequest.setSmallestDisplacement(displacement);
        mLocationRequest!!.fastestInterval = FASTEST_INTERVAL
        showLocationDialog()
    }

    fun stopFetchingLocation() {
        if (mGoogleApiClient!!.isConnected) LocationServices.FusedLocationApi.removeLocationUpdates(
            mGoogleApiClient!!,
            this)
    }

    fun showLocationDialog() {
        val builder: LocationSettingsRequest.Builder =
            LocationSettingsRequest.Builder().addLocationRequest(
                (mLocationRequest)!!)

        //**************************
        builder.setAlwaysShow(true) //this is the key ingredient
        //**************************
        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient!!, builder.build())
        result.setResultCallback { result ->
            val status: Status = result.status
            val state: LocationSettingsStates = result.locationSettingsStates!!
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {}
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {}
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
            }
        }
    }

    public override fun onConnected(bundle: Bundle?) {
        try {
            if ((ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)
            ) {
            } else {
                location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient!!)
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient!!,
                    mLocationRequest!!,
                    this)
                if (location == null) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient!!,
                        mLocationRequest!!,
                        this)
                } else {
                    //mapPrefences.saveMapData(mContext, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
//                    AppPreferencesData.getInstance(mContext).setLatitudeData(String.valueOf(location.getLatitude()));
//                    AppPreferencesData.getInstance(mContext).setLongitudeData(String.valueOf(location.getLongitude()));
                }
                // TODO(: 1/2/17 LocationData of the user.
            }
        } catch (e: Exception) {
            e.getLocalizedMessage()
        }
    }

    public override fun onConnectionSuspended(i: Int) {
        // Logger.d("connection_suspended");
    }

    public override fun onConnectionFailed(connectionResult: ConnectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                if (isLocationConfirmation) {
                    connectionResult.startResolutionForResult((mContext as Activity?)!!,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST)
                }
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (e: Exception) {
                // Log the error
                e.printStackTrace()
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            // Logger.d("LocationData services connection failed with code " + connectionResult.getErrorCode());
            try {
                if (connectionResult.errorCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
                    (mContext as Activity).startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.google.android.gms")))
                }
            } catch (e: Exception) {
            }
        }
    }

    /*
     * This method is calling to onLocationChanged()
     *
     * */
    public override fun onLocationChanged(location: Location) {
//        Toast.makeText(mContext, "" + location, Toast.LENGTH_SHORT).show();
        //Logger.d("onLocationChanged      " + location);
        this.location = location
        if (location.latitude == 0.0 && location.longitude == 0.0) {
            // Logger.d("OnLocation Changed:- With Lat:-0 & Long:-0");
        } else {
            AppSharedPreferences.getInstance(mContext)!!
                .setlatitude(location.latitude.toString())
            AppSharedPreferences.getInstance(mContext)!!
                .setlongitude(location.longitude.toString())
            sendBroadCast()
        }
    }

    fun connectClient(isLocationConfirmation: Boolean) {
        this.isLocationConfirmation = isLocationConfirmation
        mGoogleApiClient!!.connect()
    }

    /*
     * send broadcast while driving
     * */
    private fun sendBroadCast() {
        // Logger.d("broadcast send");
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(Intent(UPDATE_LOCATION_ACTION))
        stopFetchingLocation()
    }

    companion object {
        val UPDATE_LOCATION_ACTION: String = "location_found"
        private val INTERVAL: Long = (2 * 1000).toLong()
        private val CONNECTION_FAILURE_RESOLUTION_REQUEST: Int = 3000
        private val FASTEST_INTERVAL: Long = 3000
        private var appLocationUtils: AppLocationUtils? = null
        private var displacement: Float = 0f
        @Synchronized
        fun getInstance(context: Activity): AppLocationUtils? {
            if (appLocationUtils == null) {
                appLocationUtils = AppLocationUtils(context)
            }
            return appLocationUtils
        }
    }
}