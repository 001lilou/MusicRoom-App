package io.kroom.app.view.activitymain.trackvoteevent.activitymusictrackvote.activitymusictrackvoteuser

import android.app.Application
import androidx.lifecycle.*
import io.kroom.app.repo.TrackVoteEventRepo
import io.kroom.app.repo.UserRepo
import io.kroom.app.util.Session
import io.kroom.app.webservice.GraphClient

class MusicTrackVoteUserViewModelFactory(private val mApplication: Application, private val mParam: Int) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MusicTrackVoteUserViewModel(mApplication, mParam) as T
    }
}

class MusicTrackVoteUserViewModel (app: Application, private val eventId: Int) : AndroidViewModel(app) {
    private val client = GraphClient {
        Session.getToken(getApplication())
    }.client


    private val repoTrack = TrackVoteEventRepo(client)
    private val userRepo = UserRepo(client)

    private val autoCompletion: MediatorLiveData<List<Pair<String, Int>>> = MediatorLiveData()
    private val userList: MediatorLiveData<List<Pair<String, Int>>> = MediatorLiveData()
    private val errorMessage: MediatorLiveData<String> = MediatorLiveData()

    private val cacheUser: MutableSet<Pair<String, Int>> = hashSetOf()

    init {
        userList.addSource(repoTrack.byId(eventId)) { r ->
            r.onFailure {
                errorMessage.postValue(it.message)
                userList.postValue(null)
            }
            r.onSuccess {
                userList.postValue(
                    it.TrackVoteEventById().trackVoteEvent()?.userInvited()?.mapNotNull {
                        val id = it.id()
                        if (id != null) Pair(it.userName(), id)
                        else null
                    }?.map {
                        cacheUser.add(it)
                        it
                    }
                )
            }

        }
    }

    fun updateAutoComplet(prefix: String) {
        autoCompletion.addSource(userRepo.userNameAutocompletion(prefix)) { r ->
            r.onFailure {
                errorMessage.postValue(it.message)
                autoCompletion.postValue(null)
            }
            r.onSuccess {
                autoCompletion.postValue(
                    it.UserNameAutocompletion().mapNotNull {
                        val id = it.id()
                        if (id != null) Pair(it.userName(), id)
                        else null
                    }.map {
                        cacheUser.add(it)
                        it
                    }
                )
            }
        }
    }

    fun addUser(userId: Int) {
        userList.addSource(repoTrack.addUser(eventId, userId)) { r ->
            r.onFailure {
                errorMessage.postValue(it.message)
                userList.postValue(null)
            }
            r.onSuccess {
                if (it.TrackVoteEventAddUser().errors().isNotEmpty()) {
                    errorMessage.postValue(it.TrackVoteEventAddUser().errors()[0].messages()[0])
                } else {
                    userList.postValue(
                        it.TrackVoteEventAddUser().trackVoteEvent()?.userInvited()?.mapNotNull {
                            val id = it.id()
                            if (id != null) Pair(it.userName(), id)
                            else null
                        }?.map {
                            cacheUser.add(it)
                            it
                        }
                    )
                }
            }
        }
    }

    fun delUser(userId: Int) {
        userList.addSource(repoTrack.delUser(eventId, userId)) { r ->
            r.onFailure {
                errorMessage.postValue(it.message)
                userList.postValue(null)
            }
            r.onSuccess {
                if (it.TrackVoteEventDelUser().errors().isNotEmpty()) {
                    errorMessage.postValue(it.TrackVoteEventDelUser().errors()[0].messages()[0])
                } else {
                    userList.postValue(
                        it.TrackVoteEventDelUser().trackVoteEvent()?.userInvited()?.mapNotNull {
                            val id = it.id()
                            if (id != null) Pair(it.userName(), id)
                            else null
                        }?.map {
                            cacheUser.add(it)
                            it
                        }
                    )
                }
            }
        }
    }

    fun getAutoCompletion(): LiveData<List<Pair<String, Int>>> {
        return autoCompletion
    }

    fun getFriendsList(): LiveData<List<Pair<String, Int>>> {
        return userList
    }

    fun getErrorMessage(): LiveData<String> {
        return errorMessage
    }

    fun getCacheUSer(): Set<Pair<String, Int>> {
        return cacheUser
    }


}