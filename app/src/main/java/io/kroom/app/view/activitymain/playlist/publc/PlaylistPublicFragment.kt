package io.kroom.app.view.activitymain.playlist.publc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.kroom.app.R
import io.kroom.app.graphql.PlayListEditorsPublicQuery
import io.kroom.app.repo.PlaylistEditorRepo
import io.kroom.app.util.Session
import io.kroom.app.view.activitymain.playlist.PlaylistPublicAdapter
import io.kroom.app.view.activitymain.playlist.playAdapterModel
import io.kroom.app.webservice.GraphClient
import kotlinx.android.synthetic.main.fragment_playlist_tab_public.*

class PlaylistPublicFragment : Fragment() {

    private val adapterPublic by lazy {
        context?.let {
            PlaylistPublicAdapter(arrayListOf(), it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_playlist_tab_public, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (playlistPublicList.adapter == null) {
            playlistPublicList.adapter = adapterPublic
        }

        val client = GraphClient {
            activity?.let {
                Session.getToken(it.application)
            }
        }.client

        val playRepo = PlaylistEditorRepo(client)

        fun getListPublic(): LiveData<Result<PlayListEditorsPublicQuery.Data>> {
            return playRepo.public()
        }

        val listPublic = getListPublic()

        updatePlaylistPublic(listPublic.value)
        listPublic.observe(this, Observer {
            updatePlaylistPublic(it)
        })

    }

    private fun updatePlaylistPublic(res: Result<PlayListEditorsPublicQuery.Data>?) {
        if (res == null) return
        res.onFailure {
            Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
        }
        res.onSuccess {
            it.PlayListEditorsPublic().mapNotNull {
                val userName = it.userMaster()?.userName()
                val nbTrack = it.tracks()?.count()

                if (userName != null && nbTrack != null)
                    playAdapterModel(
                        it.id(),
                        it.name(),
                        userName,
                        nbTrack,
                        it.public_()
                    )
                else null
            }.sortedBy { it.name }
                .let {
                    adapterPublic?.updateDataSet(it)
                    adapterPublic?.notifyDataSetChanged()
                }
        }
    }

}