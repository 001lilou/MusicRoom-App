package io.kroom.app.view.activityauth.usersignin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import io.kroom.app.repo.UserRepo
import io.kroom.app.util.Session
import io.kroom.app.webservice.GraphClient
import kotlinx.android.synthetic.main.activity_forgot_password.*

class UserForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Forgot password"
        setContentView(io.kroom.app.R.layout.activity_forgot_password)


        forgotPasswordSubmit.setOnClickListener {
            val client = GraphClient {
                Session.getToken(application)
            }.client

            val userRepo = UserRepo(client)

            if (forgotPasswordPassword.text.isNotBlank() && forgotPasswordEmail.text.isNotBlank()) {
                userRepo.updatePassword(
                    forgotPasswordEmail.text.toString(),
                    forgotPasswordPassword.text.toString()
                ).observe(this, Observer {
                    it.onFailure {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                    it.onSuccess {
                        finish()
                    }
                })
            }
        }

    }
}
