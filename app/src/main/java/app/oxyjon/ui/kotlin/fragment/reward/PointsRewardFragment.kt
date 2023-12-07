package app.oxyjon.ui.kotlin.fragment.reward

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import app.oxyjon.databinding.FragmentPointsRewardsBinding
import app.oxyjon.ui.kotlin.fragment.reward.adaptor.OfferPagerAdapter

class PointsRewardFragment : Fragment() {
    private lateinit var binding: FragmentPointsRewardsBinding

    var adapter: OfferPagerAdapter? = null
    var items: ArrayList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPointsRewardsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        items.add("One")
        items.add("Two")
        items.add("Three")

        adapter = OfferPagerAdapter(requireActivity().supportFragmentManager, items)
        binding.viewPager.adapter = adapter
        binding.dotIndicator.setViewPager(binding.viewPager)

        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}