/*
 * Copyright 2021 Vipin KT
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