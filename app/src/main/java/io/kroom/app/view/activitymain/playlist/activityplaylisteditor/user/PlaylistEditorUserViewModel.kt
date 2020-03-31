package io.kroom.app.view.activitymain.playlist.activityplaylisteditor.user

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import io.kroom.app.repo.PlaylistEditorRepo
import io.kroom.app.repo.UserRepo
import io.kroom.app.util.Session
import io.kroom.app.webservice.GraphClient

class PlaylistEditorUserViewModelFactory(private val mApplication: Application, private val mParam: Int) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlaylistEditorUserViewModel(mApplication, mParam) as T
    }
}

class PlaylistEditorUserViewModel(app: Application, private val playlistId: Int) : AndroidViewModel(app) {
    private val client = GraphClient {
        Session.getToken(getApplication())
    }.client

    private val playRepo = PlaylistEditorRepo(client)
    private val userRepo = UserRepo(client)

    // ---

    private val autoCompletion: MediatorLiveData<List<Pair<String, Int>>> = MediatorLiveData()
    private val userList: MediatorLiveData<List<Pair<String, Int>>> = MediatorLiveData()
    private val errorMessage: MediatorLiveData<String> = MediatorLiveData()

    private val cacheUser: MutableSet<Pair<String, Int>> = hashSetOf()

    init {
        userList.addSource(playRepo.byId(playlistId)) { r ->
            r.onFailure {
                errorMessage.postValue(it.message)
                userList.postValue(null)
            }
            r.onSuccess {
                userList.postValue(
                    it.PlayListEditorById().playListEditor()?.invitedUsers()?.mapNotNull {
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
        userList.addSource(playRepo.addUser(playlistId, userId)) { r ->
            r.onFailure {
                errorMessage.postValue(it.message)
                userList.postValue(null)
            }
            r.onSuccess {
                if (it.PlayListEditorAddUser().errors().isNotEmpty()) {
                    errorMessage.postValue(it.PlayListEditorAddUser().errors()[0].messages()[0])
                } else {
                    userList.postValue(
                        it.PlayListEditorAddUser().playListEditor()?.invitedUsers()?.mapNotNull {
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
        userList.addSource(playRepo.delUser(playlistId, userId)) { r ->
            r.onFailure {
                errorMessage.postValue(it.message)
                userList.postValue(null)
            }
            r.onSuccess {
                if (it.PlayListEditorDelUser().errors().isNotEmpty()) {
                    errorMessage.postValue(it.PlayListEditorDelUser().errors()[0].messages()[0])
                } else {
                    userList.postValue(
                        it.PlayListEditorDelUser().playListEditor()?.invitedUsers()?.mapNotNull {
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