package app.oxyjon.ui.kotlin.fragment.order

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import app.oxyjon.ui.kotlin.activity.NotificationListActivity
import app.oxyjon.ui.kotlin.fragment.order.adapter.OrderListAdapter

class OrderItemFragment : Fragment() {
    private lateinit var binding: app.oxyjon.databinding.FragmentOrderBinding

    private val adapter: OrderListAdapter by lazy { OrderListAdapter(requireActivity()) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = app.oxyjon.databinding.FragmentOrderBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgNotification.setOnClickListener {
            val intent = Intent(requireActivity(), NotificationListActivity::class.java)
            startActivity(intent)
        }
        binding.rvOrder.adapter = adapter

        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}