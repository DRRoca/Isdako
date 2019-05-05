package com.usep.isdako

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ListView


class GearActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gear_activity)

        listView = findViewById<ListView>(R.id.gears_list_view)

        val gearsList = Gear.getGearFromFile("gears.json", this)

        val adapter = GearAdapter(this, gearsList)
        listView.adapter = adapter

        val context = this
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedGear = gearsList[position]

            val detailIntent = GearDetailActivity.newIntent(context, selectedGear)

            startActivity(detailIntent)
        }
    }
}