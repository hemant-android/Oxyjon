package app.oxyjon.ui.kotlin.activity.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import app.oxyjon.bean.MyDietPlanResponse
import app.oxyjon.ui.kotlin.fragment.diet.DietPlanDetailFragment

class ViewPagerDietAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    data: ArrayList<MyDietPlanResponse.Data>
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    var items: ArrayList<MyDietPlanResponse.Data> = ArrayList()

    init {
        items = data
    }
    override fun getItemCount(): Int {
        return items.size
    }

    override fun createFragment(position: Int): Fragment {

        return DietPlanDetailFragment.newInstance(items[position].meal_data,items[position].meal_remarks)
    }

}