package io.kroom.app.view.activitymain.trackvoteevent.event.eventprivate

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import io.kroom.app.R
import io.kroom.app.view.activitymain.trackvoteevent.CustomLayoutManager
import io.kroom.app.view.activitymain.trackvoteevent.TrackVoteEventsViewModel
import io.kroom.app.view.activitymain.trackvoteevent.model.EventModel
import io.kroom.app.view.activitymain.trackvoteevent.activitymusictrackvote.EXTRA_EVENT_ID
import io.kroom.app.view.activitymain.trackvoteevent.activitymusictrackvote.MusicTrackVoteActivity


class TrackVoteEventPrivateFragment : Fragment() {
    private var adapterTrackEventPrivate: RecyclerViewAdapterTrackEventPrivate? = null
    private var recyclerViewEventsPrivate: RecyclerView? = null
    private var trackVoteEventListPrivate: List<EventModel> = listOf()
    lateinit var eventsPrivateViewModel: TrackVoteEventsViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View? = inflater.inflate(R.layout.fragment_list_public_events, container, false)

        recyclerViewEventsPrivate = view?.findViewById(R.id.listPublicEvents)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapterTrackEventPrivate?.setEventList(trackVoteEventListPrivate)

        recyclerViewEventsPrivate?.layoutManager = context?.let { CustomLayoutManager(it) }
        recyclerViewEventsPrivate?.setHasFixedSize(true)
        adapterTrackEventPrivate = RecyclerViewAdapterTrackEventPrivate(trackVoteEventListPrivate) {
            onsTrackVoteEventSelected(it)
        }
        recyclerViewEventsPrivate?.adapter = adapterTrackEventPrivate

        activity?.let {
            eventsPrivateViewModel = ViewModelProviders.of(it).get(TrackVoteEventsViewModel::class.java)
            eventsPrivateViewModel.getTrackVoteEventPrivateList().observe(this, Observer {
                trackVoteEventListPrivate = it
                adapterTrackEventPrivate?.setEventList(trackVoteEventListPrivate)
                adapterTrackEventPrivate?.notifyDataSetChanged()
                // Toast.makeText(this.context, it.size.toString(), Toast.LENGTH_LONG).show()
            })
        }
    }

    fun onsTrackVoteEventSelected(eventItem: EventModel) {
        val musicTrackVoteActivityIntent = Intent(activity, MusicTrackVoteActivity::class.java).apply {
            putExtra(EXTRA_EVENT_ID, eventItem.id)
        }
        startActivity(musicTrackVoteActivityIntent)
    }
}
