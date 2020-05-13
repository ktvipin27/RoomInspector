package com.ktvipin27.roomexplorer

/**
 * Created by Vipin KT on 12/05/20
 */

object Queries {
    const val GET_TABLE_NAMES = "SELECT name _id FROM sqlite_master WHERE type ='table'"
    const val GET_TABLE_DATA = "SELECT * FROM "
    const val GET_COUNT = "SELECT COUNT(*) FROM "
    const val DROP_TABLE = "DROP TABLE "
    const val DELETE_TABLE = "DELETE FROM "
}