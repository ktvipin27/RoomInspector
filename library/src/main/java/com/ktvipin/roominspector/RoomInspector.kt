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

package com.ktvipin.roominspector

import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ktvipin.roominspector.view.RIMainActivity

/**
 * An in-app database inspector for your [RoomDatabase].
 *
 * Created by Vipin KT on 08/05/20
 */
object RoomInspector {

    /**
     * Key for dbClass.
     */
    internal const val KEY_DATABASE_CLASS = "com.ktvipin.roominspector.DATABASE_CLASS"

    /**
     * Key for dbName.
     */
    internal const val KEY_DATABASE_NAME = "com.ktvipin.roominspector.DATABASE_NAME"

    /**
     * Launches [RIMainActivity] from the [context].
     *
     * @param context The context such as any [android.app.Activity] or [android.app.Fragment].
     * @param dbClass A subclass of [RoomDatabase] registered in [Room] with @Database annotation
     * @param dbName The name of [dbClass]
     */
    @JvmStatic
    fun inspect(
        context: Context,
        dbClass: Class<out RoomDatabase>,
        dbName: String
    ) {
        Intent(context, RIMainActivity::class.java)
            .apply {
                putExtras(
                    bundleOf(KEY_DATABASE_CLASS to dbClass, KEY_DATABASE_NAME to dbName)
                )
            }
            .also { context.startActivity(it) }
    }
}
