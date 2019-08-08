package com.example.drmdemo.helper

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.util.Base64
import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.regex.Pattern


object Utils {

    fun printHashKey(pContext: Context) {
        try {
            val info = pContext.packageManager.getPackageInfo(pContext.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("", "printHashKey()", e)
        }

    }

    fun isValidPassword(password: String): Boolean {
        return Pattern.compile("(?=.*[0-9])(?=.*[a-z])(?=.*[@#$%^&+=]).{8,16}")
            .matcher(password)
            .matches()
    }

    fun isConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivityManager.activeNetworkInfo
        return info != null && info.isConnected
    }


}