package com.trainee.appinventiv.notesapp.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.trainee.appinventiv.notesapp.utils.Constants.PREFS_TOKEN_FILE
import com.trainee.appinventiv.notesapp.utils.Constants.USER_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(@ApplicationContext context: Context){
    private var prefs: SharedPreferences = context.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)


    fun saveToken(token: String) {
        Log.e("prefs" , prefs.toString() )
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun getToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }
}