package com.usep.isdako

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso


class GearAdapter(private val context: Context,
                    private val dataSource: ArrayList<Gear>) : BaseAdapter() {

  private val inflater: LayoutInflater
      = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

  companion object {
    private val LABEL_COLORS = hashMapOf(

        "High" to R.color.colorHigh,
        "Destructive" to R.color.colorDestructive,
        "Medium" to R.color.colorMedium,
        "Low" to R.color.colorLow
    )
  }

  override fun getCount(): Int {
    return dataSource.size
  }

  override fun getItem(position: Int): Any {
    return dataSource[position]
  }

  override fun getItemId(position: Int): Long {
    return position.toLong()
  }

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val view: View
    val holder: ViewHolder

    // 1
    if (convertView == null) {

      // 2
      view = inflater.inflate(R.layout.list_item_gear, parent, false)

      // 3
      holder = ViewHolder()
      holder.thumbnailImageView = view.findViewById(R.id.gear_list_thumbnail) as ImageView
      holder.titleTextView = view.findViewById(R.id.gear_list_title) as TextView
      holder.subtitleTextView = view.findViewById(R.id.gear_list_subtitle) as TextView
      holder.detailTextView = view.findViewById(R.id.gear_list_detail) as TextView

      // 4
      view.tag = holder
    } else {
      // 5
      view = convertView
      holder = convertView.tag as ViewHolder
    }

    // 6
    val titleTextView = holder.titleTextView
    val subtitleTextView = holder.subtitleTextView
    val detailTextView = holder.detailTextView
    val thumbnailImageView = holder.thumbnailImageView

    val gear = getItem(position) as Gear

    titleTextView.text = gear.title
    subtitleTextView.text = gear.description
    detailTextView.text = gear.label

    Picasso.get().load(gear.imageUrl).placeholder(R.mipmap.ic_launcher).into(thumbnailImageView)

    val titleTypeFace = ResourcesCompat.getFont(context, R.font.josefinsans_bold)
    titleTextView.typeface = titleTypeFace


    val detailTypeFace = ResourcesCompat.getFont(context, R.font.quicksand_bold)
    detailTextView.typeface = detailTypeFace

    detailTextView.setTextColor(
        ContextCompat.getColor(context, LABEL_COLORS[gear.label] ?: R.color.colorPrimary))

    return view
  }

  private class ViewHolder {
    lateinit var titleTextView: TextView
    lateinit var subtitleTextView: TextView
    lateinit var detailTextView: TextView
    lateinit var thumbnailImageView: ImageView
  }
}
