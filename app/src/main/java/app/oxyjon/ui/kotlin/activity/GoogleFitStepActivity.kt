package app.oxyjon.ui.kotlin.activity

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import app.oxyjon.R
import app.oxyjon.adapter.StepDetailAdapter
import app.oxyjon.bean.StepsCaloriesData
import app.oxyjon.database.AppSharedPreferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import net.danlew.android.joda.JodaTimeAndroid
import java.util.*
import java.util.concurrent.TimeUnit


class GoogleFitStepActivity : AppCompatActivity() {
    private var imgBack: ImageView? = null
    private var rvStepDetail: RecyclerView? = null
    private var adapter: StepDetailAdapter? = null
    var preferences: AppSharedPreferences? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_fit_step)
        preferences = AppSharedPreferences.getInstance(this)
        JodaTimeAndroid.init(this)
        imgBack = findViewById(R.id.imgBack)
        rvStepDetail = findViewById(R.id.rvStepDetail)
        if (hasFitPermission()) {
            readHistoricStepCount()
        } else {
            requestFitnessPermission()
        }
        imgBack!!.setOnClickListener(View.OnClickListener { finish() })
    }

    /**
     * Request Fitness permission of the user. This process will present an account dialog for the
     * user to select their Google account, and then the Fitness permissions dialog.
     */
    private fun requestFitnessPermission() {
        GoogleSignIn.requestPermissions(
            this,
            REQUEST_OAUTH_REQUEST_CODE,
            GoogleSignIn.getLastSignedInAccount(this),
            fitnessSignInOptions)
    }

    /**
     * Verify if the app has permissions to fetch Fitness data.
     *
     * @return true if user has permitted permissions.
     */
    private fun hasFitPermission(): Boolean {
        // Request permission to collect Google Fit data
        val fitnessOptions = fitnessSignInOptions
        return GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this),
            fitnessOptions)
    }// Request access to step count data from Fit history

    /**
     * Specify which data types we would like access to. This is presented to the user as a list
     * of permissions we are seeking to get approved.
     *
     * @return the FitnessOptions containing the data types.
     */
    private val fitnessSignInOptions: FitnessOptions
        private get() =// Request access to step count data from Fit history
            FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED)
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_WRITE)
                .build()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // When the user has accepted the use of Fit data, subscribeStepCount to record data
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                Log.i(TAG, "Fitness permission granted")
                subscribeStepCount()
                readHistoricStepCount() // Read last weeks data
                preferences!!.isFitConnect = true
            }
        } else {
            Log.i(TAG, "Fitness permission denied")
        }
    }

    /**
     * Request a subscription to record step data on the background. This means that the app will
     * record the step count and push it to the Fitness history. Without this, the Google Fit app
     * must be installed to do the recording for us!
     */
    private fun subscribeStepCount() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .subscribe(DataType.TYPE_CALORIES_EXPENDED)
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .subscribe(DataType.TYPE_HEART_RATE_BPM)
    }

    /**
     * Asynchronous task to read the history data. When the task succeeds, it will print out the data.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun readHistoricStepCount() {
        if (!hasFitPermission()) {
            requestFitnessPermission()
            return
        }
        if (preferences!!.weight != null && !TextUtils.isEmpty(preferences!!.weight)) {
            insertWeight(this, DataType.TYPE_WEIGHT, preferences!!.weight!!.toFloat())

            if (preferences!!.heightFeet != null && !TextUtils.isEmpty(preferences!!.heightFeet)) {

                var tInch = (preferences!!.heightFeet!!.toInt() * 12)+preferences!!.heightInch!!.toInt()
                var cMeter = tInch*0.0254

                insertHeight(this, DataType.TYPE_HEIGHT, cMeter.toFloat())
            }
        }

        // Invoke the History API to fetch the data with the query
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .readData(queryFitnessData())
            .addOnSuccessListener { dataReadResponse -> // For the sake of the sample, we'll print the data so we can see what we just
                // added. In general, logging fitness information should be avoided for privacy
                // reasons.
                printData(dataReadResponse)
            }
            .addOnFailureListener { e ->
                Log.e(TAG,
                    "There was a problem reading the historic data.",
                    e)
            }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun printData(dataReadResult: DataReadResponse) {
        val mStepsArr = ArrayList<StepsCaloriesData>()
        if (dataReadResult.buckets.size > 0) {
            for (bucket in dataReadResult.buckets) {
                val dataSets = bucket.dataSets
                for (dataSet in dataSets) {
                    val dataPoint = dataSet.dataPoints
                    for (dp in dataPoint) {
                        val stepsData = StepsCaloriesData()
                        val startDateTime = dp.getStartTime(TimeUnit.MILLISECONDS)
                        val dateTime = getDate(startDateTime, "dd MMM yyyy")
                        val fieldValue = dp.getValue(dp.dataType.fields[0]).toString().toFloat()
                        val roundedValue = String.format("%.0f", fieldValue)
                        val name = dp.dataType.fields[0].name
                        stepsData.dateTime = dateTime
                        stepsData.roundedValue = roundedValue
                        stepsData.name = name
                        mStepsArr.add(stepsData)
                    }
                }
            }
        }
        val mCaloriesArr = ArrayList<StepsCaloriesData>()
        val mStepCountArr = ArrayList<StepsCaloriesData>()
        if (mStepsArr != null && mStepsArr.size > 0) {
            for (matchCal in mStepsArr) {
                if (matchCal.name.equals("calories", ignoreCase = true)) {
                    mCaloriesArr.add(matchCal)
                } else {
                    mStepCountArr.add(matchCal)
                }
            }
        }
        adapter = StepDetailAdapter(this, mStepCountArr, mCaloriesArr)
        rvStepDetail!!.adapter = adapter
    }

    private fun insertHeight(context: Context, dataType: DataType, value: Float) {
        val startTime = Calendar.getInstance().timeInMillis
        val dataSource = DataSource.Builder()
            .setAppPackageName(this)
            .setDataType(dataType)
            .setType(DataSource.TYPE_RAW)
            .build()
        val dataPoint = DataPoint.builder(dataSource)
            .setTimeInterval(startTime, startTime, TimeUnit.MILLISECONDS)
            .setFloatValues(value)
            .build()
        val dataSet = DataSet.builder(dataSource)
            .add(dataPoint)
            .build()
        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .insertData(dataSet)
            .addOnSuccessListener { println("success") }
            .addOnFailureListener { println("failed") }
    }

    private fun insertWeight(context: Context, dataType: DataType, value: Float) {
        val startTime = Calendar.getInstance().timeInMillis
        val dataSource = DataSource.Builder()
            .setAppPackageName(this)
            .setDataType(dataType)
            .setType(DataSource.TYPE_RAW)
            .build()
        val dataPoint = DataPoint.builder(dataSource)
            .setTimeInterval(startTime, startTime, TimeUnit.MILLISECONDS)
            .setFloatValues(value)
            .build()
        val dataSet = DataSet.builder(dataSource)
            .add(dataPoint)
            .build()
        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .insertData(dataSet)
            .addOnSuccessListener { println("success") }
            .addOnFailureListener { println("failed") }
    }

    companion object {
        private const val REQUEST_OAUTH_REQUEST_CODE = 0x1001
        private const val TAG = "GoogleFitStepActivity"

        /**
         * Returns a [DataReadRequest] for all step count changes in the past week.
         */
        fun queryFitnessData(): DataReadRequest {
            val cal = Calendar.getInstance()
            val now = Date()
            cal.time = now
            val endTimeNew = cal.timeInMillis
            val dt = org.joda.time.DateTime().withTimeAtStartOfDay()
            val endTime = dt.millis
            val startTime = dt.minusDays(6).millis
            return DataReadRequest.Builder()
                .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_HEART_RATE_BPM)
                .enableServerQueries()
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTimeNew, TimeUnit.MILLISECONDS)
                .build()
        }

        @RequiresApi(Build.VERSION_CODES.N)
        fun getDate(milliSeconds: Long, dateFormat: String?): String {
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat(dateFormat)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }
    }
}