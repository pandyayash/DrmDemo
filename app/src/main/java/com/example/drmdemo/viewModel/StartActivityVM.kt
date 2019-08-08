package com.example.drmdemo.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.drmdemo.helper.BaseViewModel
import com.example.drmdemo.helper.Event
import com.example.drmdemo.helper.SharedPrefs
import com.example.drmdemo.model.BasicModel
import com.example.drmdemo.retrofit.APITask
import com.example.drmdemo.retrofit.OnResponseListener


class StartActivityVM(val context: Application) : BaseViewModel(context), OnResponseListener {

    private var content = MutableLiveData<BasicModel>()
    private val eventLiveDataStatus: MutableLiveData<Event<String>> = MutableLiveData()
    fun ObserveForChange(): LiveData<Event<String>> = eventLiveDataStatus

    fun ObserveServiceResponse(): LiveData<BasicModel> = content

    fun notifyView(message: String) {
        this.eventLiveDataStatus.value = Event(message)
    }


    fun callApi() {
        mDisposable = APITask.getInstance().isContentValid(this)
    }

    override fun <T> onResponseReceived(response: T, requestCode: Int) {
        content.value = response as BasicModel
        SharedPrefs.storeResponse(context, response)
        notifyView("1")

    }

    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {

        if (responseCode == 0) {
            notifyView("Internet is not available.")
        } else {
            notifyView("Error in response.")
        }
    }


    override fun onCleared() {
        super.onCleared()
    }
}

//http://l.deployninja.com/765270