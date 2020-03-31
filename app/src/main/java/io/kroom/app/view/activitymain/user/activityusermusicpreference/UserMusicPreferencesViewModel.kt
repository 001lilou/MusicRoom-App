package io.kroom.app.view.activitymain.user.activityusermusicpreference

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import io.kroom.app.repo.UserRepo
import io.kroom.app.webservice.GraphClient
import io.kroom.app.util.Session

class UserMusicPreferencesViewModel(app: Application) : AndroidViewModel(app) {
    private val client = GraphClient {
        Session.getToken(getApplication())
    }.client

    private val userRepo = UserRepo(client)
    private val userId = Session.getId(getApplication())

    private val autoCompletion: MediatorLiveData<List<Pair<String, Int>>> = MediatorLiveData()
    private val preferencesList: MediatorLiveData<List<Pair<String, Int>>> = MediatorLiveData()
    private val errorMessage: MediatorLiveData<String> = MediatorLiveData()


    init {
        userId?.let {
            preferencesList.addSource(userRepo.byId(it)) { r ->
                r.onFailure {
                    errorMessage.postValue(it.message)
                    preferencesList.postValue(null)
                }
                r.onSuccess {
                    preferencesList.postValue(
                        it.UserGetById().user()?.musicalPreferences()?.mapNotNull {
                            val id = it.id()
                            Pair(it.name(), id)
                        }
                    )
                }
            }

        }
    }

    fun addGenre(genreId: Int) {
        Log.i("add-genre", "add genre $genreId")
        userId?.let {
            preferencesList.addSource(userRepo.addGenre(it, genreId)) { r ->
                r.onFailure {
                    errorMessage.postValue(it.message)
                    preferencesList.postValue(null)
                }
                r.onSuccess {
                    Log.i("genre-success", it.UserAddMusicalPreference().errors().toString())
                    preferencesList.postValue(
                        it.UserAddMusicalPreference().user()?.musicalPreferences()?.mapNotNull {
                            val id = it.id()
                            Pair(it.name(), id)
                        }
                    )
                }
            }
        }
    }

    fun delGenre(genreId: Int) {
        userId?.let {
            preferencesList.addSource(userRepo.deleteGenre(it, genreId)) { r ->
                r.onFailure {
                    errorMessage.postValue(it.message)
                    preferencesList.postValue(null)
                }
                r.onSuccess {
                    preferencesList.postValue(
                        it.UserDelMusicalPreference().user()?.musicalPreferences()?.mapNotNull {
                            val id = it.id()
                            Pair(it.name(), id)
                        }
                    )
                }
            }
        }
    }

    fun getAutoCompletion(): LiveData<List<Pair<String, Int>>> {
        return autoCompletion
    }

    fun getGenreList(): LiveData<List<Pair<String, Int>>> {
        return preferencesList
    }

    fun getErrorMessage(): LiveData<String> {
        return errorMessage
    }

}
