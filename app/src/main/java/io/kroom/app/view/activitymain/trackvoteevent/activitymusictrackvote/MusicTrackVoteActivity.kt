package io.kroom.app.view.activitymain.trackvoteevent.activitymusictrackvote

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.kroom.app.R

const val EXTRA_EVENT_ID = "EXTRA_EVENT_ID"

class MusicTrackVoteActivity : AppCompatActivity() {

    var eventId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Event Vote"
        setContentView(R.layout.activity_music_track_vote_event)

        eventId = intent.getIntExtra(EXTRA_EVENT_ID, 0)

        if (savedInstanceState == null) {
            changeFragment(MusicTrackVoteVoteFragement(eventId))
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // show menu if is master ?
        menuInflater.inflate(R.menu.music_track_vote_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        item ?: return true
        when (item.itemId) {
            R.id.musicTrackVoteEditor -> changeFragment(MusicTrackVoteEditorFragement(eventId))
            R.id.musicTrackVoteVote -> changeFragment(MusicTrackVoteVoteFragement(eventId))
        }
        return true
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.musicTrackVoteEventContainer, fragment)
            .commit()
    }
}
