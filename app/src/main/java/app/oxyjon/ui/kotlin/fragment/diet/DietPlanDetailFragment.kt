package app.oxyjon.ui.kotlin.fragment.diet

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.oxyjon.bean.MyDietPlanResponse
import app.oxyjon.databinding.FragmentDietDetailBinding
import app.oxyjon.ui.kotlin.fragment.diet.adapter.DietPlanDetailAdapter

class DietPlanDetailFragment : Fragment() {

    private val adapter: DietPlanDetailAdapter by lazy { DietPlanDetailAdapter(requireActivity()) }
    var items: ArrayList<MyDietPlanResponse.Data.MealData>? = arrayListOf()
    var remark: String? = ""

    companion object {
        fun newInstance(
            data: ArrayList<MyDietPlanResponse.Data.MealData>,
            remark: String
        ): DietPlanDetailFragment {
            val fragmentFirst = DietPlanDetailFragment()
            val args = Bundle()
            args.putSerializable("data", data)
            args.putString("remark", remark)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        items =
            requireArguments().getSerializable("data") as ArrayList<MyDietPlanResponse.Data.MealData>?
    }

    private lateinit var binding: FragmentDietDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDietDetailBinding.inflate(inflater)


        if (requireArguments().getString("remark") != null && !TextUtils.isEmpty(
                requireArguments().getString(
                    "remark"
                )
            )
        ) {
            binding.llRemark.visibility = View.VISIBLE
            binding.tvRemark.text = requireArguments().getString("remark")
        } else {
            binding.llRemark.visibility = View.GONE
        }

        binding.rvMealData.adapter = adapter

        if (items?.size!! > 0) {
            adapter.setData(items)
        }
        return binding.root
    }

}