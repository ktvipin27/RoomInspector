package com.ktvipin.roominspector

import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import androidx.room.RoomDatabase
import com.ktvipin.roominspector.activity.RIMainActivity
import com.ktvipin.roominspector.activity.RIQueryActivity

/**
 * Created by Vipin KT on 08/05/20
 */
object RoomInspector {

    internal const val KEY_DATABASE_CLASS = "com.ktvipin.roominspector.DATABASE_CLASS"
    internal const val KEY_DATABASE_NAME = "com.ktvipin.roominspector.DATABASE_NAME"

    /**
     * Launches [RIMainActivity] from the context passed in the method.
     * @param context The context such as any activity or fragment or context reference
     * @param dbClass The database class registered in Room with @Database annotation and extended with RoomDatabase
     * @param dbName The name of your Room Database
     */
    fun explore(
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

    /**
     * internal purpose
     * Launches [RIQueryActivity] from the context passed in the method.
     * @param context RIMainActivity context
     */
    internal fun query(
        context: RIMainActivity
    ) = Intent(context, RIQueryActivity::class.java)
        .also { context.startActivity(it) }
}