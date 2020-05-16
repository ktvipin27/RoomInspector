package com.ktvipin.roominspector.sample.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ktvipin.roominspector.sample.db.entity.User

/**
 * Created by Vipin KT on 08/05/20
 */
@Dao
interface UserDAO {

    @Insert
    fun insert(user: User)

    @Query("DELETE FROM User")
    fun clear()
}