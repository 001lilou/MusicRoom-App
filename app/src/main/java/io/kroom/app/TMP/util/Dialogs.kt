package io.kroom.app.TMP.util

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity


object Dialogs {

    fun errorDialog(ctx : Context, message: String, title: String = "An error occured!"): AlertDialog.Builder {
        return AlertDialog.Builder(ctx)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("ok", null)
    }

    fun successDialog(ctx : Context, message: String, title: String = "Success!"): AlertDialog.Builder {
        return AlertDialog.Builder(ctx)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("ok", null)
    }
}