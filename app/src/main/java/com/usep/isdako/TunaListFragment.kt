package com.usep.isdako

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.usep.isdako.databinding.RecyclerItemTunaModelBinding


class TunaListFragment : Fragment() {

    private lateinit var imageResIds: IntArray
    private lateinit var names: Array<String>
    private lateinit var descriptions: Array<String>
    private lateinit var size: Array<String>
    private lateinit var listener: OnTunaSelected

    companion object {

        fun newInstance(): TunaListFragment {
            return TunaListFragment()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is OnTunaSelected) {
            listener = context
        } else {
            throw ClassCastException(context.toString() + " must implement OnTunaSelected.")
        }

        // Get tuna names and descriptions.
        val resources = context.resources
        names = resources.getStringArray(R.array.names)
        descriptions = resources.getStringArray(R.array.descriptions)
        size = resources.getStringArray(R.array.size)

        // Get tuna images.
        val typedArray = resources.obtainTypedArray(R.array.images)
        val imageCount = names.size
        imageResIds = IntArray(imageCount)
        for (i in 0 until imageCount) {
            imageResIds[i] = typedArray.getResourceId(i, 0)
        }
        typedArray.recycle()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_tuna_list, container,
            false)
        val activity = activity as Context
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(activity, 2)
        recyclerView.adapter = TunaListAdapter(activity)
        return view
    }

    internal inner class TunaListAdapter(context: Context) : RecyclerView.Adapter<ViewHolder>() {

        private val layoutInflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val recyclerTunaModelBinding =
                RecyclerItemTunaModelBinding.inflate(layoutInflater, viewGroup, false)
            return ViewHolder(recyclerTunaModelBinding.root, recyclerTunaModelBinding)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val tuna = TunaModel(imageResIds[position], names[position],
                descriptions[position], size[position])
            viewHolder.setData(tuna)
            viewHolder.itemView.setOnClickListener { listener.onTunaSelected(tuna) }
        }

        override fun getItemCount() = names.size
    }

    internal inner class ViewHolder constructor(itemView: View,
                                                private val recyclerItemTunaListBinding:
                                                RecyclerItemTunaModelBinding
    ) :
        RecyclerView.ViewHolder(itemView) {

        fun setData(tunaModel: TunaModel) {
            recyclerItemTunaListBinding.tunaModel = tunaModel
        }
    }

    interface OnTunaSelected {
        fun onTunaSelected(tunaModel: TunaModel)
    }

}