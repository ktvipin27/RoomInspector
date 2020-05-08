package com.ktvipin27.roomexplorer.sample.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Vipin KT on 08/05/20
 */
@Entity
data class Person(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String
)