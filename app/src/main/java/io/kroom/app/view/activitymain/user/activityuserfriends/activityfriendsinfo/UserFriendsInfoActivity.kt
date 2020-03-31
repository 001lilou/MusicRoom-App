package io.kroom.app.view.activitymain.user.activityuserfriends.activityfriendsinfo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import io.kroom.app.R
import io.kroom.app.repo.UserRepo
import io.kroom.app.view.activitymain.user.activityuserfriends.EXTRA_NAME_USER_ID
import io.kroom.app.webservice.GraphClient
import kotlinx.android.synthetic.main.activity_user_friends_info.*
import io.kroom.app.util.Session

class UserFriendsInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "User Info"
        setContentView(R.layout.activity_user_friends_info)

        val userId = intent.getIntExtra(EXTRA_NAME_USER_ID, 0)

        val client = GraphClient {
            Session.getToken(application)
        }.client

        val userRepo = UserRepo(client)

        userRepo.byId(userId).observe(this, Observer { r ->
            r.onFailure {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
            r.onSuccess {
                val usr = it.UserGetById().user()
                usr?.userName()?.let {
                    userFriendsInfoName.text = it
                }
                usr?.email()?.let {
                    userFriendsInfoEmail.text = it
                }
                usr?.latitude()?.let {
                    val lat = it
                    usr.longitude()?.let {
                        userFriendsInfoLocation.text = "$lat/$it"
                    }
                }
                usr?.friends()?.let {
                    val lName = it.map { it.userName() }
                    userFriendsInfoFriends.text = if (lName.isEmpty()) {
                        ""
                    } else if (lName.size == 1) {
                        lName[0]
                    } else {
                        lName.reduce { s1, s2 ->
                            "$s1 $s2"
                        }
                    }
                }
                usr?.musicalPreferences()?.let {
                    val lName = it.map { it.name() }
                    userFriendsInfoPrefMusical.text = if (lName.isEmpty()) {
                        ""
                    } else if (lName.size == 1) {
                        lName[0]
                    } else {
                        lName.reduce { s1, s2 ->
                            "$s1 $s2"
                        }
                    }
                }
            }
        })

    }
}