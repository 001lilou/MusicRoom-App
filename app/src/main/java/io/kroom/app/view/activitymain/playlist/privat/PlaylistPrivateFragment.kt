package io.kroom.app.view.activitymain.playlist.privat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.kroom.app.R
import io.kroom.app.graphql.PlayListEditorByUserIdQuery
import io.kroom.app.repo.PlaylistEditorRepo
import io.kroom.app.util.Session
import io.kroom.app.view.activitymain.playlist.PlaylistPublicAdapter
import io.kroom.app.view.activitymain.playlist.playAdapterModel
import io.kroom.app.webservice.GraphClient
import kotlinx.android.synthetic.main.fragment_playlist_tab_invited.*

class PlaylistPrivateFragment : Fragment() {

    private val adapterIvited by lazy {
        context?.let {
            PlaylistPublicAdapter(arrayListOf(), it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_playlist_tab_invited, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (playlistInvitedList.adapter == null) {
            playlistInvitedList.adapter = adapterIvited
        }

        val client = GraphClient {
            activity?.let {
                Session.getToken(it.application)
            }
        }.client

        val playRepo = PlaylistEditorRepo(client)

        fun getListInvited(): LiveData<Result<PlayListEditorByUserIdQuery.Data>>? {
            return Session.getId(activity!!.application)?.let {
                playRepo.byUserId(it)
            }
        }

        val listInvited = getListInvited()

        updatePlaylistInvited(listInvited?.value)
        listInvited?.observe(this, Observer {
            updatePlaylistInvited(it)
        })

    }

    private fun updatePlaylistInvited(res: Result<PlayListEditorByUserIdQuery.Data>?) {
        if (res == null) return
        res.onFailure {
            Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
        }
        res.onSuccess {
            it.PlayListEditorByUserId().playListEditor()?.mapNotNull {
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
            }?.filter { !it.public }
                ?.sortedBy { it.name }
                ?.let {
                    adapterIvited?.updateDataSet(it)
                    adapterIvited?.notifyDataSetChanged()
                }
        }
    }

}