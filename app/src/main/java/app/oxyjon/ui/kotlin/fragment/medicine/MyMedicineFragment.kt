package app.oxyjon.ui.kotlin.fragment.medicine

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import app.oxyjon.R
import app.oxyjon.bean.MyMedicineResponse
import app.oxyjon.bean.MyMedicineResponse.Data.Medicineitem
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.FragmentMyMedicineBinding
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.ui.kotlin.activity.MedicineListActivity
import app.oxyjon.ui.kotlin.fragment.medicine.adapter.MyMedicineListAdapter
import app.oxyjon.utils.FunctionHelper
import retrofit2.Response

class MyMedicineFragment : Fragment(), IApiCallback, MyMedicineListAdapter.ClickListener {

    private var posDeleteItem: Int? = 0
    private var timeSlot: String? = ""

    private lateinit var binding: FragmentMyMedicineBinding
    var preferences: AppSharedPreferences? = null

    private val adapter: MyMedicineListAdapter by lazy { MyMedicineListAdapter(requireActivity()) }

    private var items: ArrayList<Medicineitem>? = arrayListOf()


    companion object {
        @JvmStatic
        fun newInstance(items: ArrayList<Medicineitem>): MyMedicineFragment {
            val fragmentFirst = MyMedicineFragment()
            val args = Bundle()
            args.putSerializable("items", items)
            fragmentFirst.arguments = args
            return fragmentFirst
        }

        fun isConnection(ctx: Context): Boolean {
            val connectivityManager =
                ctx.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val ni = connectivityManager.activeNetworkInfo
            return ni != null && ni.isAvailable && ni.isConnected
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        items = requireArguments().getSerializable("items") as ArrayList<Medicineitem>
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMyMedicineBinding.inflate(inflater)

        preferences = AppSharedPreferences.getInstance(requireContext())

        binding.rvMedicineList.adapter = adapter
        adapter.setClickListener(this)
        adapter.setData(items)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onSelectMedicineClick(medicineId: Int, position: Int) {
        posDeleteItem = position

//        (activity as MedicineListActivity?)!!.removeMedicine(posDeleteItem!!)

        var pos = (activity as MedicineListActivity?)!!.binding.tbLayout.selectedTabPosition

        timeSlot = when (pos) {
            0 -> {
                "before_breakfast"
            }
            1 -> {
                "after_breakfast"
            }
            2 -> {
                "before_lunch"
            }
            3 -> {
                "after_lunch"
            }
            4 -> {
                "before_dinner"
            }
            5 -> {
                "after_dinner"
            }
            else -> {
                "bed_time"
            }
        }

        if (MedicineListActivity.isConnection(requireActivity())) {
            FunctionHelper.disable_user_Intration(
                requireActivity(),
                resources.getString(R.string.loading)
            )
            ApiCall.instance.removeMedicineList(medicineId.toString(), timeSlot!!, this)
        } else {
            Toast.makeText(
                requireActivity(),
                "please check your internet connection",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "myMedicine") {
            val response = data as Response<MyMedicineResponse>
            if (response.isSuccessful && response.body()!!.errorCode == "1") {
                if (response.body()!!.data?.size!! > 0) {
                    if (items != null && items!!.size > 0) {
                        items!!.clear()
                    }

                } else {
                }
            } else {

//                medicineListAdapter.setData(medicineList)
            }
        }
        if (type == "removeMedicine") {
            /*var pos = 0
            if (MedicineListActivity.isConnection(requireActivity())) {
                ApiCall.getInstance().getMedicineList(preferences!!.getprofileid(), this)
            } else {
                Toast.makeText(requireActivity(),
                    "please check your internet connection",
                    Toast.LENGTH_SHORT).show()
            }*/

            (activity as MedicineListActivity?)!!.removeMedicine(posDeleteItem!!)

        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }
}