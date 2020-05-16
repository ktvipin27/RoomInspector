package com.ktvipin.roominspector.sample.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Vipin KT on 08/05/20
 */
@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val gender: String,
    val age: Int,
    val phoneNumber: String,
    val email: String
)