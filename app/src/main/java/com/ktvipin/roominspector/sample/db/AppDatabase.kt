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

package com.ktvipin.roominspector.sample.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ktvipin.roominspector.sample.db.dao.UserDAO
import com.ktvipin.roominspector.sample.db.entity.User

/**
 * Created by Vipin KT on 08/05/20
 */
@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun personDAO(): UserDAO

    companion object {
        const val DB_NAME = "app-db"
        private val lock = Any()
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(lock) {
                if (INSTANCE == null)
                    INSTANCE = createDB(context)
                return INSTANCE as AppDatabase
            }
        }

        private fun createDB(context: Context): AppDatabase {
            val database: Builder<AppDatabase> =
                Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
            return database
                .allowMainThreadQueries()
                .build()
        }
    }
}