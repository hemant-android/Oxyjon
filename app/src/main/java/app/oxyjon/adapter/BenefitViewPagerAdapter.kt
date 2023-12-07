package app.oxyjon.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import app.oxyjon.bean.BenefitResponse
import app.oxyjon.ui.fragments.BenefitFragment


class BenefitViewPagerAdapter constructor(
    fragmentManager: FragmentManager?,
    benefitList: ArrayList<BenefitResponse.Data.Benefit>
) : FragmentPagerAdapter(
    (fragmentManager)!!) {
    var items: ArrayList<BenefitResponse.Data.Benefit> = ArrayList()

    init {
        items = benefitList
    }

    public override fun getItem(position: Int): Fragment {
        return BenefitFragment.Companion.newInstance(items[position])
        /*switch (position) {
            case 0:
                return BenefitFragment.newInstance("Benefit from App", "60% + of customers see a reduction in sugar within 30 days.");
            case 1:
                return BenefitFragment.newInstance("Benefit from App", "70% + of customers see a reduction in sugar within 30 days.");
            case 2:
                return BenefitFragment.newInstance("Benefit from App", "80% + of customers see a reduction in sugar within 30 days.");
            default:
                return null;

        }*/
    }

    public override fun getCount(): Int {
        return items.size
    }

    public override fun getPageTitle(position: Int): CharSequence? {
        return "Page $position"
    }
}