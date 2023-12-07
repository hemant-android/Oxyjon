package app.oxyjon.utils

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.DataSourcesRequest
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import java.util.*
import java.util.concurrent.TimeUnit


object GoogleFitUtil {
    private val TAG: String = "GoogleFitUtil"

    /**
     * We get all the device details
     *
     * @param dataType
     * @param googleSignInAccount
     * @param activity
     */
    fun getDeviceDetails(
        dataType: DataType?,
        googleSignInAccount: GoogleSignInAccount?,
        activity: AppCompatActivity?
    ) {
        Fitness.getSensorsClient((activity)!!, (googleSignInAccount)!!)
            .findDataSources(DataSourcesRequest.Builder()
                .setDataTypes(dataType)
                .setDataSourceTypes(DataSource.TYPE_RAW).build())
            .addOnSuccessListener { dataSources ->
                Log.e(TAG, "getSensorClient " + dataSources.size)
                for (i in dataSources.indices) {
                    Log.e(TAG, "getSensorClient " + dataSources[i].device!!.manufacturer)
                }
            }
            .addOnFailureListener { e -> Log.e(TAG, "getSensorClient " + e.localizedMessage) }
    }

    /**
     * We get all the personal detail
     */
    fun readPersonalDetails() {
        val dataReadRequest: DataReadRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_HEIGHT)
            .read(DataType.TYPE_WEIGHT)
            .setLimit(1)
            .setTimeRange(1, Calendar.getInstance().timeInMillis, TimeUnit.MILLISECONDS)
            .build()
    }

    /**
     * We can unsubscribe the specific DataType
     *
     * @param dataType
     * @param googleSignInAccount
     * @param activity
     */
    fun unScribe(
        dataType: DataType?,
        googleSignInAccount: GoogleSignInAccount?,
        activity: AppCompatActivity?
    ) {
        Fitness.getRecordingClient((activity)!!, (googleSignInAccount)!!)
            .unsubscribe((dataType)!!)
            .addOnSuccessListener { Log.e(TAG, "unsubscribed ") }
            .addOnFailureListener(OnFailureListener { e: Exception ->
                Log.e(TAG,
                    "Failure " + e.localizedMessage)
            })
    }
}