package app.oxyjon.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import app.oxyjon.R
import com.google.android.material.snackbar.Snackbar
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.ParseException
import java.util.*
import java.util.regex.Pattern


object FunctionHelper {
    private var sProgressDialog: ProgressDialog? = null
    fun disable_user_Intration(context: Context?, text: String?) {
        enableUserIntraction()
        if (sProgressDialog == null || sProgressDialog!!.context !== context) {
            sProgressDialog = ProgressDialog(context)
            sProgressDialog!!.setCancelable(true)
            sProgressDialog!!.setCanceledOnTouchOutside(false)
            sProgressDialog!!.setMessage(text)
            try {
                sProgressDialog!!.show()
            } catch (e: WindowManager.BadTokenException) {
                e.printStackTrace()
            }
        }
    }

    fun enableUserIntraction() {
        try {
            if (sProgressDialog != null && sProgressDialog!!.isShowing) {
                sProgressDialog!!.dismiss()
            }
            sProgressDialog = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showFailureToast(context: Context?, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun hideKeyBoard(context: Context, editText: View?) {
        val imm: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText!!.windowToken, 0)
    }

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view: View? = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showSnackMessage(view: View, message: String?) {
        try {
            val snackbar: Snackbar = Snackbar.make(view, (message)!!, Snackbar.LENGTH_SHORT)
            val snackBarView: View = snackbar.view
            snackBarView.setBackgroundColor(Color.parseColor("#303F9F"))
            val textView: TextView = snackBarView.findViewById<View>(R.id.snackbar_text) as TextView
            textView.setTextColor(Color.parseColor("#FFFFFF"))
            snackbar.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isValidEmail(target: String?): Boolean {
        return !TextUtils.isEmpty(target) && !Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun isValidMobile(phone: String): Boolean {
        var check: Boolean = false
        check = if (!Pattern.matches("[a-zA-Z]+", phone)) {
            !(phone.length < 4 || phone.length > 15)
        } else {
            false
        }
        return check
    }

    fun changeStatusBarColor(activity: Activity, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, color));
            activity.window.statusBarColor = ContextCompat.getColor(activity, color)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun changeDateformat(date: String?, currentformat: String?, changeinformat: String?): String {
        val dateFormat: SimpleDateFormat = SimpleDateFormat(currentformat)
        var sourceDate: Date? = null
        try {
            sourceDate = dateFormat.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val targetFormat: SimpleDateFormat = SimpleDateFormat(changeinformat)
        val targetdatevalue: String = targetFormat.format(sourceDate)
        return targetdatevalue
    }

    fun daysCalculate(startDate: String?,endDate: String?): String {
        val mDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val mDate11 = mDateFormat.parse(endDate)
        val mDate22 = mDateFormat.parse(startDate)
        val mDifference = (mDate11.time - mDate22.time)
        val mDifferenceDates = mDifference / (24 * 60 * 60 * 1000)
        return mDifferenceDates.toString()
    }

    fun showKeyBoard(context: Context, view: View?) {
        val imm: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    // If you want the highest value of the 2 digits after decimal use below code
    fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }

    // If you want the lowest value of the 2 digits after decimal use below code.
    fun roundOffDecimalLowest(number: Double): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(number).toDouble()
    }
}