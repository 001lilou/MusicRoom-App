package io.kroom.app.view.activitymain.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import io.kroom.app.graphql.type.PrivacyEnum
import io.kroom.app.repo.UserRepo
import io.kroom.app.util.Session
import io.kroom.app.webservice.GraphClient

data class UserSpinnerModel(
    val email: Int,
    val friends: Int,
    val location: Int,
    val musicPreference: Int
)

private fun PrivacyEnum.privacyEnumToPos(): Int {
    return when (this) {
        PrivacyEnum.FRIENDS -> 1
        PrivacyEnum.PUBLIC -> 2
        else -> 0
    }
}

private fun Int.posToPrivacyEnum(): PrivacyEnum {
    return when (this) {
        1 -> PrivacyEnum.FRIENDS
        2 -> PrivacyEnum.PUBLIC
        else -> PrivacyEnum.PRIVATE
    }
}

class UserViewModel(app: Application) : AndroidViewModel(app) {
    private val client = GraphClient {
        Session.getToken(getApplication())
    }.client
    private val userRepo = UserRepo(client)
    private val userId = Session.getId(getApplication())
    private val spinnerModel: MediatorLiveData<UserSpinnerModel> = MediatorLiveData()
    private val errorMessage: MediatorLiveData<String> = MediatorLiveData()

    init {
        userId?.let {
            spinnerModel.addSource(userRepo.byId(it)) { r ->
                r.onFailure {
                    errorMessage.postValue(it.message)
                }
                r.onSuccess {
                    if (it.UserGetById().errors().isNotEmpty()) {
                        errorMessage.postValue(
                            it.UserGetById().errors()[0].messages()[0]
                        )
                    }
                    val emailP = it.UserGetById().user()?.privacy()?.email() ?: return@onSuccess
                    val friendsP = it.UserGetById().user()?.privacy()?.friends() ?: return@onSuccess
                    val locationP = it.UserGetById().user()?.privacy()?.location() ?: return@onSuccess
                    val musicPrefP = it.UserGetById().user()?.privacy()?.musicalPreferencesGenre() ?: return@onSuccess
                    spinnerModel.postValue(
                        UserSpinnerModel(
                            emailP.privacyEnumToPos(),
                            friendsP.privacyEnumToPos(),
                            locationP.privacyEnumToPos(),
                            musicPrefP.privacyEnumToPos()
                        )
                    )
                }
            }

        }
    }

    private fun sendUpdatePrivacy(nm: UserSpinnerModel) {
        userId?.let {
            spinnerModel.addSource(
                userRepo.updatePrivacy(
                    it,
                    nm.email.posToPrivacyEnum(),
                    nm.friends.posToPrivacyEnum(),
                    nm.location.posToPrivacyEnum(),
                    nm.musicPreference.posToPrivacyEnum()
                )
            ) { r ->
                r.onFailure {
                    errorMessage.postValue(it.message)
                }
                r.onSuccess {
                    if (it.UserUpdatePrivacy().errors().isNotEmpty()) {
                        errorMessage.postValue(
                            it.UserUpdatePrivacy().errors()[0].messages()[0]
                        )
                    }
                    val emailP = it.UserUpdatePrivacy().user()?.privacy()?.email() ?: return@onSuccess
                    val friendsP = it.UserUpdatePrivacy().user()?.privacy()?.friends() ?: return@onSuccess
                    val locationP = it.UserUpdatePrivacy().user()?.privacy()?.location() ?: return@onSuccess
                    val musicPrefP =
                        it.UserUpdatePrivacy().user()?.privacy()?.musicalPreferencesGenre() ?: return@onSuccess
                    spinnerModel.postValue(
                        UserSpinnerModel(
                            emailP.privacyEnumToPos(),
                            friendsP.privacyEnumToPos(),
                            locationP.privacyEnumToPos(),
                            musicPrefP.privacyEnumToPos()
                        )
                    )
                }
            }

        }
    }

    // ---

    fun getSpinnerModel(): LiveData<UserSpinnerModel> {
        return spinnerModel
    }

    fun getErrorMessage(): LiveData<String> {
        return errorMessage
    }

    fun updateSpinnerEmail(n: Int) {
        spinnerModel.value?.let {
            sendUpdatePrivacy(
                it.copy(email = n)
            )
        }
    }

    fun updateSpinnerFreinds(n: Int) {
        spinnerModel.value?.let {
            sendUpdatePrivacy(
                it.copy(friends = n)
            )
        }
    }

    fun updateSpinnerLocation(n: Int) {
        spinnerModel.value?.let {
            sendUpdatePrivacy(
                it.copy(location = n)
            )
        }
    }

    fun updateSpinnerMusicalPreference(n: Int) {
        spinnerModel.value?.let {
            sendUpdatePrivacy(
                it.copy(musicPreference = n)
            )
        }
    }

}
