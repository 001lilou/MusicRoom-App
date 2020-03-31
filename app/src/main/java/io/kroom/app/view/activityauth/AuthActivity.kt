package io.kroom.app.view.activityauth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import io.kroom.app.R
import io.kroom.app.view.activityauth.usersignin.UserSignInFragment
import io.kroom.app.view.activityauth.usersignup.UserSignUpFragment
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        if (savedInstanceState == null) {
            changeFragment(0)
        }

        authTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                changeFragment(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun changeFragment(position: Int) {
        if (position == 0)
            supportFragmentManager.beginTransaction()
                .replace(R.id.authFragmentContainer, UserSignInFragment())
                .commit()
        else
            supportFragmentManager.beginTransaction()
                .replace(R.id.authFragmentContainer, UserSignUpFragment())
                .commit()
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}