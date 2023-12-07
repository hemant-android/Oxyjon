package app.oxyjon.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import app.oxyjon.MainApplication


open class BaseFragment constructor() : Fragment() {
    var mMyApp: MainApplication? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMyApp = MainApplication.context as MainApplication?
    }

    public override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        hideKeyboard(getActivity())
    }

    public override fun onDetach() {
        super.onDetach()
        hideKeyboard(activity)
    }

    public override fun onResume() {
        super.onResume()
        mMyApp!!.setCurrentActivity(requireActivity())
    }

    companion object {
        fun hideKeyboard(activity: Activity?) {
            val imm: InputMethodManager =
                activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            var view: View? = activity.currentFocus
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}