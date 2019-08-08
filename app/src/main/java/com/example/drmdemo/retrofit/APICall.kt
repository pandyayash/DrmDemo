package com.example.drmdemo.retrofit


import com.example.drmdemo.model.BasicModel
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET

interface APICall {


    @GET(API.VALIDATE_CONTENT)
    fun getData(): Observable<Response<BasicModel>>


}