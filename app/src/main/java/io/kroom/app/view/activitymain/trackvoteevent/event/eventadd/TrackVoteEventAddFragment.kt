package io.kroom.app.view.activitymain.trackvoteevent.event.eventadd

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import io.kroom.app.R
import io.kroom.app.repo.TrackVoteEventRepo
import io.kroom.app.util.Session
import io.kroom.app.view.activitymain.trackvoteevent.TrackVoteEventsViewModel
import io.kroom.app.webservice.GraphClient
import kotlinx.android.synthetic.main.fragment_event_add.*


class TrackVoteEventAddFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_add, container, false)
        // lateinit var eventsAddViewModel: TrackVoteEventsViewModel
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val client = GraphClient {
            activity?.let {
                Session.getToken(it.application)
            }

        }.client

        val trackVoteRepo = TrackVoteEventRepo(client)

        eventAddBtnNew.setOnClickListener {

            val inputName = eventAddNameEdit.text.toString()
            val public = eventAddSwitchPublic.isChecked

            Session.getId(activity!!.application)?.let {
                trackVoteRepo.setTrackVoteEventNew(
                    it,
                    inputName,
                    public,
                    false,
                    0,
                    0,
                    1.0,
                    1.0
                ).observe(this, Observer {
                    it.onSuccess {
                        if (it.TrackVoteEventNew().errors().isEmpty()) {
                            Toast.makeText(context, "Event created", Toast.LENGTH_SHORT).show()
                            eventAddNameEdit.setText("")
                        } else {
                            eventAddNameEdit.error = it.TrackVoteEventNew().errors()[0].messages()[0]
                        }
                    }
                    it.onFailure {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

    }
}
