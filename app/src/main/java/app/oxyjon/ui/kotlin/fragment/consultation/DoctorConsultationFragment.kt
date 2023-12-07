package app.oxyjon.ui.kotlin.fragment.consultation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import app.oxyjon.databinding.FragmentDoctorConsultationBinding
import app.oxyjon.ui.kotlin.activity.DoctorConsultationActivity
import app.oxyjon.ui.kotlin.fragment.consultation.adapter.DoctorConsultationListAdapter

class DoctorConsultationFragment : Fragment(), DoctorConsultationListAdapter.onClickListner {
    private lateinit var binding: FragmentDoctorConsultationBinding
    private val adapter: DoctorConsultationListAdapter by lazy {
        DoctorConsultationListAdapter(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDoctorConsultationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvDoctorConsultation.layoutManager = layoutManager
        binding.rvDoctorConsultation.adapter = adapter

        adapter.setClickListner(this)

        binding.imgBack.setOnClickListener {

            findNavController().popBackStack()
        }
    }

    override fun onSelectDoctor() {
        val intent = Intent(requireActivity(), DoctorConsultationActivity::class.java)
        startActivity(intent)
    }
}