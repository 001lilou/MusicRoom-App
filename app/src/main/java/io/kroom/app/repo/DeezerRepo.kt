package io.kroom.app.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloClient
import io.kroom.app.graphql.DeezerSearchQuery

class DeezerRepo(private val client: ApolloClient) {

    fun search(
        str: String
    ): LiveData<Result<DeezerSearchQuery.Data>> {
        val data: MutableLiveData<Result<DeezerSearchQuery.Data>> =
            MutableLiveData()

        client.query(
            DeezerSearchQuery.builder()
                .search(str)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

}
