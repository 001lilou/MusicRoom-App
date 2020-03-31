package io.kroom.app.view.activitymain.playlist.activityplaylisteditor.track

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import io.kroom.app.util.Session
import io.kroom.app.webservice.GraphClient
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apollographql.apollo.ApolloSubscriptionCall
import io.kroom.app.graphql.PlayListEditorByIdSubscription
import io.kroom.app.repo.DeezerRepo
import io.kroom.app.repo.PlaylistEditorRepo


class PlaylistEditorOrderViewModelFactory(private val mApplication: Application, private val mParam: Int) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlaylistEditorOrderViewModel(mApplication, mParam) as T
    }
}

data class AutoCompletView(
    val str: String,
    val id: Int
)

class PlaylistEditorOrderViewModel(app: Application, private val playlistId: Int) : AndroidViewModel(app) {
    private val client = GraphClient {
        Session.getToken(getApplication())
    }.client

    private val playlistRepo = PlaylistEditorRepo(client)
    private val deezerRepo = DeezerRepo(client)

    private val autoCompletion: MediatorLiveData<List<AutoCompletView>> = MediatorLiveData()
    private val trackList: MediatorLiveData<List<TrackAdapterOrderModel>> = MediatorLiveData()
    private val errorMessage: MediatorLiveData<String> = MediatorLiveData()
    private val cacheAutoComplet: MutableMap<String, Int> = mutableMapOf()

    private var sCall: ApolloSubscriptionCall<PlayListEditorByIdSubscription.Data>

    init {
        val (lData, subCall) = playlistRepo.subById(playlistId)
        sCall = subCall
        trackList.addSource(lData) { r ->
            r.onFailure {
                errorMessage.postValue(it.message)
                trackList.postValue(null)
            }
            r.onSuccess {
                trackList.postValue(
                    it.PlayListEditorById().playListEditor()?.tracks()?.mapNotNull {
                        val artist = it.artist()
                        artist ?: return@mapNotNull null
                        TrackAdapterOrderModel(it.title(), artist.name(), it.id())
                    }
                )
            }
        }
    }

    fun updateAutoComplet(str: String) {
        autoCompletion.addSource(deezerRepo.search(str)) { r ->
            r.onFailure {
                errorMessage.postValue(it.message)
                autoCompletion.postValue(null)
            }
            r.onSuccess {
                autoCompletion.postValue(
                    it.DeezerSearch().search()?.mapNotNull {
                        AutoCompletView("${it.title()} / ${it.artistName()}", it.id())
                    }?.map {
                        cacheAutoComplet[it.str] = it.id
                        it
                    }
                )
            }
        }
    }

    fun trackUp(trackId: Int) {
        playlistRepo.moveTrack(playlistId, trackId, true)
    }

    fun trackDown(trackId: Int) {
        playlistRepo.moveTrack(playlistId, trackId, false)
    }

    fun trackDel(trackId: Int) {
        playlistRepo.delTrack(playlistId, trackId)
    }

    fun trackAdd(trackId: Int) {
        playlistRepo.addTrack(playlistId, trackId)
    }

    // ---

    fun getAutoCompletion(): LiveData<List<AutoCompletView>> {
        return autoCompletion
    }

    fun getTracksList(): LiveData<List<TrackAdapterOrderModel>> {
        return trackList
    }

    fun getErrorMessage(): LiveData<String> {
        return errorMessage
    }

    fun getCacheAutoComplet(): Map<String, Int> {
        return cacheAutoComplet
    }

    override fun onCleared() {
        super.onCleared()
        sCall.cancel()
    }

}
