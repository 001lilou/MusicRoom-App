package io.kroom.app.util

import android.app.Application
import android.content.Context
import android.util.Log

object Session {

    fun isConnected(app: Application): Boolean {
        val sharedPreferences = app.getSharedPreferences("byId", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", null) != null
    }

    fun setUser(app: Application, id: Int, email: String, username: String, token: String) {
        val sharedPref = app.getSharedPreferences("byId", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("token", token)
            putString("email", email)
            putInt("id", id)
            putString("username", username)
            apply()
        }
    }

    fun removeUser(app: Application) {
        val sharedPref = app.getSharedPreferences("byId", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("token", null)
            putString("email", null)
            putString("id", null)
            putString("username", null)
            apply()
        }
    }

    fun getUsername(app: Application): String? {
        val sharedPreferences = app.getSharedPreferences("byId", Context.MODE_PRIVATE)
        return sharedPreferences.getString("username", null)
    }

    fun getEmail(app: Application): String? {
        val sharedPreferences = app.getSharedPreferences("byId", Context.MODE_PRIVATE)
        return sharedPreferences.getString("email", null)
    }

    fun getId(app: Application): Int? {
        val sharedPreferences = app.getSharedPreferences("byId", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("id", 0)
    }

    fun getToken(app: Application): String? {
        val sharedPreferences = app.getSharedPreferences("byId", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", null)
    }
}