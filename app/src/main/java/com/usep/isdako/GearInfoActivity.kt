package com.usep.isdako

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity


class GearInfoActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gear_info)

        listView = findViewById(R.id.gears_list_view)

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

