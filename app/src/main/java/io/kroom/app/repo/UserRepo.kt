package io.kroom.app.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloClient
import io.kroom.app.graphql.*
import io.kroom.app.graphql.type.PrivacyEnum


class UserRepo(private val client: ApolloClient) {

    fun byId(
        id: Int
    ): LiveData<Result<UserByIdQuery.Data>> {
        val data: MutableLiveData<Result<UserByIdQuery.Data>> =
            MutableLiveData()

        client.query(
            UserByIdQuery.builder()
                .id(id)
                .build()
        ).enqueue(CallBackHandler {
            data.postValue(it)
        })

        return data
    }

    fun signUp(
        userName: String,
        email: String,
        pass: String
    ): LiveData<Result<UserSignUpMutation.Data>> {
        val data: MutableLiveData<Result<UserSignUpMutation.Data>> =
            MutableLiveData()

        client.mutate(
            UserSignUpMutation.builder()
                .userName(userName)
                .email(email)
                .pass(pass)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun signIn(
        userName: String,
        pass: String
    ): LiveData<Result<UserSignInMutation.Data>> {
        val data: MutableLiveData<Result<UserSignInMutation.Data>> =
            MutableLiveData()

        client.mutate(
            UserSignInMutation.builder()
                .userName(userName)
                .pass(pass)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun signGoogleRequest(
        token: String
    ): LiveData<Result<UserSignWhithGoolgeMutation.Data>> {
        val data: MutableLiveData<Result<UserSignWhithGoolgeMutation.Data>> =
            MutableLiveData()

        client.mutate(
            UserSignWhithGoolgeMutation.builder()
                .token(token)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun addFriend(
        userId: Int,
        friendId: Int
    ): LiveData<Result<UserAddFriendMutation.Data>> {
        val data: MutableLiveData<Result<UserAddFriendMutation.Data>> =
            MutableLiveData()

        client.mutate(
            UserAddFriendMutation.builder()
                .userId(userId)
                .friendId(friendId)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun deleteFriend(
        userId: Int,
        friendId: Int
    ): LiveData<Result<UserDeleteFriendMutation.Data>> {
        val data: MutableLiveData<Result<UserDeleteFriendMutation.Data>> =
            MutableLiveData()

        client.mutate(
            UserDeleteFriendMutation.builder()
                .userId(userId)
                .friendId(friendId)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun addGenre(
        userId: Int,
        genreId: Int
    ): LiveData<Result<UserAddMusicalPreferenceMutation.Data>> {
        val data: MutableLiveData<Result<UserAddMusicalPreferenceMutation.Data>> =
            MutableLiveData()

        client.mutate(
            UserAddMusicalPreferenceMutation.builder()
                .userId(userId)
                .genreId(genreId)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun deleteGenre(
        userId: Int,
        genreId: Int
    ): LiveData<Result<UserDelMusicalPreferenceMutation.Data>> {
        val data: MutableLiveData<Result<UserDelMusicalPreferenceMutation.Data>> =
            MutableLiveData()

        client.mutate(
            UserDelMusicalPreferenceMutation.builder()
                .userId(userId)
                .genreId(genreId)
                .build()
        ).enqueue(CallBackHandler { data.postValue(it) })

        return data
    }

    fun byIdGenres(
        id: Int
    ): LiveData<Result<UserGetMusicalPreferencesQuery.Data>> {
        val data: MutableLiveData<Result<UserGetMusicalPreferencesQuery.Data>> =
            MutableLiveData()

        client.query(
            UserGetMusicalPreferencesQuery.builder()
                .id(id)
                .build()
        ).enqueue(CallBackHandler {
            data.postValue(it)
        })

        return data
    }

    fun userNameAutocompletion(
        prefix: String
    ): LiveData<Result<UserNameAutocompletionQuery.Data>> {
        val data: MutableLiveData<Result<UserNameAutocompletionQuery.Data>> =
            MutableLiveData()

        client.query(
            UserNameAutocompletionQuery.builder()
                .prefix(prefix)
                .build()
        ).enqueue(CallBackHandler {
            data.postValue(it)
        })

        return data
    }

    fun updatePrivacy(
        userId: Int,
        email: PrivacyEnum,
        friends: PrivacyEnum,
        location: PrivacyEnum,
        musicalPreferencesGenre: PrivacyEnum
    ): LiveData<Result<UserUpdatePrivacyMutation.Data>> {
        val data: MutableLiveData<Result<UserUpdatePrivacyMutation.Data>> =
            MutableLiveData()

        client.mutate(
            UserUpdatePrivacyMutation.builder()
                .userId(userId)
                .email(email)
                .friends(friends)
                .location(location)
                .musicalPreferencesGenre(musicalPreferencesGenre)
                .build()
        ).enqueue(CallBackHandler {
            data.postValue(it)
        })

        return data
    }

    fun updatePassword(
        email: String,
        pass: String
    ): LiveData<Result<UserUpdatePasswordMutation.Data>> {
        val data: MutableLiveData<Result<UserUpdatePasswordMutation.Data>> =
            MutableLiveData()

        client.mutate(
            UserUpdatePasswordMutation.builder()
                .email(email)
                .newPassword(pass)
                .build()
        ).enqueue(CallBackHandler {
            data.postValue(it)
        })

        return data
    }

}