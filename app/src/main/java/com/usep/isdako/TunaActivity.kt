package com.usep.isdako

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.usep.isdako.TunaDetailsFragment
import com.usep.isdako.TunaListFragment
import com.usep.isdako.TunaModel

class TunaActivity : AppCompatActivity(), TunaListFragment.OnTunaSelected {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.root_layout, TunaListFragment.newInstance(), "tunaList")
                .commit()
        }
    }

    override fun onTunaSelected(tunaModel: TunaModel) {
        val detailsFragment =
            TunaDetailsFragment.newInstance(tunaModel)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.root_layout, detailsFragment, "tunaDetails")
            .addToBackStack(null)
            .commit()
    }

}