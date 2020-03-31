package io.kroom.app.view.activitymain.user.activityuserfriends.activityfriendsmanagement

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.kroom.app.R
import kotlinx.android.synthetic.main.activity_user_friends_management.*


class UserFriendsManagementActivity : AppCompatActivity() {

    private lateinit var adapterAutocompletion: ArrayAdapter<String>
    private lateinit var adapterFriendsList: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Friends management"
        setContentView(R.layout.activity_user_friends_management)

        adapterAutocompletion = ArrayAdapter(this, R.layout.select_dialog_item_material, java.util.ArrayList())
        adapterFriendsList = ArrayAdapter(this, R.layout.select_dialog_item_material, java.util.ArrayList())

        userFriendsAddInput.setAdapter(adapterAutocompletion)
        userFriendsManagementList.adapter = adapterFriendsList

        val model = ViewModelProviders.of(this).get(UserFriendsManagementViewModel::class.java)
        val autoCompletion = model.getAutoCompletion()
        val friendsList = model.getFriendsList()
        val errorMessage = model.getErrorMessage()

        updateAutoCompletion(autoCompletion.value)
        autoCompletion.observe(this, Observer {
            updateAutoCompletion(it)
        })

        updateListFriends(friendsList.value)
        friendsList.observe(this, Observer {
            updateListFriends(it)
        })

        errorMessage.observe(this, Observer {
            userFriendsAddInput.error = it
        })

        // ---

        userFriendsAddInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                model.updateAutoComplet(userFriendsAddInput.text.toString())
            }
        })

        userFriendsAddSubmit.setOnClickListener {
            model.getAutoCompletion().value?.find {
                it.first == userFriendsAddInput.text.toString()
            }?.second?.let {
                model.addFriend(it)
            }
        }

        userFriendsManagementList.setOnItemClickListener { _, _, position, _ ->
            adapterFriendsList.getItem(position)?.let { username ->
                model.getCacheUSer().find {
                    it.first == username
                }?.second?.let {
                    model.delFriend(it)
                }
            }
        }
        //disableAutofill()
    }
    /*
    @TargetApi(Build.VERSION_CODES.O)
    private fun disableAutofill() {
        window.decorView.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
    }
    */

    private fun updateAutoCompletion(ls: List<Pair<String, Int>>?) {
        ls ?: return
        adapterAutocompletion.clear()
        adapterAutocompletion.addAll(ls.map { it.first })
        adapterAutocompletion.notifyDataSetChanged()
    }

    private fun updateListFriends(ls: List<Pair<String, Int>>?) {
        ls ?: return
        adapterFriendsList.clear()
        adapterFriendsList.addAll(ls.map { it.first })
        adapterFriendsList.notifyDataSetChanged()
        // userFriendsList.invalidate()
    }

}
