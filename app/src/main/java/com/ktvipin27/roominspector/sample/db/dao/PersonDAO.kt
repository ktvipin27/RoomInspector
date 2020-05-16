package com.ktvipin27.roominspector.sample.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ktvipin27.roominspector.sample.db.entity.Person

/**
 * Created by Vipin KT on 08/05/20
 */
@Dao
interface PersonDAO {

    @Insert
    fun insert(person: Person)

    @Query("DELETE FROM Person")
    fun clear()
}