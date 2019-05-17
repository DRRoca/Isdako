package com.usep.isdako

import android.content.Context
import org.json.JSONException
import org.json.JSONObject


class Gear(
    val title: String,
    val description: String,
    val imageUrl: String,
    val label: String) {

  companion object {

    fun getGearFromFile(filename: String, context: Context): ArrayList<Gear> {
      val gearsList = ArrayList<Gear>()

      try {
        // Load data
        val jsonString = loadJsonFromAsset("gears.json", context)
        val json = JSONObject(jsonString)
        val gears = json.getJSONArray("gears")

        // Get Gear objects from data
        (0 until gears.length()).mapTo(gearsList) {
          Gear(gears.getJSONObject(it).getString("title"),
              gears.getJSONObject(it).getString("description"),
              gears.getJSONObject(it).getString("image"),

              gears.getJSONObject(it).getString("label"))
        }
      } catch (e: JSONException) {
        e.printStackTrace()
      }

      return gearsList
    }

    private fun loadJsonFromAsset(filename: String, context: Context): String? {
      var json: String? = null

      try {
        val inputStream = context.assets.open(filename)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        json = String(buffer, Charsets.UTF_8)
      } catch (ex: java.io.IOException) {
        ex.printStackTrace()
        return null
      }

      return json
    }
  }
}