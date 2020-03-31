package io.kroom.app.view.activitymain.user.activityuserfriends

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import io.kroom.app.R
import io.kroom.app.repo.UserRepo
import io.kroom.app.util.Session
import io.kroom.app.view.activitymain.user.activityuserfriends.activityfriendsinfo.UserFriendsInfoActivity
import io.kroom.app.view.activitymain.user.activityuserfriends.activityfriendsmanagement.UserFriendsManagementActivity
import io.kroom.app.webservice.GraphClient
import kotlinx.android.synthetic.main.activity_user_friends.*

const val EXTRA_NAME_USER_ID = "EXTRA_NAME_USER_ID"

class UserFriendsActivity : AppCompatActivity() {

    private lateinit var adapterFriendsList: ArrayAdapter<String>
    private val cacheUser: MutableSet<Pair<String, Int>> = hashSetOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Friends"
        setContentView(R.layout.activity_user_friends)

        userFriendsUpdate.setOnClickListener {
            val intent = Intent(this, UserFriendsManagementActivity::class.java)
            startActivity(intent)
        }

        adapterFriendsList = ArrayAdapter(this, R.layout.select_dialog_item_material, java.util.ArrayList())

        userFriendsListr.adapter = adapterFriendsList

        // ---

        val client = GraphClient {
            Session.getToken(application)
        }.client

        val userRepo = UserRepo(client)
        val userId = Session.getId(application)

        userId?.let {
            userRepo.byId(it).observe(this, Observer { r ->
                r.onFailure {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                r.onSuccess {
                    val lName = it.UserGetById().user()?.friends()?.mapNotNull {
                        val id = it.id()
                        if (id != null) Pair(it.userName(), id)
                        else null
                    }?.map {
                        cacheUser.add(it)
                        it.first
                    }
                    adapterFriendsList.clear()
                    adapterFriendsList.addAll(lName ?: listOf())
                    adapterFriendsList.notifyDataSetChanged()
                }
            })
        }

        userFriendsListr.setOnItemClickListener { _, _, position, _ ->
            adapterFriendsList.getItem(position)?.let { username ->
                cacheUser.find {
                    it.first == username
                }?.second?.let {
                    val intent = Intent(this, UserFriendsInfoActivity::class.java).apply {
                        putExtra(EXTRA_NAME_USER_ID, it)
                    }
                    startActivity(intent)
                }
            }
        }

    }
}
