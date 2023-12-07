package app.oxyjon.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import app.oxyjon.R
import app.oxyjon.ui.fragments.WalkThroughFragment


class ViewAdapter constructor(fragmentManager: FragmentManager?) : FragmentPagerAdapter(
    (fragmentManager)!!) {
    public override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> WalkThroughFragment.newInstance(0,
                "Sugar reduction plans ",
                "We use international guidelines and best diabetes doctors",
                R.mipmap.slide1)
            1 -> WalkThroughFragment.newInstance(1,
                "Medicines and Lab Tests",
                "Doorstep service of medicines and lab tests at attractive prices",
                R.mipmap.slide2)
            2 -> WalkThroughFragment.newInstance(2,
                "Self management",
                "Keep track of sugar levels. food consumed and calories burnt ",
                R.mipmap.slide3)
            else -> WalkThroughFragment.newInstance(0,
                "Sugar reduction plans ",
                "We use international guidelines and best diabetes doctors",
                R.mipmap.slide1)
        }
    }

    public override fun getCount(): Int {
        return NUM_ITEMS
    }

    public override fun getPageTitle(position: Int): CharSequence? {
        return "Page $position"
    }

    companion object {
        private const val NUM_ITEMS: Int = 3
    }
}