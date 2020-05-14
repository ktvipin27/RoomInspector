package com.ktvipin27.roomexplorer

import android.R
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
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

fun Context.toast(messageId: Int) = toast(getString(messageId))

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER, 0, 0)
    }.show()
}

fun AppCompatActivity.showAlert(
    title: String,
    message: String,
    positiveButton: String,
    onSubmit: () -> Unit
) = showDialog(title, message, null, positiveButton, onSubmit)

fun AppCompatActivity.showAlert(
    title: String,
    view: View,
    positiveButton: String,
    onSubmit: () -> Unit
) = showDialog(title, null, view, positiveButton, onSubmit)

private fun AppCompatActivity.showDialog(
    title: String,
    message: String?,
    view: View?,
    positiveButton: String,
    onSubmit: () -> Unit
) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setView(view)
    builder.setPositiveButton(
        positiveButton
    ) { dialog, which ->
        onSubmit()
        dialog.dismiss()
    }
    builder.setNegativeButton(
        getString(R.string.cancel)
    ) { dialog, which -> dialog.dismiss() }
    if (!isFinishing) {
        builder.create().show()
    }
}

fun AppCompatActivity.hideKeyboard() {
    val view = this.currentFocus
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun <A, B> Pair<Iterable<A>, Iterable<B>>.forEach(f: (A, B) -> Unit) {
    val ia = first.iterator()
    val ib = second.iterator()

    while (ia.hasNext() && ib.hasNext()) {
        val va = ia.next()
        val vb = ib.next()

        f(va, vb)
    }
}

fun <A, B> Pair<Iterable<A>, Iterable<B>>.forEachIndexed(f: (Int, A, B) -> Unit) {
    val ia = first.iterator().withIndex()
    val ib = second.iterator().withIndex()

    while (ia.hasNext() && ib.hasNext()) {
        val next = ia.next()
        val index = next.index
        val va = next.value
        val vb = ib.next().value

        f(index, va, vb)
    }
}