package io.kroom.app.view.activityauth

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import io.kroom.app.R
import io.kroom.app.graphql.UserSignWhithGoolgeMutation
import io.kroom.app.repo.UserRepo
import io.kroom.app.util.Session
import io.kroom.app.webservice.GraphClient
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

open class UserSignWithGoogleViewModel(app: Application) : AndroidViewModel(app) {

    private val client = GraphClient {
        Session.getToken(getApplication())
    }.client

    protected val userRepo = UserRepo(client)

    private var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(app.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    private var googleSignInClient: GoogleSignInClient
    private lateinit var googleToken: String

    init {
        googleSignInClient = GoogleSignIn.getClient(app, gso)
    }

    fun signInGoogleIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun getGoogleResult(completedTask: Task<GoogleSignInAccount>)
            : LiveData<Result<UserSignWhithGoolgeMutation.UserSignWithGoogle>>? {

        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)

            googleToken = account?.idToken.toString()


            googleToken.let {
                return Transformations.map(userRepo.signGoogleRequest(it)) { r ->
                    r.onFailure {
                        return@map failure<UserSignWhithGoolgeMutation.UserSignWithGoogle>(it)
                    }
                    r.onSuccess {
                        val id = it.UserSignWithGoogle().user()?.id()
                        val email = it.UserSignWithGoogle().user()?.email()
                        val userNAme = it.UserSignWithGoogle().user()?.userName()
                        val token = it.UserSignWithGoogle().user()?.token()
                        if (id != null && email != null && userNAme != null && token != null) {
                            Session.setUser(
                                getApplication(),
                                id,
                                email,
                                userNAme,
                                token
                            )
                        }
                        Log.i("TOKENNNNNNNNNNNNNNNNNN", Session.getToken(getApplication()).toString() )
                        return@map success(it.UserSignWithGoogle())
                    }
                    null
                }
            }
        } catch (e: ApiException) {
            Log.e(TAG, e.message)
        }
        return null
    }
}