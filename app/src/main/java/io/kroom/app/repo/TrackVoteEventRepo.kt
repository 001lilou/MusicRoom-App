package io.kroom.app.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.ApolloSubscriptionCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import io.kroom.app.graphql.*
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

class TrackVoteEventRepo(private val client: ApolloClient) {

    fun trackVoteEventAddOrUpdateVote(
        eventId: Int,
        userId: Int,
        musicId: Int,
        up: Boolean
    ): LiveData<Result<TrackVoteEventAddOrUpdateVoteMutation.Data>> {
        val data: MutableLiveData<Result<TrackVoteEventAddOrUpdateVoteMutation.Data>> =
            MutableLiveData()

        client.mutate(
            TrackVoteEventAddOrUpdateVoteMutation.builder()
                .eventId(eventId)
                .userId(userId)
                .musicId(musicId)
                .up(up)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun trackVoteEventDelVote(
        eventId: Int,
        userId: Int,
        musicId: Int
    ): LiveData<Result<TrackVoteEventDelVoteMutation.Data>> {
        val data: MutableLiveData<Result<TrackVoteEventDelVoteMutation.Data>> =
            MutableLiveData()

        client.mutate(
            TrackVoteEventDelVoteMutation.builder()
                .eventId(eventId)
                .userId(userId)
                .musicId(musicId)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun byId(
        id: Int
    ): LiveData<Result<TrackVoteEventByIdQuery.Data>> {
        val data: MutableLiveData<Result<TrackVoteEventByIdQuery.Data>> =
            MutableLiveData()
        val queryCall = TrackVoteEventByIdQuery
            .builder()
            .id(id)
            .build()
        client.query(queryCall).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    data class trackVoteEventSubRes(
        val lData: LiveData<Result<TrackVoteEventByIdSubscription.Data>>,
        val subCall: ApolloSubscriptionCall<TrackVoteEventByIdSubscription.Data>
    )

    fun subById(
        eventId: Int
    ): trackVoteEventSubRes {
        val data: MutableLiveData<Result<TrackVoteEventByIdSubscription.Data>> =
            MutableLiveData()

        val subCall: ApolloSubscriptionCall<TrackVoteEventByIdSubscription.Data> = client.subscribe(
            TrackVoteEventByIdSubscription.builder()
                .id(eventId)
                .build()
        )
        subCall.execute(object : ApolloSubscriptionCall.Callback<TrackVoteEventByIdSubscription.Data> {
            override fun onResponse(response: Response<TrackVoteEventByIdSubscription.Data>) {
                data.postValue(
                    if (response.errors().isEmpty()) {
                        val dataRep = response.data()
                        if (dataRep != null) success(dataRep)
                        else failure(Throwable("Empty Response"))
                    } else failure(Throwable(response.errors()[0].message()))
                )
            }

            override fun onConnected() {}
            override fun onTerminated() {}
            override fun onCompleted() {}

            override fun onFailure(e: ApolloException) {
                Log.e("ERROR", e.toString())
                data.postValue(failure(e))
            }

        })

        return trackVoteEventSubRes(data, subCall)
    }

    fun getTrackVoteEventByUserId(
        userId: Int
    ): LiveData<Result<TrackVoteEventByUserIdQuery.Data>> {
        val data: MutableLiveData<Result<TrackVoteEventByUserIdQuery.Data>> =
            MutableLiveData()
        val queryCall = TrackVoteEventByUserIdQuery
            .builder()
            .userId(userId)
            .build()
        client.query(queryCall).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun getTrackVoteEventsPublic(

    ): LiveData<Result<TrackVoteEventsPublicQuery.Data>> {
        val data: MutableLiveData<Result<TrackVoteEventsPublicQuery.Data>> =
            MutableLiveData()
        val queryCall = TrackVoteEventsPublicQuery
            .builder()
            .build()
        client.query(queryCall).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun setTrackVoteEventNew(
        userIdMaster: Int,
        name: String,
        open: Boolean,
        locAndSchRestriction: Boolean,
        scheduleBegin: Long,
        scheduleEnd: Long,
        latitude: Double,
        longitude: Double
    ): LiveData<Result<TrackVoteEventNewMutation.Data>> {
        val data: MutableLiveData<Result<TrackVoteEventNewMutation.Data>> =
            MutableLiveData()

        client.mutate(
            TrackVoteEventNewMutation.builder()
                .userIdMaster(userIdMaster)
                .name(name)
                .open(open)
                .locAndSchRestriction(locAndSchRestriction)
                .scheduleBegin(scheduleBegin.toString())
                .scheduleEnd(scheduleEnd.toString())
                .latitude(latitude)
                .longitude(longitude)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun del(
        eventId: Int
    ): LiveData<Result<TrackVoteEventDelMutation.Data>> {
        val data: MutableLiveData<Result<TrackVoteEventDelMutation.Data>> =
            MutableLiveData()

        client.mutate(
            TrackVoteEventDelMutation.builder()
                .id(eventId)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun update(
        eventId: Int,
        userIdMaster: Int,
        name: String,
        publc: Boolean,
        locAndSchRestriction: Boolean,
        scheduleBegin: String,
        scheduleEnd: String,
        latitude: Double,
        longitude: Double
    ): LiveData<Result<TrackVoteEventUpdateMutation.Data>> {
        val data: MutableLiveData<Result<TrackVoteEventUpdateMutation.Data>> =
            MutableLiveData()

        client.mutate(
            TrackVoteEventUpdateMutation.builder()
                .eventId(eventId)
                .userIdMaster(userIdMaster)
                .name(name)
                .publc(publc)
                .locAndSchRestriction(locAndSchRestriction)
                .scheduleBegin(scheduleBegin)
                .scheduleEnd(scheduleEnd)
                .latitude(latitude)
                .longitude(longitude)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun addUser(
        eventId: Int,
        userId: Int
    ): LiveData<Result<TrackVoteEventAddUserMutation.Data>> {
        val data: MutableLiveData<Result<TrackVoteEventAddUserMutation.Data>> =
            MutableLiveData()

        client.mutate(
            TrackVoteEventAddUserMutation.builder()
                .eventId(eventId)
                .userId(userId)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun delUser(
        eventId: Int,
        userId: Int
    ): LiveData<Result<TrackVoteEventDelUserMutation.Data>> {
        val data: MutableLiveData<Result<TrackVoteEventDelUserMutation.Data>> =
            MutableLiveData()

        client.mutate(
            TrackVoteEventDelUserMutation.builder()
                .eventId(eventId)
                .userId(userId)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun nextTrack(id: Int): LiveData<Result<TrackVoteEventNextTrackMutation.Data>> {
        val data: MutableLiveData<Result<TrackVoteEventNextTrackMutation.Data>> =
            MutableLiveData()

        client.mutate(
            TrackVoteEventNextTrackMutation.builder()
                .id(id)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

}
