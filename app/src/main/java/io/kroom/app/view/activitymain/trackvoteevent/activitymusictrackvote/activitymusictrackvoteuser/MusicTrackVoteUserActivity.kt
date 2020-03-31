package io.kroom.app.view.activitymain.trackvoteevent.activitymusictrackvote.activitymusictrackvoteuser

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.kroom.app.R
import io.kroom.app.view.activitymain.playlist.activityplaylisteditor.user.PlaylistEditorUserViewModel
import io.kroom.app.view.activitymain.playlist.activityplaylisteditor.user.PlaylistEditorUserViewModelFactory
import io.kroom.app.view.activitymain.trackvoteevent.activitymusictrackvote.EXTRA_EVENT_ID
import kotlinx.android.synthetic.main.activity_music_track_vote_event_user.*
import java.util.ArrayList

class MusicTrackVoteUserActivity : AppCompatActivity() {
    private lateinit var adapterAutocompletion: ArrayAdapter<String>
    private lateinit var adapterUserList: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Event Vote User"
        setContentView(R.layout.activity_music_track_vote_event_user)

        val eventId = intent.getIntExtra(EXTRA_EVENT_ID, 0)
        adapterAutocompletion = ArrayAdapter(this, R.layout.select_dialog_item_material, ArrayList())
        adapterUserList = ArrayAdapter(this, R.layout.select_dialog_item_material, ArrayList())

        musicTrackVoteEventUsersAddInput.setAdapter(adapterAutocompletion)
        musicTrackVoteEventUsersFriendsList.adapter = adapterUserList

        val model = ViewModelProviders.of(
            this,
            MusicTrackVoteUserViewModelFactory(this.application, eventId)
        ).get(MusicTrackVoteUserViewModel::class.java)
        val autoCompletion = model.getAutoCompletion()
        val friendsList = model.getFriendsList()
        val errorMessage = model.getErrorMessage()

        updateAutoCompletion(autoCompletion.value)
        autoCompletion.observe(this, Observer {
            updateAutoCompletion(it)
        })

        updateListUser(friendsList.value)
        friendsList.observe(this, Observer {
            updateListUser(it)
        })

        errorMessage.observe(this, Observer {
            musicTrackVoteEventUsersAddInput.error = it
        })

        // ---

        musicTrackVoteEventUsersAddInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                model.updateAutoComplet(musicTrackVoteEventUsersAddInput.text.toString())
            }
        })

        musicTrackVoteEventUsersAddSubmit.setOnClickListener {
            model.getAutoCompletion().value?.find {
                it.first == musicTrackVoteEventUsersAddInput.text.toString()
            }?.second?.let {
                model.addUser(it)
            }
        }

        musicTrackVoteEventUsersFriendsList.setOnItemClickListener { _, _, position, _ ->
            adapterUserList.getItem(position)?.let { username ->
                model.getCacheUSer().find {
                    it.first == username
                }?.second?.let {
                    model.delUser(it)
                }
            }
        }
    }

    private fun updateAutoCompletion(ls: List<Pair<String, Int>>?) {
        ls ?: return
        adapterAutocompletion.clear()
        adapterAutocompletion.addAll(ls.map { it.first })
        adapterAutocompletion.notifyDataSetChanged()
    }

    private fun updateListUser(ls: List<Pair<String, Int>>?) {
        ls ?: return
        adapterUserList.clear()
        adapterUserList.addAll(ls.map { it.first })
        adapterUserList.notifyDataSetChanged()
        // userFriendsList.invalidate()
    }

}
