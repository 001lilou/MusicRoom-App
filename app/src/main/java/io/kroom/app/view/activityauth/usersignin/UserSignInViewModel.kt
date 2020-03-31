package io.kroom.app.view.activityauth.usersignin

import android.app.Application
import androidx.lifecycle.LiveData
import io.kroom.app.graphql.UserSignInMutation
import io.kroom.app.view.activityauth.UserSignWithGoogleViewModel

class UserSignInViewModel(app: Application) : UserSignWithGoogleViewModel(app) {

    fun signIn(username: String, password: String): LiveData<Result<UserSignInMutation.Data>> {
        return userRepo.signIn(username, password)
    }

}