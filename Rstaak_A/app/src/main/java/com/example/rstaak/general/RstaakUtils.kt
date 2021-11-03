package com.example.rstaak.general

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.inputmethod.InputMethodManager
import javax.inject.Inject

class RstaakUtils @Inject constructor()
{
    @Inject
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    fun hideKeyboard(activity: Activity)
    {
        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val currentFocusedView = activity.currentFocus
        currentFocusedView?.let {
            inputMethodManager.hideSoftInputFromWindow(currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    fun saveUpdatedAt(type: String, newUpdatedAt: String)
    {
        var oldUpdatedAt = getUpdatedAt(type)
        if(oldUpdatedAt == null)
            oldUpdatedAt = "0"

        if(newUpdatedAt.toDouble() > oldUpdatedAt.toDouble())
            sharedPreferencesEditor.putString(type, newUpdatedAt).commit()
    }

    fun getUpdatedAt(type: String): String?
    {
        return sharedPreferences.getString(type, null)
    }
}