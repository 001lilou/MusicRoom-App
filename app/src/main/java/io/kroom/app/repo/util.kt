package io.kroom.app.repo

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

class CallBackHandler<T>(val res: (Result<T>) -> Unit) : ApolloCall.Callback<T>() {
    override fun onResponse(response: Response<T>) {
        res(
            if (response.errors().isEmpty()) {
                val data = response.data()
                if (data != null) success(data)
                else failure(Throwable("Empty Response"))
            } else failure(Throwable(response.errors()[0].message()))
        )
    }

    override fun onFailure(e: ApolloException) {
        res(failure(e))
    }
}
