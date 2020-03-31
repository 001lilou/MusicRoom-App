package io.kroom.app.view.activitymain.trackvoteevent.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import io.kroom.app.R
import io.kroom.app.view.activitymain.trackvoteevent.event.eventadd.TrackVoteEventAddFragment
import io.kroom.app.view.activitymain.trackvoteevent.event.eventpublic.TrackVoteEventPublicFragment
import io.kroom.app.view.activitymain.trackvoteevent.event.eventprivate.TrackVoteEventPrivateFragment
import kotlinx.android.synthetic.main.fragment_event.*

class TrackVoteEventFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireActivity().title = "Eventlist editor"
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            changeFragment(TrackVoteEventPublicFragment())
        }

        eventListNavigation.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    1 -> changeFragment(TrackVoteEventPrivateFragment())
                    2 -> changeFragment(TrackVoteEventAddFragment())
                    else -> changeFragment(TrackVoteEventPublicFragment())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }

    private fun changeFragment(fragment: Fragment) {
        fragmentManager?.beginTransaction()
            ?.replace(R.id.eventNavigationContainer, fragment)
            ?.commit()
    }
}
