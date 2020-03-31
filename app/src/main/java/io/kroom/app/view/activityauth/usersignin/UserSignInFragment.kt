package io.kroom.app.view.activityauth.usersignin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import io.kroom.app.R
import io.kroom.app.TMP.util.Dialogs
import io.kroom.app.graphql.UserSignInMutation
import io.kroom.app.util.Session
import io.kroom.app.view.activityauth.usersignup.UserSignUpFragment
import kotlinx.android.synthetic.main.fragment_user_sign_in.*


class UserSignInFragment : Fragment() {

    lateinit var model: UserSignInViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireActivity().title = "Sign in"
        return inflater.inflate(R.layout.fragment_user_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = ViewModelProviders.of(this).get(UserSignInViewModel::class.java)


        signInAction.setOnClickListener { onSignIn() }

        signInForgotPassword.setOnClickListener {
            val intent = Intent(context, UserForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        userSignInGoogle.setOnClickListener {
            onGoogleSignIn()
        }
    }

    private fun onGoogleSignIn() {
        val intent = model.signInGoogleIntent()
        startActivityForResult(intent, UserSignUpFragment.GOOGLE_REQUEST_CODE, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        Log.i("DEBUG", "onActivityResult: $requestCode $resultCode")

        if (requestCode == UserSignUpFragment.GOOGLE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            val googleResult = model.getGoogleResult(task)
            googleResult?.observe(this, Observer {
                Log.i("DEBUG", "google result observer")

                it.onFailure { t ->
                    Log.i("DEBUG", "on failure")
                    Toast.makeText(activity, "exception: ${t.message}", Toast.LENGTH_SHORT).show()
                }
                it.onSuccess { userData ->
                    Log.i("DEBUG", "on success activity result")
                    userData.user()?.let { user ->
                        Toast.makeText(activity, user.token(), Toast.LENGTH_LONG).show()

                        Session.setUser(
                            activity?.application!!,
                            user.id()!!,
                            user.email()!!,
                            user.userName(),
                            user.token()!!
                        )
                    }
                    activity?.finish()
                }
            })
        }
    }


    private fun onSignIn() {
        Log.i("TEST", "in onSignIn")
        clearFields()

        signInAction.isEnabled = false
        signInLoading.visibility = View.VISIBLE

        Toast
            .makeText(context, "Login your account...", Toast.LENGTH_SHORT)
            .show()

        model.signIn(musicTrackVoteEventEditorInputName.text.toString(), signInPassword.text.toString())
            .observe(this, Observer { observe(it) })
    }


    private fun observe(result: Result<UserSignInMutation.Data>) {
        Log.i("TEST", "in observe")
        result.onFailure(this::onFail)
        result.onSuccess {
            Log.i("TEST", "in observe -> onSuccess")

            val userSignIn = it.UserSignIn()
            if (userSignIn.errors().isNotEmpty()) {
                userSignIn.errors().forEach { err -> getFieldByName(err.field())?.error = err.messages()[0] }
                return@onSuccess
            }

            userSignIn.user()?.let(this::onSuccess)
        }

        signInLoading.visibility = View.INVISIBLE
        signInAction.isEnabled = true
    }

    private fun onSuccess(user: UserSignInMutation.User) {
        Log.i("success-sign-in", "byId sign up: $user")

        Session.setUser(
            this.activity?.application!!,
            user.id()!!, user.email()!!, user.userName(), user.token()!!
        )

        activity?.finish()
    }


    private fun onFail(f: Throwable) {
        Log.i("fail-sign-in", "fail: $f")

        Dialogs.errorDialog(context!!, "You encounter an error ${f.message}")
            .show()
    }

    private fun getFieldByName(name: String): EditText? {
        when (name) {
            "pass" -> return musicTrackVoteEventEditorInputName
            "userName" -> return signInPassword
        }
        return null
    }

    private fun clearFields() {
        signInPassword.error = null
        musicTrackVoteEventEditorInputName.error = null
    }

    companion object {
        const val GOOGLE_REQUEST_CODE = 9999
    }


}
