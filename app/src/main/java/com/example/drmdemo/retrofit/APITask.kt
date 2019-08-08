package com.example.drmdemo.retrofit


import com.example.drmdemo.model.BasicModel
import io.reactivex.disposables.Disposable

class APITask : BaseAPITask() {

    private val apiCall: APICall = Retrofit.getRetrofit().create(APICall::class.java)

    companion object Singleton {
        fun getInstance(): APITask {
            return APITask()
        }
    }


    fun isContentValid(listener: OnResponseListener): Disposable? {
        return getRequest(apiCall.getData(), listener, 1)
    }

}