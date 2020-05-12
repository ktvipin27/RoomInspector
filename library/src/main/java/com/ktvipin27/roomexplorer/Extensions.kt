package com.ktvipin27.roomexplorer

import android.content.Context
import android.widget.Toast

/**
 * Created by Vipin KT on 12/05/20
 */

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}