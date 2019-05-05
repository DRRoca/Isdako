package com.usep.isdako

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.usep.isdako.databinding.FragmentTunaDetailsBinding

//1
class TunaDetailsFragment : Fragment() {

    //2
    companion object {

        private const val TUNAMODEL = "model"

        fun newInstance(tunaModel: TunaModel): TunaDetailsFragment {
            val args = Bundle()
            args.putSerializable(TUNAMODEL, tunaModel)
            val fragment = TunaDetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    //3
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentTunaDetailsBinding =
            FragmentTunaDetailsBinding.inflate(inflater, container, false)

        val model = arguments!!.getSerializable(TUNAMODEL) as TunaModel
        fragmentTunaDetailsBinding.tunaModel = model
        model.text = String.format(getString(R.string.description_format), model.description, model.size)
        return fragmentTunaDetailsBinding.root
    }

}