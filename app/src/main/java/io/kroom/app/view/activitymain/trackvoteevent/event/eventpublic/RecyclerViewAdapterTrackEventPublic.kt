package io.kroom.app.view.activitymain.trackvoteevent.event.eventpublic


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.kroom.app.R
import io.kroom.app.view.activitymain.trackvoteevent.model.EventModel
import kotlinx.android.synthetic.main.row_item_track_vote_event.view.*

class RecyclerViewAdapterTrackEventPublic(
    val trackVoteEventList: List<EventModel>,
    val onTrackVoteEventListener: (EventModel) -> Unit
) : RecyclerView.Adapter<RecyclerViewAdapterTrackEventPublic.TrackEventVoteHolder>() {


    private var _trackVoteEventList: List<EventModel> = trackVoteEventList

    // private lateinit var trackVoteEvent: List<EventModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackEventVoteHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.row_item_track_vote_event, parent, false)
        return TrackEventVoteHolder(itemView)
    }

    override fun getItemCount(): Int {
        return _trackVoteEventList.size
    }

    fun setEventList(tracVoteEventList: List<EventModel>) {
        _trackVoteEventList = tracVoteEventList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TrackEventVoteHolder, position: Int) {

        holder.bind(_trackVoteEventList[position])
        holder.clickableItem1.setOnClickListener { onTrackVoteEventListener(_trackVoteEventList[position]) }
        holder.clickableItem2.setOnClickListener { onTrackVoteEventListener(_trackVoteEventList[position]) }
    }

    class TrackEventVoteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clickableItem1 = itemView.linearlayoutEvent1
        val clickableItem2 = itemView.linearlayoutEvent2
        fun bind(trackVoteEvent: EventModel) {
            itemView.itemTrackVoteEventUserMaster.text = "By " + trackVoteEvent.userMasterName
            itemView.itemTrackVoteEventName.text = "Event: " + trackVoteEvent.name
            itemView.itemTrackVoteEventScheduleBegin.text = "Begin " + trackVoteEvent.scheduleBegin.toString()
            itemView.itemTrackVoteEventScheduleEnd.text =  "End " + trackVoteEvent.scheduleEnd.toString()
            itemView.itemTrackVoteEventLocation.text =
                "long: " + trackVoteEvent.longitude.toString() + " /  lat: " + trackVoteEvent.latitude.toString()

        }
    }
}