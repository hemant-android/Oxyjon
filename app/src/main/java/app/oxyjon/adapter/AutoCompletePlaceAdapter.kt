package app.oxyjon.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import app.oxyjon.R
import app.oxyjon.database.Medicine
import java.util.*


class AutoCompletePlaceAdapter constructor(context: Context, placesList: List<Medicine>) :
    ArrayAdapter<Medicine?>(context, 0, placesList) {
    private var allPlacesList: List<Medicine>? = listOf()
    private var filteredPlacesList: MutableList<Medicine?>? = null
    public override fun getFilter(): Filter {
        return placeFilter
    }

    public override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView: View? = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                R.layout.raw_autocomplete_item, parent, false
            )
        }
        val placeLabel: TextView = convertView!!.findViewById(R.id.textView)
        val place: Medicine? = getItem(position)
        if (place != null) {
            placeLabel.text = place.medicineName
        }
        return (convertView)
    }

    private val placeFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val results = FilterResults()
            filteredPlacesList = ArrayList()
            if (constraint == null || constraint.isEmpty()) {
                Log.i(AutoCompletePlaceAdapter::class.java.simpleName,
                    "performFiltering no constraint")
                filteredPlacesList!!.addAll(allPlacesList!!)
            } else {
                Log.i(AutoCompletePlaceAdapter::class.java.simpleName,
                    "performFiltering $constraint")
                val filterPattern: String =
                    constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (place: Medicine? in allPlacesList!!) {
                    if (place!!.medicineName.lowercase(Locale.getDefault())
                            .contains(filterPattern)
                    ) {
                        filteredPlacesList!!.add(place)
                    }
                }
            }
            results.values = filteredPlacesList
            results.count = filteredPlacesList!!.size
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            clear()
            if (constraint != null) {
                addAll((results?.values as List<Medicine>?)!!)
                notifyDataSetChanged()
            }
        }

        public override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as Medicine?)!!.medicineName
        }
    }

    init {
        allPlacesList = ArrayList(placesList)
    }
}