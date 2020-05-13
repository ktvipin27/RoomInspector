package com.ktvipin27.roomexplorer

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by Vipin KT on 12/05/20
 */
fun AppCompatActivity.refreshActivity() {
    finish()
    startActivity(intent)
}

fun Context.toast(messageId: Int) {
    Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show()
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.showAlert(
    title: String,
    message: String,
    positiveButton: String,
    onSubmit: () -> Unit
) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton(
        positiveButton
    ) { dialog, which ->
        onSubmit()
        dialog.dismiss()
    }
    builder.setNegativeButton(
        getString(android.R.string.cancel)
    ) { dialog, which -> dialog.dismiss() }
    if (!isFinishing) {
        builder.create().show()
    }
}