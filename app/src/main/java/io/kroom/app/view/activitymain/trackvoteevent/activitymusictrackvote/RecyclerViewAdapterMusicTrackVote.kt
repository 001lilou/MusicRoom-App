package io.kroom.app.view.activitymain.trackvoteevent.activitymusictrackvote

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.kroom.app.R
import io.kroom.app.view.activitymain.trackvoteevent.model.TrackVoteEvent
import io.kroom.app.view.activitymain.trackvoteevent.model.TrackWithVote
import kotlinx.android.synthetic.main.fragment_music_track_vote_event_vote.*
import kotlinx.android.synthetic.main.row_item_music_track_vote.view.*

class RecyclerViewAdapterMusicTrackVote(
    val context: Context,
    val trackVoteList: MutableList<TrackWithVote>,
    val onTrackVoteListener: (TrackWithVote, up: Boolean) -> Unit
) : RecyclerView.Adapter<RecyclerViewAdapterMusicTrackVote.TrackVoteHolder>() {


    private var _trackVoteList: MutableList<TrackWithVote> = trackVoteList
    private var _context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackVoteHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.row_item_music_track_vote, parent, false)
        return TrackVoteHolder(itemView, onTrackVoteListener)
    }

    override fun getItemCount(): Int {
        return _trackVoteList.size
    }

    fun setTrackList(tracVotetList: MutableList<TrackWithVote>) {
        _trackVoteList = tracVotetList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TrackVoteHolder, position: Int) {

        holder.bind(_trackVoteList[position])
        if (_trackVoteList[position].track.coverSmall != null) {
            val imageResource =
                _context.getResources()!!.getIdentifier(_trackVoteList[position].track.coverSmall, null, "io.kroom.app")
            // holder.res = ContextCompat.getDrawable(_context, imageResource)!!
        }
    }

    class TrackVoteHolder(itemView: View, val onTrackVoteListener: (TrackWithVote, up: Boolean) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        //  lateinit var res : Drawable
        fun bind(trackWithVote: TrackWithVote) {
            //      itemView.itemMusicTrackVoteCoverSmall.setImageDrawable(res)
            // itemView.itemMusicTrackVoteCoverSmall.drawable = trackWithVote.track.coverSmall
            itemView.itemMusicTrackVoteArtist.text = trackWithVote.track.artist.toString()
            itemView.itemMusicTrackVoteTitle.text = trackWithVote.track.title
            itemView.itemMusicTrackVoteTitle.text = trackWithVote.track.title
            itemView.itemMusicTrackVoteScore.text = trackWithVote.score.toString()
            itemView.itemMusicTrackVoteUp.setOnClickListener {
                onTrackVoteListener(trackWithVote, true)
            }
            itemView.itemMusicTrackVoteDel.setOnClickListener {
                onTrackVoteListener(trackWithVote, false)
            }
        }
    }
}
