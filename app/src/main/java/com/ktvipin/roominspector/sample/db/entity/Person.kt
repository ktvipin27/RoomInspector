package com.ktvipin.roominspector.sample.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Vipin KT on 08/05/20
 */
@Entity
data class Person(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "first_name")
    val firstName: String,
    @ColumnInfo(name = "last_name")
    val lastName: String,
    val age: Int,
    val address: String
)