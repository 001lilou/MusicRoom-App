package io.kroom.app.view.activitymain.trackvoteevent.activitymusictrackvote

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.kroom.app.R
import androidx.recyclerview.widget.RecyclerView
import com.deezer.sdk.model.PlayableEntity
import com.deezer.sdk.player.TrackPlayer
import com.deezer.sdk.player.event.PlayerWrapperListener
import com.deezer.sdk.player.networkcheck.WifiAndMobileNetworkStateChecker
import io.kroom.app.view.activitymain.MainActivity
import io.kroom.app.view.activitymain.trackvoteevent.CustomLayoutManager
import io.kroom.app.view.activitymain.trackvoteevent.TrackVoteEventsViewModel
import io.kroom.app.view.activitymain.trackvoteevent.model.TrackVoteEvent
import io.kroom.app.view.activitymain.trackvoteevent.model.TrackWithVote
import kotlinx.android.synthetic.main.fragment_music_track_vote_event_vote.*
import java.lang.Exception
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import java.net.URL


class MusicTrackVoteVoteFragement(val eventId: Int) : Fragment() {
    private var adapterTrackVote: RecyclerViewAdapterMusicTrackVote? = null
    private var recyclerViewTrackVote: RecyclerView? = null
    private val trackVoteList: MutableList<TrackWithVote> = mutableListOf()
    private var trackItemCurrentId: Int? = null
    private var trackVoteViewModel: TrackVoteEventsViewModel? = null
    private var trackPlayer: TrackPlayer? = null
    var idCurrent: Int = 0
    var scCurent: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireActivity().title = "Music tracks vote"
        val view: View? = inflater.inflate(R.layout.fragment_music_track_vote_event_vote, container, false)
        recyclerViewTrackVote = view?.findViewById(R.id.listTrackVoteEvent)

        adapterTrackVote?.setTrackList(trackVoteList)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        music_track_vote_button_search_add_track.setOnClickListener {
            val musicTrackVoteSearchAddActivityIntent =
                Intent(activity, MusciTrackVoteSearchAddTrackActivity::class.java).apply {
                    putExtra(EXTRA_EVENT_ID, eventId)
                }
            startActivity(musicTrackVoteSearchAddActivityIntent)
        }

        activity?.let {
            trackVoteViewModel = ViewModelProviders.of(it).get(TrackVoteEventsViewModel::class.java)
        }

        trackPlayer = trackVoteViewModel?.makePlayer()

        trackPlayer?.addPlayerListener(object : PlayerWrapperListener {
            override fun onAllTracksEnded() {}
            override fun onPlayTrack(p0: PlayableEntity?) {}
            override fun onRequestException(p0: Exception?, p1: Any?) {}
            override fun onTrackEnded(p0: PlayableEntity?) {
                trackVoteViewModel?.nextTrack(eventId)
            }
        })

        recyclerViewTrackVote?.layoutManager = (context?.let { CustomLayoutManager(it) })
        recyclerViewTrackVote?.setHasFixedSize(true)

        trackVoteViewModel?.getErrorMsg()?.observe(this, Observer {
            Toast.makeText(this.context, it, Toast.LENGTH_SHORT).show()
        })

        adapterTrackVote = trackVoteList.let {
            this.context?.let { it1 ->
                RecyclerViewAdapterMusicTrackVote(it1, it) { t, up ->
                    if (up) {
                        trackVoteViewModel?.getTrackVoteEventAddOrUpdateVote(eventId, t.track.id, true)
                    } else {
                        trackVoteViewModel?.delVote(eventId, t.track.id)
                    }
                }
            }
        }

        recyclerViewTrackVote?.adapter = adapterTrackVote

        trackVoteViewModel?.subTrackVoteByid(eventId)?.observe(viewLifecycleOwner, Observer {
            // Toast.makeText(this.context, "SUB", Toast.LENGTH_SHORT).show()
            // Log.e("SUB", it.toString())
            it ?: return@Observer

            trackVoteList.clear()
            it.trackWithVote.sortedBy { it?.score }.reversed().forEach {
                if (it != null) {
                    trackVoteList.add(it)

                    trackVoteList.let { r ->
                        val scor = it.score
                        if (scor > scCurent) {
                            scCurent = scor
                            idCurrent = it.track.id
                        }
                    }
                }
            }

            if (it.currentTrack?.id == null && it.trackWithVote.isNotEmpty()) {
                // Toast.makeText(this.context, "CUR", Toast.LENGTH_SHORT).show()
                trackItemCurrentId = null
                trackVoteViewModel?.nextTrack(eventId)
            } else if (it.currentTrack?.id != trackItemCurrentId) {
                val id = it.currentTrack?.id
                id ?: return@Observer

                trackPlayer?.playTrack(it.currentTrack.id.toLong())
                trackItemCurrentId = it.currentTrack.id
                val thread = Thread(Runnable {
                    try {
                        val url = URL(it.currentTrack.coverMedium)
                        val bmp: Bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                        this.activity?.runOnUiThread {
                            musicTrackVoteCoverMedium.setImageBitmap(bmp)
                            musicTrackVoteCoverMedium.invalidate()
                            coverMediumcontainer.invalidate()
                            getView()?.invalidate()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })
                thread.start()
            }

            // trackPlayer.playTrack(idCurrent.toLong())
            //  eventsPublicViewModel.getSelectedTrackVoteEvent()?.postValue(eventItem)
            adapterTrackVote?.setTrackList(trackVoteList)
            adapterTrackVote?.notifyDataSetChanged()
            recyclerViewTrackVote?.invalidate()
            musicTrackVoteList?.invalidate()
            //list_track_vote?.invalidate()
            getView()?.invalidate()
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        trackPlayer?.stop()
        trackPlayer?.release()
    }
}
