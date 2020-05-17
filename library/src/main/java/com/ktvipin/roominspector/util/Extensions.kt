/*
 * Copyright 2020 Vipin KT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ktvipin.roominspector.util

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/**
 * Contains custom extension functions.
 *
 * Created by Vipin KT on 12/05/20
 */

/**
 * Restarts an activity with the same intent by finishing the current.
 *
 * @receiver [AppCompatActivity]
 */
fun AppCompatActivity.refreshActivity() {
    finish()
    startActivity(intent)
}

/**
 * Shows a toast message.
 *
 * @param messageId string ref id of the message to be displayed
 * @receiver [Context]
 */
fun Context.toast(messageId: Int) = toast(getString(messageId))

/**
 * Shows a toast message.
 *
 * @param message message to be displayed
 * @receiver [Context]
 */
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER, 0, 0)
    }.show()
}

/**
 * Shows an alert dialog with given values.
 *
 * @param title title of the dialog
 * @param message message to be displayed
 * @param positiveButton positive action text
 * @param onSubmit function to be called on clicking positive button
 * @receiver [AppCompatActivity]
 */
fun AppCompatActivity.showAlert(
    title: String,
    message: String,
    positiveButton: String,
    onSubmit: () -> Unit
) = showDialog(title, message, null, positiveButton, onSubmit)

/**
 * Shows an alert dialog with given values.
 *
 * @param title title of the dialog
 * @param view custom view to be displayed
 * @param positiveButton positive action text
 * @param onSubmit function to be called on clicking positive button
 * @receiver [AppCompatActivity]
 */
fun AppCompatActivity.showAlert(
    title: String,
    view: View,
    positiveButton: String,
    onSubmit: () -> Unit
) = showDialog(title, null, view, positiveButton, onSubmit)

/**
 * Shows an alert dialog with given values.
 *
 * @param title title of the dialog
 * @param message @Nullable message to be displayed
 * @param view @Nullable custom view to be displayed
 * @param positiveButton positive action text
 * @param onSubmit function to be called on clicking positive button
 * @receiver [AppCompatActivity]
 */
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
        getString(android.R.string.cancel)
    ) { dialog, which -> dialog.dismiss() }
    if (!isFinishing) {
        builder.create().show()
    }
}

/**
 * Hides the keyboard.
 *
 * @receiver [AppCompatActivity]
 */
fun AppCompatActivity.hideKeyboard() {
    val view = this.currentFocus
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

/**
 * Loops multiple list at same time and performs the given [action] on each element of this [Iterator]s.
 * @param [action] function that takes elements of lists itself and performs the desired action on the element.
 */
fun <A, B> Pair<Iterable<A>, Iterable<B>>.forEach(action: (A, B) -> Unit) {
    val ia = first.iterator()
    val ib = second.iterator()

    while (ia.hasNext() && ib.hasNext()) {
        val va = ia.next()
        val vb = ib.next()

        action(va, vb)
    }
}

/**
 * Loops multiple list at same time and performs the given [action] on each element,
 * providing sequential index with the elements.
 * @param [action] function that takes the index of an element and the elements of the lists itself
 * and performs the desired action on the element.
 */
fun <A, B> Pair<Iterable<A>, Iterable<B>>.forEachIndexed(action: (Int, A, B) -> Unit) {
    val ia = first.iterator().withIndex()
    val ib = second.iterator().withIndex()

    while (ia.hasNext() && ib.hasNext()) {
        val next = ia.next()
        val index = next.index
        val va = next.value
        val vb = ib.next().value

        action(index, va, vb)
    }
}