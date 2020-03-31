package io.kroom.app.TMP.fragment_TOSORT

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.kroom.app.R

class UserFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireActivity().title = "Settings"
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // settingsFriends.setOnClickListener { MainActivity.app.goToRoute(Routes.USER_FRIENDS) }
        // settingsDebug.setOnClickListener { MainActivity.app.goToRoute(Routes.DEBUG)  }
        // settingsProfile.setOnClickListener {  }
    }
}
