package io.kroom.app.view.activityauth.usersignup

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
import io.kroom.app.graphql.UserSignUpMutation
import io.kroom.app.util.Session
import kotlinx.android.synthetic.main.fragment_user_sign_up.*

class UserSignUpFragment : Fragment() {

    lateinit var model: UserSignUpViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireActivity().title = "Sign up"
        return inflater.inflate(R.layout.fragment_user_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = ViewModelProviders.of(this).get(UserSignUpViewModel::class.java)

        signUpAction.setOnClickListener {
            this.onSignUp()
        }

        userSignUpGoogle.setOnClickListener {
            onGoogleSignUp()
        }
    }

    private fun onGoogleSignUp() {
        val intent = model.signInGoogleIntent()
        startActivityForResult(intent, GOOGLE_REQUEST_CODE, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.i("DEBUG", "onActivityResult: $requestCode $resultCode")

        if (requestCode == GOOGLE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
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

    private fun onSignUp() {

        clearFields()
        signUpAction.isEnabled = false
        signUpLoading.visibility = View.VISIBLE

        Toast
            .makeText(context, "Registering your account...", Toast.LENGTH_SHORT)
            .show()

        model.signUp(signUpUsername.text.toString(), signUpEmail.text.toString(), signUpPassword.text.toString())
            .observe(this, Observer { observe(it) })
    }

    private fun observe(result: Result<UserSignUpMutation.Data>) {
        result.onFailure(this::onFail)
        result.onSuccess {
            val userSignUp = it.UserSignUp()
            if (userSignUp.errors().isNotEmpty()) {
                userSignUp.errors().forEach { err -> getFieldByName(err.field())?.error = err.messages()[0] }
                return@onSuccess
            }

            userSignUp.user()?.let(this::onSuccess)
        }

        signUpLoading.visibility = View.INVISIBLE
        signUpAction.isEnabled = true
    }


    fun onSuccess(user: UserSignUpMutation.User) {

        Log.i("success-sign-up", "byId sign up: $user")
        Session.setUser(
            this.activity?.application!!,
            user.id()!!, user.email()!!, user.userName(), user.token()!!
        )

        activity?.finish()
    }

    private fun onFail(f: Throwable) {
        Log.println(Log.INFO, "fail-sign-up", "fail: $f")
        Dialogs.errorDialog(context!!, "You encounter an error ${f.message}")
            .show()

    }

    private fun getFieldByName(name: String): EditText? {
        when (name) {
            "email" -> return signUpEmail
            "pass" -> return signUpPassword
            "userName" -> return signUpUsername
        }
        return null
    }

    private fun clearFields() {
        signUpEmail.error = null
        signUpPassword.error = null
        signUpUsername.error = null
    }

    companion object {
        const val GOOGLE_REQUEST_CODE = 8888
    }

}