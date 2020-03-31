package io.kroom.app.TMP

import androidx.appcompat.app.AppCompatActivity

class SignWithGoogle_TODO(var activity: AppCompatActivity)  {

    /*
    private val users = KroomApolloClient.Users


    private lateinit var googletoken: String

    fun setAuthProvider() {
        SimpleSession.setAuthProvider(
            IdpType.GOOGLE,
            SERVER_ID
        )
    }

    fun action() {
        println("HELLO WORLD ACTION")
        SimpleSession.login(this.activity, IdpType.GOOGLE) { result ->
            onGoogleLoginCallback(result)
        }
    }

    private fun onGoogleLoginCallback(result: SimpleAuthResult<Void>?) {
        println("DEBUG JE PASSE ICI")

        if (result == null) {
            println("DEBUG JE SUIS NULL ")
        }
        result?.let {
            if (it.isSuccess) {
                googletoken = SimpleSession.getAccessToken()
                this.onGoogleSignIn()
            } else {
                Dialogs.errorDialog(activity, "Can't sign with google")
                Log.println(Log.ERROR, "error-login-callback", "byId sign up: ${it.errorCode} ${it.errorMessaage}")
            }
        }
    }

    private fun onGoogleSignIn() {

        Toast
            .makeText(MainActivity.app.applicationContext, "connection...", Toast.LENGTH_SHORT)
            .show()

        KroomApolloClient.Users.signGoogleRequest(
            KroomApolloClient.Users.UserGoogleSignRequest(
                googletoken
            )
        ) { res, exception ->

            res?.let(this::onSuccess)
            exception?.let(this::onFail)
        }
    }

    override fun onSuccess(s: UserSignWhithGoolgeMutation.UserSignWithGoogle) {
        if (s.errors().isNotEmpty()) {
            Dialogs.errorDialog(MainActivity.app, "Can't sign up in with google")
            return
        }
        s.byId()?.let { byId ->
            Toast.makeText(activity, "" + byId.token(), Toast.LENGTH_LONG).show()
            Session.setUser(byId.id()!!, byId.email()!!, byId.userName(), byId.token()!!)
            MainActivity.app.goToRoute(Routes.TRACK_VOTE_EVENT)
        }
    }

    override fun onFail(f: ApolloException) {
        Log.println(Log.INFO, "on-google-fail", "fail: $f")
        Dialogs.errorDialog(MainActivity.app, "You encounter an error ${f.message}")
            .show()
    }

    companion object {
        val SERVER_ID = "795222071121-82h8a0k3bhafm1jekm9k8eipmpvcshgr.apps.googleusercontent.com"
    }
    */
}