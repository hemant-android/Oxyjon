package app.oxyjon.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager


/**
 * Created by appsinvo on 16/7/18.
 */
class KeyBoardUtils private constructor(act: Activity, listener: SoftKeyboardToggleListener) :
    ViewTreeObserver.OnGlobalLayoutListener {
    private var mCallback: SoftKeyboardToggleListener?
    private val mRootView: View
    private var prevValue: Boolean? = null
    private val mScreenDensity: Float

    open interface SoftKeyboardToggleListener {
        fun onToggleSoftKeyboard(isVisible: Boolean)
    }

    public override fun onGlobalLayout() {
        val r: Rect = Rect()
        mRootView.getWindowVisibleDisplayFrame(r)
        val heightDiff: Int = mRootView.rootView.height - (r.bottom - r.top)
        val dp: Float = heightDiff / mScreenDensity
        val isVisible: Boolean = dp > MAGIC_NUMBER
        if (mCallback != null && (prevValue == null || isVisible != prevValue)) {
            prevValue = isVisible
            mCallback!!.onToggleSoftKeyboard(isVisible)
        }
    }

    private fun removeListener() {
        mCallback = null
        mRootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    init {
        mCallback = listener
        mRootView = (act.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
        mRootView.viewTreeObserver.addOnGlobalLayoutListener(this)
        mScreenDensity = act.resources.displayMetrics.density
    }

    companion object {
        private val MAGIC_NUMBER: Int = 200
        private val sListenerMap: HashMap<SoftKeyboardToggleListener, KeyBoardUtils> = HashMap()

        /**
         * Add a new keyboard listener
         * @param act calling activity
         * @param listener callback
         */
        fun addKeyboardToggleListener(act: Activity, listener: SoftKeyboardToggleListener) {
            removeKeyboardToggleListener(listener)
            sListenerMap[listener] = KeyBoardUtils(act, listener)
        }

        /**
         * Remove a registered listener
         * @param listener [SoftKeyboardToggleListener]
         */
        private fun removeKeyboardToggleListener(listener: SoftKeyboardToggleListener) {
            if (sListenerMap.containsKey(listener)) {
                val k: KeyBoardUtils? = sListenerMap.get(listener)
                k!!.removeListener()
                sListenerMap.remove(listener)
            }
        }

        /**
         * Remove all registered keyboard listeners
         */
        fun removeAllKeyboardToggleListeners() {
            for (l: SoftKeyboardToggleListener in sListenerMap.keys) sListenerMap.get(l)!!
                .removeListener()
            sListenerMap.clear()
        }

        /**
         * Manually toggle soft keyboard visibility
         * @param context calling context
         */
        fun toggleKeyboardVisibility(context: Context) {
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)?.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                0)
        }

        /**
         * Force closes the soft keyboard
         * @param activeView the view with the keyboard focus
         */
        fun forceCloseKeyboard(activeView: View) {
            (activeView.context.getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager?)?.hideSoftInputFromWindow(
                activeView.windowToken,
                0)
        }
    }
}