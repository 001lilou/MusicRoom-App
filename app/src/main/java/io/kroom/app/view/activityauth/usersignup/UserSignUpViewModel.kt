package io.kroom.app.view.activityauth.usersignup

import android.app.Application
import androidx.lifecycle.LiveData
import io.kroom.app.graphql.UserSignUpMutation
import io.kroom.app.view.activityauth.UserSignWithGoogleViewModel

class UserSignUpViewModel(app: Application) : UserSignWithGoogleViewModel(app) {

    fun signUp(username: String, email: String, password: String): LiveData<Result<UserSignUpMutation.Data>> {
        return userRepo.signUp(username, email, password)
    }

}