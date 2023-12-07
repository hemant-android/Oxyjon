package app.oxyjon.ui.kotlin.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityStepGoalBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.retrofit.response.CommonResponse
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.utils.CheckConnection
import app.oxyjon.utils.FunctionHelper
import app.oxyjon.utils.PermissionUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper
import com.moengage.core.analytics.MoEAnalyticsHelper.setUserAttribute
import retrofit2.Response

class StepGoalActivity : BaseActivity(), IApiCallback {
    lateinit var binding: ActivityStepGoalBinding
    var preferences: AppSharedPreferences? = null
    var steps = 0

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val REQUEST_OAUTH_REQUEST_CODE = 0x1001
    private val TAG = "StepGoalActivity.class"

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStepGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppSharedPreferences.getInstance(this)
        MainApplication.currentActivity = this

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        if (preferences!!.isFitConnect) {
            binding.llConnected.visibility = View.VISIBLE
            binding.imgConnect.visibility = View.GONE
            binding.tvSetGoalDisable.visibility = View.GONE
            binding.tvSetGoal.visibility = View.VISIBLE

        } else {
            binding.llConnected.visibility = View.GONE
            binding.imgConnect.visibility = View.VISIBLE

            binding.tvSetGoalDisable.visibility = View.VISIBLE
            binding.tvSetGoal.visibility = View.GONE
        }

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.imgMinius.setOnClickListener {
            if (!TextUtils.isEmpty(binding.edtSteps.text.toString().trim { it <= ' ' })) {
                steps = binding.edtSteps.text.toString().trim { it <= ' ' }.toInt()
                steps -= 1
                if (steps > 0) {
                    binding.edtSteps.setText("" + steps)
                }
            }
        }

        binding.imgPlus.setOnClickListener {
            if (!TextUtils.isEmpty(binding.edtSteps.text.toString().trim { it <= ' ' })) {
                steps = binding.edtSteps.text.toString().trim { it <= ' ' }.toInt()
                steps += 1
                binding.edtSteps.setText("" + steps)
            }
        }

        binding.imgConnect.setOnClickListener {
            requestPermissions(true)

            val properties = Properties()
            properties.addAttribute("isCLick", true)
            MoEAnalyticsHelper.trackEvent(this, "ClickGoogleFit", properties)
        }

        binding.tvSetGoal.setOnClickListener {
            val properties = Properties()
            properties.addAttribute("isCLick", true)
            properties.addAttribute("googleFitConnected", true)
            properties.addAttribute("stepGoal", binding.edtSteps.text.toString().trim())
            MoEAnalyticsHelper.trackEvent(this, "SetStepGoal", properties)

            setUserAttribute(
                this@StepGoalActivity,
                "stepGoal",
                binding.edtSteps.text.toString().trim()
            )

            if (CheckConnection.isConnection(this)) {
                if (!TextUtils.isEmpty(
                        binding.edtSteps.text.toString()
                            .trim()
                    ) && binding.edtSteps.text.toString().trim() != "0"
                ) {
                    FunctionHelper.disable_user_Intration(
                        this,
                        resources.getString(R.string.loading)
                    )
                    ApiCall.instance.setStepGoal(
                        preferences!!.getprofileid(),
                        binding.edtSteps.text.toString().trim(),
                        this
                    )
                } else {
                    Toast.makeText(this, "Please enter daily steps", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "setStepGoal") {
            val response = data as Response<CommonResponse>
            if (response.isSuccessful) {
                if (response.body()!!.errorCode == "1") {
                    preferences!!.isStepCountDialogPopup(true)
                    val intent = Intent(this@StepGoalActivity, StepsRecordedActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                Log.i(TAG, "Fitness permission granted")
                binding.llConnected.visibility = View.VISIBLE
                binding.imgConnect.visibility = View.GONE

                binding.tvSetGoalDisable.visibility = View.GONE
                binding.tvSetGoal.visibility = View.VISIBLE

                preferences!!.isFitConnect = true
            }
        } else if (requestCode == 101) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissions(true)
            }
        }
    }

    private fun requestPermissions(isBack: Boolean) {
        // below line is use to request
        // permission in the current activity.
        Dexter.withContext(MainApplication.currentActivity) // below line is use to request the number of
            // permissions which are required in our app.
            .withPermissions(
                Manifest.permission.ACTIVITY_RECOGNITION,
                Manifest.permission.BODY_SENSORS,
                Manifest.permission.ACCESS_COARSE_LOCATION,  // below is the list of permissions
                Manifest.permission.ACCESS_FINE_LOCATION
            ) // after adding permissions we are
            // calling an with listener method.
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            if (isBack) {
                                getGoogleFitDataWithSignIn()
                            } else {
                                getGoogleFitData()
                            }
                        }
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        try {
                            if (isLocationEnabled()) {
                                showSettingsDialog()
                            } else {
                                /*Toast.makeText(
                                    this@StepGoalActivity,
                                    "Please turn on location",
                                    Toast.LENGTH_LONG
                                ).show()*/

                                val builder = AlertDialog.Builder(MainApplication.currentActivity)
                                // below line is the title
                                // for our alert dialog.
                                builder.setTitle("Enable Location")
                                // below line is our message for our dialog
                                builder.setMessage("You need to enable location.")
                                builder.setPositiveButton(
                                    "GOTO SETTINGS"
                                ) { dialog, which -> // this method is called on click on positive
                                    // button and on clicking shit button we
                                    // are redirecting our user from our app to the
                                    // settings page of our app.
                                    dialog.cancel()
                                    // below is the intent from which we
                                    // are redirecting our user.
                                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                    startActivityForResult(intent, 101)
                                }
                                builder.setNegativeButton(
                                    "Cancel"
                                ) { dialog, which -> // this method is called when
                                    // user click on negative button.
                                    dialog.cancel()
                                }
                                // below line is used
                                // to display our dialog
                                builder.show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest?>?,
                    permissionToken: PermissionToken,
                ) {
                    // this method is called when user grants some permission and denies some of them.
                    permissionToken.continuePermissionRequest()
                }
            }).check()
    }

    private fun showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        val builder = AlertDialog.Builder(MainApplication.currentActivity)
        // below line is the title
        // for our alert dialog.
        builder.setTitle("Need Permission")
        // below line is our message for our dialog
        builder.setMessage("You need to give permission for step counter. Please go to settings-> Permission-> Physical activity & Body Sensor & Location and click allow.")
        builder.setPositiveButton(
            "GOTO SETTINGS"
        ) { dialog, which -> // this method is called on click on positive
            // button and on clicking shit button we
            // are redirecting our user from our app to the
            // settings page of our app.
            dialog.cancel()
            // below is the intent from which we
            // are redirecting our user.
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", MainApplication.currentActivity!!.packageName, null)
            intent.data = uri
            startActivityForResult(intent, 101)
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> // this method is called when
            // user click on negative button.
            dialog.cancel()
        }
        // below line is used
        // to display our dialog
        builder.show()
    }

    private fun getGoogleFitData() {
        /*if (checkPermission()) {
            if (hasFitPermission()) {
                binding.imgConnect.visibility = View.GONE
                binding.llConnected.visibility = View.VISIBLE
                binding.tvSetGoalDisable.visibility = View.GONE
                binding.tvSetGoal.visibility = View.VISIBLE
                preferences!!.isFitConnect = true

                val properties = Properties()
                properties.addAttribute("isConnect", true)
                MoEAnalyticsHelper.trackEvent(this, "ConnectGoogleFit", properties)
            }
        } else {
            PermissionUtil.requestPermission(PermissionUtil.readFitnessPermissions(),
                MainApplication.currentActivity)
        }*/
        if (hasFitPermission()) {
            binding.imgConnect.visibility = View.GONE
            binding.llConnected.visibility = View.VISIBLE
            binding.tvSetGoalDisable.visibility = View.GONE
            binding.tvSetGoal.visibility = View.VISIBLE
            preferences!!.isFitConnect = true

            val properties = Properties()
            properties.addAttribute("isConnect", true)
            MoEAnalyticsHelper.trackEvent(this, "ConnectGoogleFit", properties)
        }
    }

    private fun getGoogleFitDataWithSignIn() {
        /*if (checkPermission()) {
            if (hasFitPermission()) {
                setUserAttribute(this, "stepCounter", "connected")
                binding.imgConnect.visibility = View.GONE
                binding.llConnected.visibility = View.VISIBLE
                binding.tvSetGoalDisable.visibility = View.GONE
                binding.tvSetGoal.visibility = View.VISIBLE
                preferences!!.isFitConnect = true

            } else {
                requestFitnessPermission()
            }
        } else {
            PermissionUtil.requestPermission(PermissionUtil.readFitnessPermissions(),
                MainApplication.currentActivity)
        }*/

        if (hasFitPermission()) {
            setUserAttribute(this, "stepCounter", "connected")
            binding.imgConnect.visibility = View.GONE
            binding.llConnected.visibility = View.VISIBLE
            binding.tvSetGoalDisable.visibility = View.GONE
            binding.tvSetGoal.visibility = View.VISIBLE
            preferences!!.isFitConnect = true

        } else {
            requestFitnessPermission()
        }
    }

    private fun getFitnessSignInOptions(): FitnessOptions {
        // Request access to step count data from Fit history
        return FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .addDataType(DataType.TYPE_CALORIES_EXPENDED)
            .addDataType(DataType.TYPE_BASAL_METABOLIC_RATE)
            .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_WRITE)
            .build()
    }

    private fun hasFitPermission(): Boolean {
        // Request permission to collect Google Fit data
        val fitnessOptions = getFitnessSignInOptions()
        return GoogleSignIn.hasPermissions(
            GoogleSignIn.getLastSignedInAccount(this),
            fitnessOptions
        )
    }

    private fun checkPermission(): Boolean {
        return if (PermissionUtil.verifyPermissions(
                this,
                PermissionUtil.readFitnessPermissions()
            )
        ) {
            true
        } else {
            PermissionUtil.requestPermission(
                PermissionUtil.readFitnessPermissions(),
                this
            )
            false
        }
    }

    private fun requestFitnessPermission() {
        GoogleSignIn.requestPermissions(
            this,
            REQUEST_OAUTH_REQUEST_CODE,
            GoogleSignIn.getLastSignedInAccount(this),
            getFitnessSignInOptions()
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}