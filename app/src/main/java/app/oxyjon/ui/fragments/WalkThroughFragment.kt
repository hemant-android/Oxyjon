package app.oxyjon.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import app.oxyjon.R
import butterknife.BindView
import butterknife.ButterKnife


class WalkThroughFragment : Fragment() {
    private var title: String? = null
    private var desc: String? = null
    private var image: Int = 0

    @JvmField
    @BindView(R.id.tvTitle)
    var tvTitle: TextView? = null

    @JvmField
    @BindView(R.id.tvDesc)
    var tvDesc: TextView? = null

    @JvmField
    @BindView(R.id.imageView)
    var imageView: ImageView? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = requireArguments().getString("title")
        desc = requireArguments().getString("desc")
        image = requireArguments().getInt("image")
    }

    public override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.walkthrough_item, container, false)
        ButterKnife.bind(this, view)
        tvTitle!!.text = title
        tvDesc!!.text = desc
        imageView!!.setImageResource(image)
        return view
    }

    companion object {
        fun newInstance(page: Int, title: String?, desc: String?, image: Int): WalkThroughFragment {
            val fragmentFirst: WalkThroughFragment = WalkThroughFragment()
            val args: Bundle = Bundle()
            args.putInt("page", page)
            args.putString("title", title)
            args.putString("desc", desc)
            args.putInt("image", image)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}