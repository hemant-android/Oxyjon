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
import android.util.Log
import android.view.View
import app.oxyjon.MainApplication
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivitySettingBinding
import app.oxyjon.ui.activity.BaseActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.moengage.core.analytics.MoEAnalyticsHelper

class SettingActivity : BaseActivity() {
    lateinit var binding: ActivitySettingBinding
    var preferences: AppSharedPreferences? = null

    lateinit var mGoogleSignInClient: GoogleSignInClient

    private val REQUEST_OAUTH_REQUEST_CODE = 0x1001
    private val TAG = "SettingActivity.class"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppSharedPreferences.getInstance(this)

        MainApplication.currentActivity = this

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.rlConnect.visibility = View.VISIBLE
        } else {
            binding.rlConnect.visibility = View.GONE
        }

        if (preferences!!.isFitConnect) {
            binding.tvConnect.text = "Disconnect"
        } else {
            binding.tvConnect.text = "Connect"
        }

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.tvConnect.setOnClickListener {
            if (binding.tvConnect.text.toString().equals("Disconnect", ignoreCase = true)) {
                disconnectFitbit()
            } else {
                requestPermissions(true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MainApplication.currentActivity = this
    }

    private fun disconnectFitbit() {
        val fitnessOptions = getFitnessSignInOptions()
        Fitness.getConfigClient(
            this,GoogleSignIn.getAccountForExtension(this, fitnessOptions)
        )
            .disableFit()
            .addOnSuccessListener { unused: Void? ->
                Log.i(TAG, "Disabled Google Fit")
                preferences!!.isFitConnect = false
                binding.tvConnect.text = "Connect"
            }
            .addOnFailureListener { e: Exception? ->
                Log.w(TAG, "There was an error disabling Google Fit", e)
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                Log.i(TAG, "Fitness permission granted")
                binding.tvConnect.text = "Disconnect"

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
        if (hasFitPermission()) {
            binding.tvConnect.text = "Disconnect"
            preferences!!.isFitConnect = true
        }
    }

    private fun getGoogleFitDataWithSignIn() {
        if (hasFitPermission()) {
            MoEAnalyticsHelper.setUserAttribute(this, "stepCounter", "connected")
            binding.tvConnect.text = "Disconnect"
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

    private fun requestFitnessPermission() {
        GoogleSignIn.requestPermissions(
            this,
            REQUEST_OAUTH_REQUEST_CODE,
            GoogleSignIn.getLastSignedInAccount(this),
            getFitnessSignInOptions()
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}