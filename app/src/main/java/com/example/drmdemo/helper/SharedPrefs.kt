package com.example.drmdemo.helper


import android.content.Context
import android.content.SharedPreferences
import com.example.drmdemo.model.BasicModel
import com.google.gson.Gson

object SharedPrefs {

    private const val LOGIN_DATA = "loginDetail"
    private const val SHARED_PREF = "sharedPreference"


    private fun getSharedPreference(context: Context): SharedPreferences? {
        var sp: SharedPreferences? = null
        try {
            sp = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        } catch (ignored: Exception) {

        }
        return sp
    }


    fun getStoredResponse(context: Context): BasicModel? {
        val gson = Gson()
        val json = getSharedPreference(context)?.getString(LOGIN_DATA, "")
        return gson.fromJson<BasicModel>(json, BasicModel::class.java)
    }

    fun storeResponse(
        context: Context,
        userObject: BasicModel
    ) {

        val prefsEditor = getSharedPreference(context)?.edit()
        val gson = Gson()
        val json = gson.toJson(userObject)
        prefsEditor?.putString(LOGIN_DATA, json)
        prefsEditor?.apply()
    }

    fun removeAll(context: Context) {
        getSharedPreference(context)?.edit()?.clear()?.apply()
    }
}
