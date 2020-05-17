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

package com.ktvipin.roominspector.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ktvipin.roominspector.RoomInspector
import com.ktvipin.roominspector.sample.db.AppDatabase
import com.ktvipin.roominspector.sample.db.entity.User
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

/**
 * Created by Vipin KT on 08/05/20
 */
class MainActivity : AppCompatActivity() {

    private val db: AppDatabase by lazy { AppDatabase.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val genders = arrayOf("Male", "Female")
        val phoneNumberRange = 9900000000..9999999999
        val ageRange = 1..100
        button1.setOnClickListener {
            val name = "User${Random().nextInt(100)}"
            db.personDAO().insert(
                User(
                    0,
                    name,
                    genders.random(),
                    ageRange.random(),
                    phoneNumberRange.random().toString(),
                    "$name@gmail.com"
                )
            )
        }

        button2.setOnClickListener {
            db.personDAO().clear()
        }

        button3.setOnClickListener {
            RoomInspector.inspect(this, AppDatabase::class.java, AppDatabase.DB_NAME)
        }
    }
}
