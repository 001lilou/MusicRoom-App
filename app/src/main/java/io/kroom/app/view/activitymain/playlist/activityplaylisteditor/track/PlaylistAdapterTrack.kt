package io.kroom.app.view.activitymain.playlist.activityplaylisteditor.track

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import io.kroom.app.R


data class TrackAdapterOrderModel(
    val title: String,
    val artist: String,
    val id: Int
)

class PlaylistAdapterOrder(
    private val dataSet: MutableList<TrackAdapterOrderModel>,
    mContext: Context,
    private val model: PlaylistEditorOrderViewModel
) : ArrayAdapter<TrackAdapterOrderModel>(mContext, R.layout.adapter_playlist_order, dataSet) {

    fun updateDataSet(todos: List<TrackAdapterOrderModel>) {
        dataSet.clear()
        dataSet.addAll(todos)
    }

    private class ViewHolder {
        var cacheTitle: TextView? = null
        var cacheArtistName: TextView? = null
        var cacheBtnUp: Button? = null
        var cacheBtnDown: Button? = null
        var cacheBtnDel: Button? = null
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val dataModel = dataSet[position]
        val viewHolder: ViewHolder // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.adapter_playlist_order, parent, false)
            viewHolder.cacheTitle = convertView.findViewById(R.id.trackAdapterTitle)
            viewHolder.cacheArtistName = convertView.findViewById(R.id.trackAdapterArtist)
            viewHolder.cacheBtnUp = convertView.findViewById(R.id.trackAdapterButtonUp)
            viewHolder.cacheBtnDown = convertView.findViewById(R.id.trackAdapterButtonDown)
            viewHolder.cacheBtnDel = convertView.findViewById(R.id.trackAdapterButtonDel)

            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.cacheTitle?.text = dataModel.title
        viewHolder.cacheArtistName?.text = "by ${dataModel.artist}"
        viewHolder.cacheBtnDel?.setOnClickListener {
            model.trackDel(dataModel.id)
        }
        viewHolder.cacheBtnDown?.setOnClickListener {
            model.trackDown(dataModel.id)
        }
        viewHolder.cacheBtnUp?.setOnClickListener {
            model.trackUp(dataModel.id)
        }

        return convertView!!
    }
}
