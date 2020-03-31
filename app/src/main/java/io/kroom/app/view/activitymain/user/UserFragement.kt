package io.kroom.app.view.activitymain.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.deezer.sdk.network.connect.SessionStore
import io.kroom.app.view.activitymain.MainActivity
import io.kroom.app.view.activitymain.user.activitydeezersignin.DeezerSigninActivity
import io.kroom.app.view.activitymain.user.activityuserfriends.UserFriendsActivity
import io.kroom.app.view.activitymain.user.activityusermusicpreference.UserMusicPreferencesActivity
import kotlinx.android.synthetic.main.fragment_user.*


class UserFragement : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireActivity().title = "User"
        return inflater.inflate(io.kroom.app.R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionStore = SessionStore()
        if (sessionStore.restore(MainActivity.deezerConnect, context)) {
            Log.i("connected", "deezer connected ok")
            userDeezerSignIn.visibility = View.INVISIBLE
        } else {
            userDeezerSignIn.setOnClickListener {
                val intent = Intent(context, DeezerSigninActivity::class.java)
                startActivity(intent)
            }
        }



        userPreferenceMusical.setOnClickListener {
            val intent = Intent(context, UserMusicPreferencesActivity::class.java)
            startActivity(intent)
        }

        userFriendsButton.setOnClickListener {
            val intent = Intent(context, UserFriendsActivity::class.java)
            startActivity(intent)
        }

        val adapter = ArrayAdapter.createFromResource(
            this.context!!,
            io.kroom.app.R.array.array_privacy, android.R.layout.simple_spinner_item
        )

        spinnerEmail.adapter = adapter
        spinnerFriends.adapter = adapter
        spinnerLocation.adapter = adapter
        spinnerMusicalPreferencesGenre.adapter = adapter

        val model = ViewModelProviders.of(this).get(UserViewModel::class.java)
        val errorMsg = model.getErrorMessage()

        errorMsg.observe(this, Observer {
            it ?: return@Observer
            Toast.makeText(this.context, it, Toast.LENGTH_SHORT).show()
        })

        val spinnerModel = model.getSpinnerModel()

        spinnerModel.observe(this, Observer {
            it ?: return@Observer
            spinnerEmail.setSelection(it.email)
            spinnerFriends.setSelection(it.friends)
            spinnerLocation.setSelection(it.location)
            spinnerMusicalPreferencesGenre.setSelection(it.musicPreference)
        })

        spinnerEmail.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 != spinnerModel.value?.email) {
                    model.updateSpinnerEmail(p2)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        spinnerFriends.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 != spinnerModel.value?.friends) {
                    model.updateSpinnerFreinds(p2)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 != spinnerModel.value?.location) {
                    model.updateSpinnerLocation(p2)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        spinnerMusicalPreferencesGenre.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 != spinnerModel.value?.musicPreference) {
                    model.updateSpinnerMusicalPreference(p2)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

    }

}
