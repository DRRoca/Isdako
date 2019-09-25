package com.usep.isdako

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.usep.isdako.databinding.FragmentTunaDetailsBinding
import kotlinx.android.synthetic.main.fragment_tuna_details.*

//1
class TunaDetailsFragment : androidx.fragment.app.Fragment() {

    //2
   private lateinit var model: TunaModel

    companion object {

        private const val TUNAMODEL = "model"


        fun newInstance(tunaModel: TunaModel): TunaDetailsFragment {
            val args = Bundle()
            args.putSerializable(TUNAMODEL, tunaModel)
            val fragment = TunaDetailsFragment()
            fragment.arguments = args
            return fragment
        }

        const val TUNA_TYPE : String = "com.usep.isdako.TUNA_TYPE"
    }

    //3
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentTunaDetailsBinding =
            FragmentTunaDetailsBinding.inflate(inflater, container, false)

        model = arguments!!.getSerializable(TUNAMODEL) as TunaModel
        fragmentTunaDetailsBinding.tunaModel = model
        model.text = String.format(getString(R.string.description_format), model.description, model.size)
        return fragmentTunaDetailsBinding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showMap.setOnClickListener {
            Log.i("Species Type Selected", model.name)
            val activityIntent = Intent (this@TunaDetailsFragment.context, ReportMapActivity::class.java)
            activityIntent.putExtra(TUNA_TYPE,model.name)
            startActivity(activityIntent)

        }
    }
}