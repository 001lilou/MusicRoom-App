package io.kroom.app.view.activitymain.user.activitydeezersignin

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import com.deezer.sdk.model.Permissions.*
import com.deezer.sdk.network.connect.event.DialogListener
import io.kroom.app.view.activitymain.MainActivity
import kotlinx.android.synthetic.main.activity_deezer_sign_in.*
import com.deezer.sdk.network.connect.SessionStore




class DeezerSigninActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Deezer sign in"
        setContentView(io.kroom.app.R.layout.activity_deezer_sign_in)


        connectToDeezer()

    }

    @UiThread
    private fun connectToDeezer() {
        val permissions = arrayOf(BASIC_ACCESS, MANAGE_LIBRARY, LISTENING_HISTORY)

        val listener = object : DialogListener {

            override fun onComplete(values: Bundle) {

                Log.i("complete", "DEEZER OK")
                deezerSignInText.text = "Successful login! Go back to listen music."
                deezerSignInLoadingBar.visibility = View.INVISIBLE

                saveDeezerConnect()
            }

            override fun onCancel() {
                Log.i("cancelled", "DEEZER CANCELLED")
                deezerSignInText.text = "You have cancelled the operation"
                deezerSignInLoadingBar.visibility = View.INVISIBLE

            }

            override fun onException(e: Exception) {
                Log.i("sign-deezer-exception", e.localizedMessage)
                deezerSignInText.text = "An error occured"
                deezerSignInLoadingBar.visibility = View.INVISIBLE
            }
        }

        MainActivity.deezerConnect.authorize(this, permissions, listener)
    }

    private fun saveDeezerConnect() {
        val sessionStore = SessionStore()
        sessionStore.save(MainActivity.deezerConnect, this)
    }

}
