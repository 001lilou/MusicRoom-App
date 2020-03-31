package io.kroom.app.view.activitymain.playlist.activityplaylistreading

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import io.kroom.app.R


data class TrackReadingAdapterModel(
    val title: String,
    val artist: String,
    val id: Int,
    val isReading: Boolean
)

class PlaylistAdapterReading(
    private val dataSet: MutableList<TrackReadingAdapterModel>,
    mContext: Context
) : ArrayAdapter<TrackReadingAdapterModel>(mContext, R.layout.adapter_playlist_reading, dataSet) {

    fun updateDataSet(todos: List<TrackReadingAdapterModel>) {
        dataSet.clear()
        dataSet.addAll(todos)
    }

    private class ViewHolder {
        var cacheTitle: TextView? = null
        var cacheArtistName: TextView? = null
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val dataModel = dataSet[position]
        val viewHolder: ViewHolder // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.adapter_playlist_reading, parent, false)
            viewHolder.cacheTitle = convertView.findViewById(R.id.adapterPlaylistReadingTrackTitle)
            viewHolder.cacheArtistName = convertView.findViewById(R.id.adapterPlaylistReadingTrackArtist)

            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.cacheTitle?.text = dataModel.title
        viewHolder.cacheArtistName?.text = "by ${dataModel.artist}"
        convertView?.setBackgroundColor(
            if (dataModel.isReading)
                Color.argb(255, 0, 153, 139)
            else
                Color.argb(255, 0, 133, 119)
        )

        return convertView!!
    }
}
