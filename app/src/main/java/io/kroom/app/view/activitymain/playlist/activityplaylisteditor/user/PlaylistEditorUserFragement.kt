package io.kroom.app.view.activitymain.playlist.activityplaylisteditor.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.kroom.app.R
import kotlinx.android.synthetic.main.fragment_playlist_editor_tab_users.*
import java.util.ArrayList

class PlaylistEditorUserFragement(private val playlistId: Int) : Fragment() {
    private lateinit var adapterAutocompletion: ArrayAdapter<String>
    private lateinit var adapterUserList: ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_playlist_editor_tab_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapterAutocompletion = ArrayAdapter(activity!!, R.layout.select_dialog_item_material, ArrayList())
        adapterUserList = ArrayAdapter(activity!!, R.layout.select_dialog_item_material, ArrayList())

        playlistEditorTabUsersAddInput.setAdapter(adapterAutocompletion)
        playlistEditorTabUsersFriendsList.adapter = adapterUserList

        val model = ViewModelProviders.of(
            this,
            PlaylistEditorUserViewModelFactory(this.activity!!.application, playlistId)
        ).get(PlaylistEditorUserViewModel::class.java)
        val autoCompletion = model.getAutoCompletion()
        val friendsList = model.getFriendsList()
        val errorMessage = model.getErrorMessage()

        updateAutoCompletion(autoCompletion.value)
        autoCompletion.observe(this, Observer {
            updateAutoCompletion(it)
        })

        updateListUser(friendsList.value)
        friendsList.observe(this, Observer {
            updateListUser(it)
        })

        errorMessage.observe(this, Observer {
            playlistEditorTabUsersAddInput.error = it
        })

        // ---

        playlistEditorTabUsersAddInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                model.updateAutoComplet(playlistEditorTabUsersAddInput.text.toString())
            }
        })

        playlistEditorTabUsersAddSubmit.setOnClickListener {
            model.getAutoCompletion().value?.find {
                it.first == playlistEditorTabUsersAddInput.text.toString()
            }?.second?.let {
                model.addUser(it)
            }
        }

        playlistEditorTabUsersFriendsList.setOnItemClickListener { _, _, position, _ ->
            adapterUserList.getItem(position)?.let { username ->
                model.getCacheUSer().find {
                    it.first == username
                }?.second?.let {
                    model.delUser(it)
                }
            }
        }
    }

    private fun updateAutoCompletion(ls: List<Pair<String, Int>>?) {
        ls ?: return
        adapterAutocompletion.clear()
        adapterAutocompletion.addAll(ls.map { it.first })
        adapterAutocompletion.notifyDataSetChanged()
    }

    private fun updateListUser(ls: List<Pair<String, Int>>?) {
        ls ?: return
        adapterUserList.clear()
        adapterUserList.addAll(ls.map { it.first })
        adapterUserList.notifyDataSetChanged()
        // userFriendsList.invalidate()
    }
}
