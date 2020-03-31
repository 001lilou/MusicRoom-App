package io.kroom.app.view.activitymain.trackvoteevent.activitymusictrackvote

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.kroom.app.R
import io.kroom.app.view.activitymain.trackvoteevent.activitymusictrackvote.activitymusictrackvoteuser.MusicTrackVoteUserActivity
import kotlinx.android.synthetic.main.fragment_music_track_vote_editor.*

class MusicTrackVoteEditorFragement(private val eventId: Int) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_music_track_vote_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model = ViewModelProviders.of(
            this,
            MusicTrackVoteEditorViewModelFactory(this.activity!!.application, eventId)
        ).get(MusicTrackVoteEditorViewModel::class.java)

        model.getErrorMsg().observe(this, Observer {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })

        model.getInputMusicEditor().observe(this, Observer {
            it ?: return@Observer
            musicTrackVoteEventEditorInputName.setText(it.name)
            musicTrackVoteEditorInputBegin.setText(it.begin)
            musicTrackVoteEditorInputEnd.setText(it.end)
            musicTrackVoteEditorInputLongitude.setText(it.longitude.toString())
            musicTrackvoteEditorInputLatitude.setText(it.latitude.toString())
            musicTrackVoteEditorPublicSwitch.isChecked = it.public
            musicTrackVoteEditorLocAndSchRestrictionSwitch.isChecked = it.locAndSchRestriction
        })

        musicTrackVoteEventEditorButtonDel.setOnClickListener {
            model.delTrackVote(eventId).observe(this, Observer {
                it.onFailure {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
                it.onSuccess {
                    if (it.TrackVoteEventDel().errors().isNotEmpty()) {
                        Toast.makeText(context, it.TrackVoteEventDel().errors()[0].messages()[0], Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        activity?.finish()
                    }
                }
            })
        }

        musicTrackVoteEventEditorButtonUser.setOnClickListener {
            val intent = Intent(activity, MusicTrackVoteUserActivity::class.java).apply {
                putExtra(EXTRA_EVENT_ID, eventId)
            }
            startActivity(intent)
        }

        musicTrackVoteEventEditorButtonUpdate.setOnClickListener {
            model.update(
                eventId,
                musicTrackVoteEventEditorInputName.text.toString(),
                musicTrackVoteEditorPublicSwitch.isChecked,
                musicTrackVoteEditorLocAndSchRestrictionSwitch.isChecked,
                musicTrackVoteEditorInputBegin.text.toString(),
                musicTrackVoteEditorInputEnd.text.toString(),
                musicTrackvoteEditorInputLatitude.text.toString().toDoubleOrNull() ?: 0.0,
                musicTrackVoteEditorInputLongitude.text.toString().toDoubleOrNull() ?: 0.0
            )
        }

    }

}
