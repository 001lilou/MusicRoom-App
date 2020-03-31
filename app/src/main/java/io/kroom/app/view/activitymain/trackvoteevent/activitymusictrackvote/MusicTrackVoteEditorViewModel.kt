package io.kroom.app.view.activitymain.trackvoteevent.activitymusictrackvote

import android.app.Application
import androidx.lifecycle.*
import io.kroom.app.graphql.TrackVoteEventDelMutation
import io.kroom.app.graphql.TrackVoteEventUpdateMutation
import io.kroom.app.repo.TrackVoteEventRepo
import io.kroom.app.util.Session
import io.kroom.app.webservice.GraphClient

class MusicTrackVoteEditorViewModelFactory(
    private val mApplication: Application,
    private val mParam: Int
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MusicTrackVoteEditorViewModel(mApplication, mParam) as T
    }
}

data class InputMusicEditor(
    val name: String,
    val begin: String,
    val end: String,
    val latitude: Double,
    val longitude: Double,
    val public: Boolean,
    val locAndSchRestriction: Boolean
)

class MusicTrackVoteEditorViewModel(app: Application, private val eventId: Int) :
    AndroidViewModel(app) {
    private val client = GraphClient {
        Session.getToken(getApplication())
    }.client

    private val repoTrack = TrackVoteEventRepo(client)

    private val inputMusicEditor: MediatorLiveData<InputMusicEditor> = MediatorLiveData()
    private val errorMessage: MediatorLiveData<String> = MediatorLiveData()

    init {
        inputMusicEditor.addSource(repoTrack.byId(eventId)) {
            it.onFailure {
                errorMessage.postValue(it.message)
                inputMusicEditor.postValue(null)
            }
            it.onSuccess {
                if (it.TrackVoteEventById().errors().isNotEmpty()) {
                    errorMessage.postValue(it.TrackVoteEventById().errors()[0].messages()[0])
                } else {
                    val info = it.TrackVoteEventById().trackVoteEvent()
                    info ?: return@addSource
                    inputMusicEditor.postValue(
                        InputMusicEditor(
                            info.name(),
                            info.scheduleBegin() ?: "None",
                            info.scheduleEnd() ?: "None",
                            info.latitude() ?: 0.0,
                            info.longitude() ?: 0.0,
                            info.public_(),
                            info.locAndSchRestriction()
                        )
                    )
                }
            }
        }
    }

    fun delTrackVote(eventId: Int): LiveData<Result<TrackVoteEventDelMutation.Data>> {
        return repoTrack.del(eventId)
    }

    fun update(
        eventId: Int,
        name: String,
        publc: Boolean,
        locAndSchRestriction: Boolean,
        scheduleBegin: String,
        scheduleEnd: String,
        latitude: Double,
        longitude: Double
    ) {
        val userId = Session.getId(getApplication())
        userId ?: return
        inputMusicEditor.addSource(
            repoTrack.update(
                eventId,
                userId,
                name,
                publc,
                locAndSchRestriction,
                scheduleBegin,
                scheduleEnd,
                latitude,
                longitude
            )
        ) {
            it.onFailure {
                errorMessage.postValue(it.message)
                inputMusicEditor.postValue(null)
            }
            it.onSuccess {
                if (it.TrackVoteEventUpdate().errors().isNotEmpty()) {
                    errorMessage.postValue(it.TrackVoteEventUpdate().errors()[0].messages()[0])
                } else {
                    val info = it.TrackVoteEventUpdate().trackVoteEvent()
                    info ?: return@addSource
                    inputMusicEditor.postValue(
                        InputMusicEditor(
                            info.name(),
                            info.scheduleBegin() ?: return@addSource,
                            info.scheduleEnd() ?: return@addSource,
                            info.latitude() ?: return@addSource,
                            info.longitude() ?: return@addSource,
                            info.public_(),
                            info.locAndSchRestriction()
                        )
                    )
                }
            }
        }
    }

    fun getErrorMsg(): LiveData<String> {
        return errorMessage
    }

    fun getInputMusicEditor(): LiveData<InputMusicEditor> {
        return inputMusicEditor
    }

}
