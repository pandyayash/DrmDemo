package com.example.drmdemo.helper

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.reactivex.disposables.Disposable

open class BaseViewModel(application: Application) : AndroidViewModel(application) {

    protected var mDisposable: Disposable? = null


    fun onUnauthorized() {

    }

    override fun onCleared() {
        super.onCleared()
        mDisposable?.dispose()
    }

}