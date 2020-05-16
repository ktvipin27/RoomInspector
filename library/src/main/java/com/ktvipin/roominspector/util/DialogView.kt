package com.ktvipin.roominspector.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.ktvipin.roominspector.R

/**
 * A custom view which contains multiple pairs if [TextView] and [EditText]
 *
 * Created by Vipin KT on 15/05/20
 *
 * @param context Context
 */
@SuppressLint("ViewConstructor")
class DialogView(
    context: Context
) : ScrollView(context) {
    private val editTextTopMargin: Int by lazy {
        resources.getDimension(R.dimen.ri_margin_top_dialog_add_row_item).toInt()
    }
    private val etPadding: Int by lazy {
        resources.getDimension(R.dimen.ri_padding_dialog_add_row_item).toInt()
    }
    private val dialogPadding: Int by lazy {
        resources.getDimension(R.dimen.ri_padding_vertical_dialog_add_row).toInt()
    }
    private val etBackground by lazy {
        ContextCompat.getDrawable(context, R.drawable.ri_bg_rounded_corner)
    }

    /**
     * Holds all [EditText]s in this view.
     */
    private val etList = mutableListOf<EditText>()

    /**
     * The main layout of this view, where all pairs of views will be added.
     */
    private val mainLayout: LinearLayout by lazy {
        LinearLayout(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            setPadding(dialogPadding, 0, dialogPadding, 0)
        }
    }

    /**
     * Returns a list, contains values of all [EditText]s in [etList].
     */
    val fieldValues
        get() = etList.map { it.text.toString() }

    /**
     * Creates the view by looping the given columnNames and values.
     *
     * @param columnNames list of strings which will be displayed in TextView's
     * @param values list of strings which will be displayed in EditText's
     */
    fun create(columnNames: List<String>, values: List<String>): DialogView {
        Pair(columnNames, values).forEach { columnName, value ->
            val tv = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 1f
                    setMargins(0, editTextTopMargin, 0, 0)
                }
                text = columnName
                setTextColor(Color.BLACK)
            }
            val et = EditText(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 1f
                    setMargins(0, editTextTopMargin, 0, 0)
                }
                setPadding(
                    paddingStart + etPadding,
                    paddingTop,
                    paddingEnd + etPadding,
                    paddingBottom
                )
                setTextColor(Color.BLACK)
                background = etBackground
                setText(value)
            }
            etList.add(et)
            val row = LinearLayout(context).apply {
                layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
                addView(tv)
                addView(et)
            }
            mainLayout.addView(row)
        }
        addView(mainLayout)
        return this
    }
}