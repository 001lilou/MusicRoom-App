package io.kroom.app.view.activitymain.user.activityusermusicpreference

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.kroom.app.R
import kotlinx.android.synthetic.main.activity_music_preferences.*
import java.util.*


class UserMusicPreferencesActivity : AppCompatActivity() {

    private lateinit var adapterAutocompletion: ArrayAdapter<String>
    private lateinit var adapterGenreList: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Music preferences"
        setContentView(R.layout.activity_music_preferences)

        adapterAutocompletion = ArrayAdapter(this, R.layout.select_dialog_item_material, ArrayList())
        adapterGenreList = ArrayAdapter(this, R.layout.select_dialog_item_material, ArrayList())

        userMusicPreferencesAddInput.setAdapter(adapterAutocompletion)
        userMusicPreferencessManagementList.adapter = adapterGenreList

        val model = ViewModelProviders.of(this).get(UserMusicPreferencesViewModel::class.java)
        val genresList = model.getGenreList()
        val errorMessage = model.getErrorMessage()

        updateAutoCompletion()


        updateListPreferenceGenre(genresList.value)

        genresList.observe(this, Observer {
            updateListPreferenceGenre(it)
        })

        errorMessage.observe(this, Observer {
            userMusicPreferencesAddInput.error = it
        })


        userMusicPreferencesAddSubmit.setOnClickListener {
            genres.find {
                it.name == userMusicPreferencesAddInput.text.toString()
            }?.let {
                model.addGenre(it.id)
            }
        }

        userMusicPreferencessManagementList.setOnItemClickListener { _, _, position, _ ->
            adapterGenreList.getItem(position)?.let { genre ->
                genres.find {
                    it.name == genre
                }?.id?.let {
                    model.delGenre(it)
                }
            }
        }

        // disableAutofill()
    }
    /*
    @TargetApi(Build.VERSION_CODES.O)
    private fun disableAutofill() {
        window.decorView.importantForAutofill = IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
    }
    */

    private fun updateAutoCompletion() {
        adapterAutocompletion.clear()
        adapterAutocompletion.addAll(genres.map { it.name })
        adapterAutocompletion.notifyDataSetChanged()
    }

    private fun updateListPreferenceGenre(ls: List<Pair<String, Int>>?) {
        ls ?: return
        adapterGenreList.clear()
        adapterGenreList.addAll(ls.map { it.first })
        adapterGenreList.notifyDataSetChanged()
    }

}