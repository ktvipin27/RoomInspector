package com.ktvipin.roominspector

import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ktvipin.roominspector.activity.RIMainActivity
import com.ktvipin.roominspector.activity.RIQueryActivity

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

    /**
     * Launches [RIQueryActivity] from [RIMainActivity].
     * (for internal purpose)
     *
     * @param context RIMainActivity context
     */
    internal fun query(
        context: RIMainActivity
    ) {
        Intent(context, RIQueryActivity::class.java)
            .also { context.startActivity(it) }
    }
}