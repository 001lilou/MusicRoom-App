package io.kroom.app.view.activitymain.playlist.activityplaylisteditor

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import io.kroom.app.R
import io.kroom.app.repo.PlaylistEditorRepo
import io.kroom.app.util.Session
import io.kroom.app.view.activitymain.playlist.EXTRA_NAME_PLAYLIST_ID
import io.kroom.app.view.activitymain.playlist.activityplaylisteditor.track.PlaylistEditorTrackFragement
import io.kroom.app.view.activitymain.playlist.activityplaylisteditor.user.PlaylistEditorUserFragement
import io.kroom.app.webservice.GraphClient


class PlaylistEditorActivity : AppCompatActivity() {

    var playlistId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Playlist Editor"
        setContentView(R.layout.activity_playlist_editor)

        playlistId = intent.getIntExtra(EXTRA_NAME_PLAYLIST_ID, 0)

        if (savedInstanceState == null) {
            changeFragment(PlaylistEditorTrackFragement(playlistId))
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // show menu if is master ?
        menuInflater.inflate(R.menu.playlist_editor_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        item ?: return true
        when (item.itemId) {
            R.id.playlistEditorMenuTrack -> changeFragment(PlaylistEditorTrackFragement(playlistId))
            R.id.playlistEditorMenuUser -> changeFragment(PlaylistEditorUserFragement(playlistId))
            R.id.playlistEditorMenuDelete -> {
                val client = GraphClient {
                    Session.getToken(application)
                }.client

                val playlistRepo = PlaylistEditorRepo(client)

                playlistRepo.del(playlistId).observe(this, Observer {
                    it.onSuccess {
                        if (it.PlayListEditorDel().errors().isEmpty()) {
                            finish()
                        } else {
                            Toast.makeText(this, it.PlayListEditorDel().errors()[0].messages()[0], Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    it.onFailure {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
        return true
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.playlistEditorNavigationContainer, fragment)
            .commit()
    }

}
