package app.oxyjon.smsauto

import android.app.PendingIntent
import android.content.*
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task


class AutoDetectOTP constructor(context: Context) {
    private var smsCallback: SmsCallback? = null
    private var googleApiClient: GoogleApiClient? = null
    private val context: Context
    private var chargerReceiver: BroadcastReceiver? = null
    private val appCompatActivity: AppCompatActivity
    private var intentFilter: IntentFilter? = null

    init {
        appCompatActivity = context as AppCompatActivity
        this.context = appCompatActivity.applicationContext
    }

    fun requestPhoneNoHint() {
        googleApiClient = GoogleApiClient.Builder(context)
            .enableAutoManage(appCompatActivity
            ) { }
            .addApi(Auth.CREDENTIALS_API)
            .build()
        val hintRequest: HintRequest = HintRequest.Builder()
            .setHintPickerConfig(CredentialPickerConfig.Builder()
                .setShowCancelButton(true)
                .build())
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val intent: PendingIntent = Auth.CredentialsApi.getHintPickerIntent(
            googleApiClient!!, hintRequest)
        try {
            appCompatActivity.startIntentSenderForResult(intent.intentSender,
                RC_HINT,
                null,
                0,
                0,
                0)
        } catch (e: IntentSender.SendIntentException) {
            Log.e("PHONE_HINT", "Could not start hint picker Intent", e)
        }
    }

    fun requestPhoneNoHint(callback: Callback) {
        googleApiClient = GoogleApiClient.Builder(context)
            .enableAutoManage(appCompatActivity
            ) { }
            .addApi(Auth.CREDENTIALS_API)
            .build()
        googleApiClient = GoogleApiClient.Builder(context)
            .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                public override fun onConnected(bundle: Bundle?) {
                    callback.connectionSuccess(bundle)
                }

                public override fun onConnectionSuspended(i: Int) {
                    callback.connectionSuspend(i)
                }
            })
            .enableAutoManage(appCompatActivity
            ) { connectionResult -> callback.connectionfailed(connectionResult) }
            .addApi(Auth.CREDENTIALS_API)
            .build()
        val hintRequest: HintRequest = HintRequest.Builder()
            .setHintPickerConfig(CredentialPickerConfig.Builder()
                .setShowCancelButton(true)
                .build())
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val intent: PendingIntent = Auth.CredentialsApi.getHintPickerIntent(
            googleApiClient!!, hintRequest)
        try {
            appCompatActivity.startIntentSenderForResult(intent.intentSender,
                RC_HINT,
                null,
                0,
                0,
                0)
        } catch (e: IntentSender.SendIntentException) {
            Log.e("PHONE_HINT", "Could not start hint picker Intent", e)
        }
    }

    fun startSmsRetriver(smsCallback: SmsCallback) {
        registerReceiver()
        this.smsCallback = smsCallback
        // Get an instance of SmsRetrieverClient, used to start listening for a matching
// SMS message.
        val client: SmsRetrieverClient = SmsRetriever.getClient(context)

// Starts SmsRetriever, which waits for ONE matching SMS message until timeout
// (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
// action SmsRetriever#SMS_RETRIEVED_ACTION.
        val task: Task<Void> = client.startSmsRetriever()
        // Listen for success/failure of the start Task. If in a background thread, this
// can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener { aVoid ->
            Log.e("SMSRE", "success")
            smsCallback.connectionSuccess(aVoid)
        }
        task.addOnFailureListener { smsCallback.connectionfailed() }
    }

    fun getPhoneNo(data: Intent): String {
        val cred: Credential? = data.getParcelableExtra(Credential.EXTRA_KEY)
        return cred!!.id
    }

    private fun registerReceiver() {
//        filter to receive SMS
        intentFilter = IntentFilter()
        intentFilter!!.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)

//        receiver to receive and to get otp from SMS
        chargerReceiver = object : BroadcastReceiver() {
            public override fun onReceive(context: Context?, intent: Intent) {
                if ((SmsRetriever.SMS_RETRIEVED_ACTION == intent.action)) {
                    val extras: Bundle? = intent.extras
                    val status: Status? = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status?
                    when (status!!.statusCode) {
                        CommonStatusCodes.SUCCESS -> {
                            // Get SMS message contents
                            val message: String? =
                                extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String?
                            // Extract one-time code from the message and complete verification
                            // by sending the code back to your server for SMS authenticity.
                            smsCallback!!.smsCallback(message)
                            stopSmsReciever()
                        }
                        CommonStatusCodes.TIMEOUT ->                             // Waiting for SMS timed out (5 minutes)
                            smsCallback!!.connectionfailed()
                    }
                }
            }
        }
        appCompatActivity.application.registerReceiver(chargerReceiver, intentFilter)
    }

    fun stopSmsReciever() {
        try {
            appCompatActivity.applicationContext.unregisterReceiver(chargerReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    open interface Callback {
        fun connectionfailed(connectionResult: ConnectionResult?)
        fun connectionSuspend(i: Int)
        fun connectionSuccess(bundle: Bundle?)
    }

    open interface SmsCallback {
        fun connectionfailed()
        fun connectionSuccess(aVoid: Void?)
        fun smsCallback(sms: String?)
    }

    companion object {
        val RC_HINT: Int = 1000
        fun getHashCode(context: Context?): String? {
            val appSignature = AppSignatureHelper(context)
            Log.e(" getAppSignatures ", "" + appSignature.appSignatures[0])
            return appSignature.appSignatures[0]
        }
    }
}