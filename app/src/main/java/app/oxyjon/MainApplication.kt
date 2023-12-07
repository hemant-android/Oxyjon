package app.oxyjon

import android.app.Activity
import android.app.Application
import android.content.Context
import android.widget.Toast
import app.oxyjon.database.AppDatabase
import com.facebook.FacebookSdk.fullyInitialize
import com.facebook.FacebookSdk.setAutoInitEnabled
import com.facebook.FacebookSdk.setAutoLogAppEventsEnabled
import com.facebook.appevents.AppEventsLogger
import com.moengage.core.DataCenter
import com.moengage.core.LogLevel
import com.moengage.core.MoEngage
import com.moengage.core.analytics.MoEAnalyticsHelper.setAppStatus
import com.moengage.core.config.FcmConfig
import com.moengage.core.config.LogConfig
import com.moengage.core.config.MiPushConfig
import com.moengage.core.config.NotificationConfig
import com.moengage.core.model.AppStatus
import io.branch.referral.Branch
import ly.count.android.sdk.Countly
import ly.count.android.sdk.CountlyConfig


class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        application = this
        Countly.sharedInstance().init(CountlyConfig(this,
            "31782754e970dbb85e6798940caeb0b870c68af1",
            "http://18.117.254.91/"))
        val config: CountlyConfig = (CountlyConfig(this,
            "31782754e970dbb85e6798940caeb0b870c68af1",
            "http://18.117.254.91/"))
        config.setLoggingEnabled(false)
        Countly.sharedInstance().init(config)
        setAutoInitEnabled(true)
        fullyInitialize()
        AppEventsLogger.activateApp(this)
        setAutoLogAppEventsEnabled(true)

        // Branch logging for debugging
        Branch.disableLogging()

        // Branch object initialization
        Branch.getAutoInstance(this)
        val moEngage: MoEngage = MoEngage.Builder(this, "MDC81DQSMET0S4VBYE6XGZG3")
            .setDataCenter(DataCenter.DATA_CENTER_3)
            .configureLogs(LogConfig(LogLevel.VERBOSE, false))
            .configureMiPush(MiPushConfig("2882303761520472542", "5822047247542", true))
            .configureNotificationMetaData(NotificationConfig(R.mipmap.ic_launcher,
                R.mipmap.ic_launcher,
                R.color.pink_color,
                isMultipleNotificationInDrawerEnabled = true,
                isBuildingBackStackEnabled = true,
                isLargeIconDisplayEnabled = true))
            .configureFcm(FcmConfig(false))
            .build()
        MoEngage.initialiseDefaultInstance(moEngage)
        setAppStatus(this, AppStatus.INSTALL)

        AppDatabase.getInstance(this)
    }

    fun setCurrentActivity(activity: Activity) {
        currentActivity = activity
    }

    companion object {
        var currentActivity: Activity? = null
        private var application: MainApplication? = null
        var clickSugar: Boolean = false
        var clickFood: Boolean = false
        var clickMedicine: Boolean = false
        var clickStepCounter: Boolean = false
        var isFeedback: Boolean = false
        private fun getApplication(): Application? {
            return application
        }

        var context: Context
            get() {
                return getApplication()!!.applicationContext
            }
            set(value) {}
    }
}