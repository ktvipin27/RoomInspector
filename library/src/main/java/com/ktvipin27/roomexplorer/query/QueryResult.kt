package com.ktvipin27.roomexplorer.query

/**
 * Created by Vipin KT on 12/05/20
 */
sealed class QueryResult<out T> {
    data class Success<T>(val data: T) : QueryResult<T>()
    data class Error(val exception: Exception) : QueryResult<Nothing>()
}

typealias RowsAndColumns = Pair<List<String>, List<List<String>>>