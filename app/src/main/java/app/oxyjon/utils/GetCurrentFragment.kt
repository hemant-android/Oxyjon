package app.oxyjon.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager


object GetCurrentFragment {
    fun getCurrentChildFragment(activity: FragmentActivity?): Fragment? {
        if (activity == null) return null
        val currentFragment: Fragment = getCurrentFragment(activity) ?: return null
        val manager: FragmentManager = currentFragment.childFragmentManager
        val fragmentList: List<Fragment>? = manager.fragments
        if (fragmentList != null) {
            for (i in fragmentList.size - 1 downTo -1 + 1) {
                val aFragmentList: Fragment? = fragmentList[i]
                if (aFragmentList != null && aFragmentList.isVisible) return aFragmentList
            } /*  www. j  a  v  a 2 s.co m*/
        }
        return null
    }

    fun getCurrentFragment(activity: FragmentActivity?): Fragment? {
        if (activity == null) return null
        val manager: FragmentManager = activity.supportFragmentManager
        val fragmentList: List<Fragment>? = manager.fragments
        if (fragmentList != null) {
            for (i in fragmentList.size - 1 downTo -1 + 1) {
                val aFragmentList: Fragment? = fragmentList[i]
                if (aFragmentList != null && aFragmentList.isVisible) return aFragmentList
            }
        }
        return null
    }
}