package com.ktvipin27.roomexplorer

import android.database.Cursor

/**
 * Created by Vipin KT on 12/05/20
 */

sealed class QueryResult {
    data class Success(val cursor: Cursor) : QueryResult()
    data class Error(val exception: Exception) : QueryResult()
}