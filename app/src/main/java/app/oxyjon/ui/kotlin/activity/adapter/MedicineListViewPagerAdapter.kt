package app.oxyjon.ui.kotlin.activity.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import app.oxyjon.bean.MyMedicineResponse
import app.oxyjon.ui.kotlin.fragment.medicine.MyMedicineFragment.Companion.newInstance

class MedicineListViewPagerAdapter(
    fragmentManager: FragmentManager?,
    arrayList: ArrayList<MyMedicineResponse.Data>
) : FragmentStatePagerAdapter(
    fragmentManager!!) {
    var items = ArrayList<MyMedicineResponse.Data>()

    init {
        items = arrayList
    }

    override fun getItem(position: Int): Fragment {
        return newInstance(items[position].medicineitems)
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return items[position].name
    }
}