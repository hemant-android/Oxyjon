package app.oxyjon.ui.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import app.oxyjon.R
import app.oxyjon.bean.BenefitResponse
import butterknife.BindView
import butterknife.ButterKnife

class BenefitFragment constructor() : Fragment() {
    private var title: String? = null
    private var desc: String? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tvTitle)
    var tvTitle: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tvDesc)
    var tvDesc: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.llMain)
    var llMain: LinearLayout? = null
    private var width: Int = 0
    private var height: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = requireArguments().getString("title")
        desc = requireArguments().getString("desc")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view: View = inflater.inflate(R.layout.benefit_item, container, false)
        ButterKnife.bind(this, view)
        val displayMetrics: DisplayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
        height = displayMetrics.heightPixels
        val mWidth: Int = (width / 1.5).toInt()
        llMain!!.layoutParams = LinearLayout.LayoutParams(mWidth,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        tvTitle!!.text = title
        tvDesc!!.text = desc
        return view
    }

    companion object {
        fun newInstance(title: String?, desc: String?): BenefitFragment {
            val fragmentFirst = BenefitFragment()
            val args = Bundle()
            args.putString("title", title)
            args.putString("desc", desc)
            fragmentFirst.arguments = args
            return fragmentFirst
        }

        fun newInstance(benefit: BenefitResponse.Data.Benefit): Fragment {
            val fragmentFirst = BenefitFragment()
            val args = Bundle()
            args.putString("title", benefit.title)
            args.putString("desc", benefit.details)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}