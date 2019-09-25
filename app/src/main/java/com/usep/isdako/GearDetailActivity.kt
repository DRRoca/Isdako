package com.usep.isdako

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.webkit.WebView


class GearDetailActivity : AppCompatActivity() {

  private lateinit var webView: WebView

  companion object {
    const val EXTRA_TITLE = "title"
    const val EXTRA_IMAGE = "image"

    fun newIntent(context: Context, gear: Gear): Intent {
      val detailIntent = Intent(context, GearDetailActivity::class.java)

      detailIntent.putExtra(EXTRA_TITLE, gear.title)
      detailIntent.putExtra(EXTRA_IMAGE, gear.imageUrl)

      return detailIntent
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_gear_detail)

    val title = intent.extras.getString(EXTRA_TITLE)
    val url = intent.extras.getString(EXTRA_IMAGE)

    setTitle(title)

    webView = findViewById(R.id.detail_web_view)

    webView.loadUrl(url)
  }
}
