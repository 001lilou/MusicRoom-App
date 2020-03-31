package io.kroom.app.view.activitymain.trackvoteevent


import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import androidx.lifecycle.Transformations.map
import com.apollographql.apollo.ApolloSubscriptionCall
import com.deezer.sdk.player.TrackPlayer
import com.deezer.sdk.player.networkcheck.WifiAndMobileNetworkStateChecker
import io.kroom.app.graphql.*
import io.kroom.app.repo.DeezerRepo
import io.kroom.app.repo.TrackVoteEventRepo
import io.kroom.app.util.Session
import io.kroom.app.view.activitymain.MainActivity
import io.kroom.app.view.activitymain.trackvoteevent.model.*

import io.kroom.app.webservice.GraphClient


class TrackVoteEventsViewModel(app: Application) : AndroidViewModel(app) {
    private val client = GraphClient {
        Session.getToken(getApplication())
    }.client

    private val trackVoteEventRepo = TrackVoteEventRepo(client)
    private val deezerSearchRepo = DeezerRepo(client)
    private val autoCompletion: MediatorLiveData<List<TrackDictionaryModel>> = MediatorLiveData()
    private val errorMessage: MediatorLiveData<String> = MediatorLiveData()
    private val cacheAutoComplet: MutableMap<String, Int> = mutableMapOf()
    private var sCall: ApolloSubscriptionCall<TrackVoteEventByIdSubscription.Data>? = null

    override fun onCleared() {
        super.onCleared()
        sCall?.cancel()
    }

    fun subTrackVoteByid(id: Int): LiveData<TrackVoteEvent?> {
        sCall?.cancel()
        val (live, sub) = trackVoteEventRepo.subById(id)
        sCall = sub
        return map(live) {
            it.onSuccess {
                return@map it.TrackVoteEventById().trackVoteEvent().let {
                    Log.e("SUB", it.toString())

                    TrackVoteEvent(
                        it?.id() ?: return@map null,
                        it.userMaster()?.userName() ?: return@map null,
                        it.name(),
                        it.public_(),
                        it.currentTrack().let {
                            if (it == null) null
                            else CurrentTrack(
                                it.id(),
                                it.title(),
                                it.album()?.coverMedium() ?: ""
                            )
                        },
                        it.trackWithVote()?.map {
                            TrackWithVote(
                                it.track().let {
                                    TrackModel(
                                        it.id(),
                                        it.title(),
                                        it.album()?.coverSmall() ?: return@map null,
                                        2,
                                        it.artist()?.name() ?: return@map null
                                    )
                                },
                                it.score(),
                                it.nb_vote_up(),
                                it.nb_vote_down()
                            )
                        } ?: listOf(),
                        it.scheduleBegin(),
                        it.scheduleBegin(),
                        it.latitude(),
                        it.longitude(),
                        it.userInvited()?.map {
                            User(
                                it.id() ?: -1,
                                it.userName(),
                                it.email() ?: ""
                            )
                        } ?: listOf()
                    )
                }
            }
            it.onFailure {
                errorMessage.postValue(it.message)
                return@map null
            }
            return@map null
        }
    }

    fun getTrackVoteEventPublicList(): LiveData<List<EventModel>> {
        return map(trackVoteEventRepo.getTrackVoteEventsPublic()) {
            it.onSuccess {
                return@map it.TrackVoteEventsPublic().map {
                    EventModel(
                        it.id(),
                        it.userMaster()?.id() ?: return@map null,
                        it.userMaster()?.userName() ?: return@map null,
                        it.name(),
                        it.public_(),
                        it.scheduleBegin(),
                        it.scheduleBegin(),
                        it.latitude(),
                        it.longitude()
                    )
                }.filterNotNull()
            }
            it.onFailure {
                errorMessage.postValue(it.message)
                return@map listOf<EventModel>()
            }
            return@map listOf<EventModel>()
        }
    }

    fun getTrackVoteEventPrivateList(): LiveData<List<EventModel>> {
        return map(
            Session.getId(getApplication())!!.let {
                trackVoteEventRepo.getTrackVoteEventByUserId(it)
            }
        ) {
            it.onSuccess {
                return@map it.TrackVoteEventByUserId().trackVoteEvents()?.map {
                    EventModel(
                        it.id(),
                        it.userMaster()?.id() ?: return@map null,
                        it.userMaster()?.userName() ?: return@map null,
                        it.name(),
                        it.public_(),
                        it.scheduleBegin(),
                        it.scheduleBegin(),
                        it.latitude(),
                        it.longitude()
                    )
                }?.filterNotNull()
            }
            it.onFailure {
                errorMessage.postValue(it.message)
                return@map listOf<EventModel>()
            }
            return@map listOf<EventModel>()
        }
    }

    fun getTrackVoteEventAddOrUpdateVote(
        eventId: Int,
        inputMusic: String,
        up: Boolean
    ): LiveData<Result<TrackVoteEventAddOrUpdateVoteMutation.Data>>? {

        val userId = Session.getId(getApplication())
        userId ?: return null
        val musicId = cacheAutoComplet[inputMusic]
        musicId ?: return null

        return trackVoteEventRepo.trackVoteEventAddOrUpdateVote(eventId, userId, musicId, up)
    }

    fun getTrackVoteEventAddOrUpdateVote(
        eventId: Int,
        trackId: Int,
        up: Boolean
    ): LiveData<Result<TrackVoteEventAddOrUpdateVoteMutation.Data>>? {
        val userId = Session.getId(getApplication())
        userId ?: return null
        return trackVoteEventRepo.trackVoteEventAddOrUpdateVote(eventId, userId, trackId, up)
    }

    fun delVote(
        eventId: Int,
        trackId: Int
    ): LiveData<Result<TrackVoteEventDelVoteMutation.Data>>? {
        val userId = Session.getId(getApplication())
        userId ?: return null
        return trackVoteEventRepo.trackVoteEventDelVote(eventId, userId, trackId)
    }

    fun updateTrackDictionary(str: String) {
        autoCompletion.addSource(deezerSearchRepo.search(str)) { r ->
            r.onFailure {
                errorMessage.postValue(it.message)
                autoCompletion.postValue(null)
            }
            r.onSuccess {
                autoCompletion.postValue(
                    it.DeezerSearch().search()?.mapNotNull {
                        TrackDictionaryModel("${it.title()} / ${it.artistName()}", it.id())
                    }?.map {
                        cacheAutoComplet[it.str] = it.id
                        it
                    }
                )
            }
        }
    }

    fun getAutoCompletion(): LiveData<List<TrackDictionaryModel>> {
        return autoCompletion
    }

    fun getErrorMsg(): LiveData<String> {
        return errorMessage
    }

    fun makePlayer(): TrackPlayer {
        return TrackPlayer(getApplication(), MainActivity.deezerConnect, WifiAndMobileNetworkStateChecker())
    }

    fun nextTrack(id: Int): LiveData<Result<TrackVoteEventNextTrackMutation.Data>> {
        return trackVoteEventRepo.nextTrack(id)
    }

}
