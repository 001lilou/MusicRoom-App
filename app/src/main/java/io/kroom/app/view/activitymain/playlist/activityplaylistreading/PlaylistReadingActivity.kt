package io.kroom.app.view.activitymain.playlist.activityplaylistreading

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.deezer.sdk.model.PlayableEntity
import io.kroom.app.R
import io.kroom.app.view.activitymain.playlist.EXTRA_NAME_PLAYLIST_ID
import com.deezer.sdk.network.connect.DeezerConnect
import com.deezer.sdk.player.networkcheck.WifiAndMobileNetworkStateChecker
import com.deezer.sdk.player.TrackPlayer
import com.deezer.sdk.player.event.PlayerWrapperListener
import io.kroom.app.repo.PlaylistEditorRepo
import io.kroom.app.util.Session
import io.kroom.app.view.activitymain.MainActivity
import io.kroom.app.webservice.GraphClient
import kotlinx.android.synthetic.main.activity_playlist_reading.*
import java.lang.Exception



class PlaylistReadingActivity : AppCompatActivity() {

    private lateinit var adapterTracks: PlaylistAdapterReading

    private lateinit var trackPlayer: TrackPlayer

    var indexTrackReading: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_reading)

        val playlistId: Int = intent.getIntExtra(EXTRA_NAME_PLAYLIST_ID, 0)

        val client = GraphClient {
            Session.getToken(application)
        }.client

        val playRepo = PlaylistEditorRepo(client)

        trackPlayer = TrackPlayer(application, MainActivity.deezerConnect, WifiAndMobileNetworkStateChecker())

        adapterTracks = PlaylistAdapterReading(arrayListOf(), this)
        playlistReadingAddListTrack.adapter = adapterTracks

        // ---

        playRepo.byId(playlistId).observe(this, Observer {
            it.onSuccess {
                if (it.PlayListEditorById().errors().isEmpty()) {

                    it.PlayListEditorById().playListEditor()?.tracks()?.mapIndexedNotNull { i, t ->
                        val artist = t.artist()
                        artist ?: return@mapIndexedNotNull null
                        TrackReadingAdapterModel(
                            t.title(),
                            artist.name(),
                            t.id(),
                            i == indexTrackReading
                        )
                    }?.let {
                        adapterTracks.updateDataSet(it)
                        adapterTracks.notifyDataSetChanged()

                        if (it.count() <= indexTrackReading) return@let

                        trackPlayer.playTrack(it[0].id.toLong())
                        trackPlayer.addPlayerListener(object : PlayerWrapperListener {
                            override fun onAllTracksEnded() {}
                            override fun onPlayTrack(p0: PlayableEntity?) {}
                            override fun onRequestException(p0: Exception?, p1: Any?) {}
                            override fun onTrackEnded(p0: PlayableEntity?) {
                                indexTrackReading += 1
                                if (it.count() > indexTrackReading) {
                                    trackPlayer.playTrack(it[indexTrackReading].id.toLong())

                                    adapterTracks.updateDataSet(
                                        it.mapIndexed { i, t ->
                                            t.copy(isReading = indexTrackReading == i)
                                        }
                                    )
                                    adapterTracks.notifyDataSetChanged()

                                }
                            }
                        })

                    }

                } else {
                    Toast.makeText(this, it.PlayListEditorById().errors()[0].messages()[0], Toast.LENGTH_SHORT).show()
                }
            }
            it.onFailure {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        trackPlayer.stop()
        trackPlayer.release()
    }

}
