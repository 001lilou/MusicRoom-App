package io.kroom.app.view.activitymain.trackvoteevent.activitymusictrackvote

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.kroom.app.R
import io.kroom.app.repo.TrackVoteEventRepo
import io.kroom.app.util.Session
import io.kroom.app.view.activitymain.trackvoteevent.TrackVoteEventsViewModel
import io.kroom.app.view.activitymain.trackvoteevent.model.TrackDictionaryModel
import io.kroom.app.webservice.GraphClient
import kotlinx.android.synthetic.main.activity_music_track_vote_search_add.*
import kotlinx.android.synthetic.main.fragment_playlist_editor_tab_track.*


class MusciTrackVoteSearchAddTrackActivity : AppCompatActivity() {

    lateinit var trackVoteViewModel: TrackVoteEventsViewModel
    var eventId: Int = 0
    lateinit var searchList : ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Search & Add Track"
        setContentView(R.layout.activity_music_track_vote_search_add)
        eventId = intent.getIntExtra(EXTRA_EVENT_ID, 0)

        trackVoteViewModel = ViewModelProviders.of(this).get(TrackVoteEventsViewModel::class.java)

        trackVoteViewModel.getAutoCompletion().observe(this, Observer {
            searchList = ArrayAdapter(this, R.layout.select_dialog_item_material,it.map { it.str })
            musicTrackVoteSearchInputName.setAdapter(searchList)
        })

        musicTrackVoteSearchInputName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
               trackVoteViewModel.updateTrackDictionary(musicTrackVoteSearchInputName.text.toString())
            }
        })

        musicTrackVoteAddBtnTrack.setOnClickListener {
            val inputTrack = musicTrackVoteSearchInputName.text.toString()

            trackVoteViewModel.getTrackVoteEventAddOrUpdateVote(eventId, inputTrack, true)?.observe(this, Observer {
                    it.onSuccess {
                        if (it.TrackVoteEventAddOrUpdateVote().errors().isEmpty()) {
                            Toast.makeText(this, "Track add", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            musicTrackVoteSearchInputName.error = it.TrackVoteEventAddOrUpdateVote().errors()[0].messages()[0]
                        }
                    }
                    it.onFailure {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                })

        }
    }

}
