package app.oxyjon.ui.kotlin.fragment.reward

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.oxyjon.databinding.FragmentOfferBinding

class OfferFragment : Fragment() {
    private lateinit var binding: FragmentOfferBinding

    companion object {
        fun newInstance(pos: String?, title: String?, desc: String?): OfferFragment? {
            val fragment = OfferFragment()
            val args = Bundle()
            args.putString("position", pos)
            args.putString("title", title)
            args.putString("desc", desc)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentOfferBinding.inflate(inflater)
        return binding.root
    }
}