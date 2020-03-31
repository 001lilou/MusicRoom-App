package io.kroom.app.view.activitymain.user.activityuserfriends.activityfriendsmanagement

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import io.kroom.app.repo.UserRepo
import io.kroom.app.webservice.GraphClient
import io.kroom.app.util.Session

class UserFriendsManagementViewModel(app: Application) : AndroidViewModel(app) {
    private val client = GraphClient {
        Session.getToken(getApplication())
    }.client

    private val userRepo = UserRepo(client)
    private val userId = Session.getId(getApplication())

    // ---

    private val autoCompletion: MediatorLiveData<List<Pair<String, Int>>> = MediatorLiveData()
    private val friendsList: MediatorLiveData<List<Pair<String, Int>>> = MediatorLiveData()
    private val errorMessage: MediatorLiveData<String> = MediatorLiveData()

    private val cacheUser: MutableSet<Pair<String, Int>> = hashSetOf()

    init {
        userId?.let {
            friendsList.addSource(userRepo.byId(it)) { r ->
                r.onFailure {
                    errorMessage.postValue(it.message)
                    friendsList.postValue(null)
                }
                r.onSuccess {
                    friendsList.postValue(
                        it.UserGetById().user()?.friends()?.mapNotNull {
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

    fun addFriend(friendId: Int) {
        userId?.let {
            friendsList.addSource(userRepo.addFriend(it, friendId)) { r ->
                r.onFailure {
                    errorMessage.postValue(it.message)
                    friendsList.postValue(null)
                }
                r.onSuccess {
                    friendsList.postValue(
                        it.UserAddFriend().user()?.friends()?.mapNotNull {
                            val id = it.id()
                            if (id != null) Pair(it.userName(), id)
                            else null
                        }?.map {
                            Log.i("ADD", it.toString())

                            cacheUser.add(it)
                            it
                        }
                    )
                }
            }
        }
    }

    fun delFriend(friendId: Int) {
        userId?.let {
            friendsList.addSource(userRepo.deleteFriend(it, friendId)) { r ->
                r.onFailure {
                    errorMessage.postValue(it.message)
                    friendsList.postValue(null)
                }
                r.onSuccess {
                    friendsList.postValue(
                        it.UserDelFriend().user()?.friends()?.mapNotNull {
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
        return friendsList
    }

    fun getErrorMessage(): LiveData<String> {
        return errorMessage
    }

    fun getCacheUSer(): Set<Pair<String, Int>> {
        return cacheUser
    }

}
