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

class PlaylistEditorRepo(private val client: ApolloClient) {

    fun public(
    ): LiveData<Result<PlayListEditorsPublicQuery.Data>> {
        val data: MutableLiveData<Result<PlayListEditorsPublicQuery.Data>> =
            MutableLiveData()

        client.query(
            PlayListEditorsPublicQuery.builder()
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun byUserId(
        userId: Int
    ): LiveData<Result<PlayListEditorByUserIdQuery.Data>> {
        val data: MutableLiveData<Result<PlayListEditorByUserIdQuery.Data>> =
            MutableLiveData()

        client.query(
            PlayListEditorByUserIdQuery.builder()
                .id(userId)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun new(
        userMasterId: Int,
        name: String,
        public: Boolean
    ): LiveData<Result<PlayListEditorNewMutation.Data>> {
        val data: MutableLiveData<Result<PlayListEditorNewMutation.Data>> =
            MutableLiveData()

        client.mutate(
            PlayListEditorNewMutation.builder()
                .userMasterId(userMasterId)
                .name(name)
                .publc(public)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun del(
        id: Int
    ): LiveData<Result<PlayListEditorDelMutation.Data>> {
        val data: MutableLiveData<Result<PlayListEditorDelMutation.Data>> =
            MutableLiveData()

        client.mutate(
            PlayListEditorDelMutation.builder()
                .id(id)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun byId(
        id: Int
    ): LiveData<Result<PlayListEditorByIdQuery.Data>> {
        val data: MutableLiveData<Result<PlayListEditorByIdQuery.Data>> =
            MutableLiveData()

        client.query(
            PlayListEditorByIdQuery.builder()
                .id(id)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun moveTrack(
        playlistId: Int,
        trackId: Int,
        up: Boolean
    ): LiveData<Result<PlayListEditorMoveTrackMutation.Data>> {
        val data: MutableLiveData<Result<PlayListEditorMoveTrackMutation.Data>> =
            MutableLiveData()

        client.mutate(
            PlayListEditorMoveTrackMutation.builder()
                .playListId(playlistId)
                .trackId(trackId)
                .up(up)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun delTrack(
        playlistId: Int,
        trackId: Int
    ): LiveData<Result<PlayListEditorDelTrackMutation.Data>> {
        val data: MutableLiveData<Result<PlayListEditorDelTrackMutation.Data>> =
            MutableLiveData()

        client.mutate(
            PlayListEditorDelTrackMutation.builder()
                .playListId(playlistId)
                .trackId(trackId)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun addTrack(
        playlistId: Int,
        trackId: Int
    ): LiveData<Result<PlayListEditorAddTrackMutation.Data>> {
        val data: MutableLiveData<Result<PlayListEditorAddTrackMutation.Data>> =
            MutableLiveData()

        client.mutate(
            PlayListEditorAddTrackMutation.builder()
                .playListId(playlistId)
                .trackId(trackId)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun addUser(
        playlistId: Int,
        userId: Int
    ): LiveData<Result<PlayListEditorAddUserMutation.Data>> {
        val data: MutableLiveData<Result<PlayListEditorAddUserMutation.Data>> =
            MutableLiveData()

        client.mutate(
            PlayListEditorAddUserMutation.builder()
                .playlistId(playlistId)
                .userId(userId)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun delUser(
        playlistId: Int,
        userId: Int
    ): LiveData<Result<PlayListEditorDelUserMutation.Data>> {
        val data: MutableLiveData<Result<PlayListEditorDelUserMutation.Data>> =
            MutableLiveData()

        client.mutate(
            PlayListEditorDelUserMutation.builder()
                .playlistId(playlistId)
                .userId(userId)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    data class playListEditorSubRes(
        val lData: LiveData<Result<PlayListEditorByIdSubscription.Data>>,
        val subCall: ApolloSubscriptionCall<PlayListEditorByIdSubscription.Data>
    )

    fun subById(
        playlistId: Int
    ): playListEditorSubRes {
        val data: MutableLiveData<Result<PlayListEditorByIdSubscription.Data>> =
            MutableLiveData()

        val subCall: ApolloSubscriptionCall<PlayListEditorByIdSubscription.Data> = client.subscribe(
            PlayListEditorByIdSubscription.builder()
                .id(playlistId)
                .build()
        )
        subCall.execute(object : ApolloSubscriptionCall.Callback<PlayListEditorByIdSubscription.Data> {
            override fun onResponse(response: Response<PlayListEditorByIdSubscription.Data>) {
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

        return playListEditorSubRes(data, subCall)
    }

}
