package com.example.drmdemo.retrofit

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.Response

class APICallback<T>(
    private val mListener: OnResponseListener,
    private val requestCode: Int,
    val request: Observable<Response<T>>
) :
    DisposableObserver<Response<T>>() {


    protected var mDisposable: Disposable? = null
    val Message = "message"
    override fun onComplete() {

    }

    override fun onNext(response: Response<T>) {
        /*- OK(200, "OK"),
        - NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information")
        - NO_CONTENT(204, "No Content")
        - ALREADY_REPORTED(208, "Already Reported")
        - BAD_REQUEST(400, "Bad Request")
        - UNAUTHORIZED(401, "Unauthorized")
        - NOT_FOUNToast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();D(404, "Not Found")
        - INTERNAL_SERVER_ERROR(500, "Internal Server Error")*/

        var jobj: JSONObject? = null
        try {
            jobj = JSONObject(response.errorBody()?.string())
        } catch (e: Exception) {
            jobj = JSONObject("{}")
        }

        when (response.code()) {
            200 -> mListener.onResponseReceived(response.body(), requestCode)
            203 -> mListener.onResponseError(
                jobj?.getString(Message) ?: "Non-Authoritative Information",
                requestCode,
                response.code()
            )
            204 -> mListener.onResponseError("No Content",
                requestCode,
                response.code()
            )
            208 -> mListener.onResponseError(
                jobj?.getString(Message) ?: "Already Reported",
                requestCode,
                response.code()
            )
            302 -> mListener.onResponseError(
                jobj?.getString(Message) ?: "Check input",
                requestCode,
                response.code()
            )
            400 -> mListener.onResponseError(
                jobj?.getString(Message) ?: "Bad Request",
                requestCode,
                response.code()
            )
            401 -> {
                mListener.onResponseError("Unauthorised USER....", requestCode, 401)
            }
            404 -> mListener.onResponseError(
                jobj?.getString(Message) ?: "Not Found",
                requestCode,
                response.code()
            )
            500 -> mListener.onResponseError("Internal Server Error", requestCode, response.code())
            412 -> {

            }

            else -> mListener.onResponseError("Something went wrong, Please try later", requestCode, 0)
        }


    }

    override fun onError(e: Throwable) {
        if (e is java.net.ConnectException) {
            mListener.onResponseError(e.localizedMessage, requestCode, 0)
        }

    }


}
