package com.ktvipin27.roominspector.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ktvipin27.roominspector.RoomInspector
import com.ktvipin27.roominspector.sample.db.AppDatabase
import com.ktvipin27.roominspector.sample.db.entity.Person
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

        button1.setOnClickListener {
            db.personDAO().insert(
                Person(
                    0,
                    "Person ${Random().nextInt()}",
                    "Person ${Random().nextInt()}",
                    Random().nextInt(100),
                    "Address ${Random().nextInt()}"
                )
            )
        }

        button2.setOnClickListener {
            db.personDAO().clear()
        }

        button3.setOnClickListener {
            RoomInspector.explore(this, AppDatabase::class.java, AppDatabase.DB_NAME)
        }
    }
}
