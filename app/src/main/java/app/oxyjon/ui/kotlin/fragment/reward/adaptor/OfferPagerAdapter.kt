package app.oxyjon.ui.kotlin.fragment.reward.adaptor

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import app.oxyjon.ui.kotlin.fragment.reward.OfferFragment

class OfferPagerAdapter(fragmentManager: FragmentManager, private val itemList: ArrayList<String>) :
    FragmentStatePagerAdapter(fragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return OfferFragment.newInstance("", "", "")!!
    }

    override fun getCount(): Int {
        return itemList.size
    }
}