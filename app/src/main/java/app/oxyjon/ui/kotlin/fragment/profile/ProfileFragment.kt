package app.oxyjon.ui.kotlin.fragment.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.FragmentProfileBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.retrofit.response.CommonResponse
import app.oxyjon.ui.activity.LoginActivity
import app.oxyjon.ui.kotlin.activity.DashboardActivity
import app.oxyjon.ui.kotlin.activity.EditProfileActivity
import app.oxyjon.ui.kotlin.activity.NotificationListActivity
import app.oxyjon.ui.kotlin.activity.SettingActivity
import app.oxyjon.ui.kotlin.activity.StaticWebPageActivity
import app.oxyjon.utils.CheckConnection
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.moengage.core.MoECoreHelper.logoutUser
import retrofit2.Response

class ProfileFragment : Fragment(), IApiCallback {
    private lateinit var binding: FragmentProfileBinding
    var preferences: AppSharedPreferences? = null

    var logger: AppEventsLogger? = null
    var mFirebaseAnalytics: FirebaseAnalytics? = null

    private val mGoogleSignInClient: GoogleSignInClient? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences = AppSharedPreferences.getInstance(requireActivity())

        logger = AppEventsLogger.newLogger(requireActivity())
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity())

        if (preferences!!.fullName != null && !TextUtils.isEmpty(preferences!!.fullName)) {
            binding.tvProfileName.visibility = View.VISIBLE
            binding.tvProfileName.text = "Hi " + preferences!!.fullName!!.split(" ")[0]
        } else {
            binding.tvProfileName.visibility = View.GONE
        }

        if (CheckConnection.isConnection(requireActivity())) {
            ApiCall.instance.getUserPaidOrNot(this)
        }

        binding.rlUpdateProfile.setOnClickListener {
            val intent = Intent(requireActivity(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.rlPointReward.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileToPointsRewardFragment())
        }

        binding.rlPurchasesItem.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileToOrderItemFragment())
        }

        binding.imgNotification.setOnClickListener {
            val intent = Intent(requireActivity(), NotificationListActivity::class.java)
            startActivity(intent)
        }

        binding.rlHowItWork.setOnClickListener {
            val intent = Intent(requireActivity(), StaticWebPageActivity::class.java)
            intent.putExtra("title", "How it works")
            intent.putExtra("url", "https://oxyjon.com/faqs")
            startActivity(intent)
        }

        binding.rlPrivacyPolicy.setOnClickListener {
            val intent = Intent(requireActivity(), StaticWebPageActivity::class.java)
            intent.putExtra("title", "Policy Support")
            intent.putExtra("url", "https://oxyjon.com/privacy-policy")
            startActivity(intent)
        }

        binding.rlShareApp.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(
                Intent.EXTRA_TEXT,
                """ ${resources.getString(R.string.please_check_oxyjon_app)}     
     https://play.google.com/store/apps/details?id=app.oxyjon """.trimIndent()
            )
            startActivity(Intent.createChooser(sharingIntent, "Oxyjon"))
        }

        binding.rlSetting.setOnClickListener {
            val intent = Intent(requireActivity(), SettingActivity::class.java)
            startActivity(intent)
        }


        binding.llLogout.setOnClickListener {
            if (AppSharedPreferences.getInstance(requireActivity())!!.userLoggedIn!!.isEmpty()) {
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
                builder.setTitle(resources.getString(R.string.logoutTitle))
                    .setMessage(resources.getString(R.string.areYouSureYouWantToLogout))

                builder.setPositiveButton("Yes") { dialog, id ->

                    if (requireActivity() != null && isAdded) {
                        val intent = Intent(requireActivity(), LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    callTrackScreenAPi(
                        AppSharedPreferences.getInstance(requireActivity())!!.userId!!,
                        AppSharedPreferences.getInstance(requireActivity())!!.getprofileid()!!,
                        "userLogout",
                        "0",
                        "0"
                    )

                    logoutUser(requireActivity())
                    logger!!.logEvent("logout")

                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "logout")
                    mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

                    val userMobileNumber =
                        AppSharedPreferences.getInstance(requireActivity())!!.userMobileNumber
                    val userPassword =
                        AppSharedPreferences.getInstance(requireActivity())!!.password
                    val userFullName =
                        AppSharedPreferences.getInstance(requireActivity())!!.fullName
                    val userImage = AppSharedPreferences.getInstance(requireActivity())!!.image
                    val remember = AppSharedPreferences.getInstance(requireActivity())!!.rememberMe
                    val isWalkThrough =
                        AppSharedPreferences.getInstance(requireActivity())!!.isWalkThrough
                    val isPopupShow =
                        AppSharedPreferences.getInstance(requireActivity())!!.isPopupShow

                    val sugarDialogCount =
                        AppSharedPreferences.getInstance(requireActivity())!!.sugarDialogCount
                    val foodDiaryDialogCount =
                        AppSharedPreferences.getInstance(requireActivity())!!.foodDiaryDialogCount
                    val medicineDialogCount =
                        AppSharedPreferences.getInstance(requireActivity())!!.medicineDialogCount
                    val stepCounterDialogCount =
                        AppSharedPreferences.getInstance(requireActivity())!!.stepCounterDialogCount

                    val isSugarDialogPopup =
                        AppSharedPreferences.getInstance(requireActivity())!!.sugarDialogPopup
                    val isFoodDiaryDialogPopup =
                        AppSharedPreferences.getInstance(requireActivity())!!.foodDiaryDialogPopup
                    val medicineDialogPopup =
                        AppSharedPreferences.getInstance(requireActivity())!!.medicineDialogPopup
                    val stepCountDialogPopup =
                        AppSharedPreferences.getInstance(requireActivity())!!.stepCountDialogPopup
                    val feedbackDialogPopup =
                        AppSharedPreferences.getInstance(requireActivity())!!.feedbackDialogPopup
                    val userOnBoard =
                        AppSharedPreferences.getInstance(requireActivity())!!.userOnBoard

                    AppSharedPreferences.getInstance(requireActivity())!!.clear()
                    FirebaseMessaging.getInstance().deleteToken()
                    mGoogleSignInClient?.signOut()

                    AppSharedPreferences.getInstance(requireActivity())!!.password = userPassword
                    AppSharedPreferences.getInstance(requireActivity())!!.fullName = userFullName
                    AppSharedPreferences.getInstance(requireActivity())!!.image = userImage
                    AppSharedPreferences.getInstance(requireActivity())!!.userMobileNumber =
                        userMobileNumber
                    AppSharedPreferences.getInstance(requireActivity())!!.rememberMe = remember
                    AppSharedPreferences.getInstance(requireActivity())!!
                        .isWalkThrough(isWalkThrough)
                    AppSharedPreferences.getInstance(requireActivity())!!.isPopupShow(isPopupShow)
                    AppSharedPreferences.getInstance(requireActivity())!!.isFitConnect = false

                    AppSharedPreferences.getInstance(requireActivity())!!.sugarDialogCount =
                        sugarDialogCount
                    AppSharedPreferences.getInstance(requireActivity())!!.foodDiaryDialogCount =
                        foodDiaryDialogCount
                    AppSharedPreferences.getInstance(requireActivity())!!.medicineDialogCount =
                        medicineDialogCount
                    AppSharedPreferences.getInstance(requireActivity())!!.stepCounterDialogCount =
                        stepCounterDialogCount

                    AppSharedPreferences.getInstance(requireActivity())
                        ?.isSugarDialogPopup(isSugarDialogPopup)
                    AppSharedPreferences.getInstance(requireActivity())
                        ?.isFoodDiaryDialogPopup(isFoodDiaryDialogPopup)
                    AppSharedPreferences.getInstance(requireActivity())
                        ?.isMedicineDialogPopup(medicineDialogPopup)
                    AppSharedPreferences.getInstance(requireActivity())
                        ?.isStepCountDialogPopup(stepCountDialogPopup)
                    AppSharedPreferences.getInstance(requireActivity())
                        ?.isFeedbackDialogPopup(feedbackDialogPopup)
                    AppSharedPreferences.getInstance(requireActivity())?.userOnBoard = ""

                    AppSharedPreferences.getInstance(requireActivity())?.userLoggedIn = ""

                    MainApplication.clickSugar = false
                    MainApplication.clickFood = false
                    MainApplication.clickMedicine = false
                    MainApplication.clickStepCounter = false

                }
                builder.setNegativeButton("No") { dialog, id -> dialog.cancel() }
                val alert11 = builder.create()
                alert11.show()
            }
        }

    }

    private fun callTrackScreenAPi(
        customerId: String,
        profileId: String,
        eventName: String,
        actionId: String,
        actionHeading: String,
    ) {
        if (CheckConnection.isConnection(requireActivity())) {
            ApiCall.instance
                .trackScreen(customerId, profileId, eventName, actionId, actionHeading, this)
        } else {
            Toast.makeText(
                requireActivity(),
                "please check your internet connection",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {

    }

    override fun onFailure(data: Any) {
    }

}