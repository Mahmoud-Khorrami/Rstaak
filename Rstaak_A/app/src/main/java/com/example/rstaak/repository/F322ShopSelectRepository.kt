package com.example.rstaak.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.rstaak.req_res.f322_shop_select.F322Request
import com.example.rstaak.req_res.f322_shop_select.F322Response
import com.example.rstaak.retrofit.ApiInterface
import io.reactivex.Observable
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class F322ShopSelectRepository @Inject constructor(
    var apiInterface: ApiInterface,
    var executor: ExecutorService,
    var f322RequestFactory: F322Request.Factory,
    var sharedPreferences: SharedPreferences)
{
    fun getShop(): Future<Observable<F322Response>>
    {
        val request = f322RequestFactory.create(sharedPreferences.getString("userId", null).toString())
        val callable = Callable { apiInterface.getShop(request) }

        return object : Future<Observable<F322Response>>
        {

            override fun cancel(mayInterruptIfRunning: Boolean): Boolean
            {
                Log.i(TAG, "cancel: ")

                if(mayInterruptIfRunning) executor.shutdown()

                return false
            }

            override fun isCancelled(): Boolean
            {
                Log.i(TAG, "isCancelled: ")
                return executor.isShutdown
            }

            override fun isDone(): Boolean
            {
                Log.i(TAG, "isDone: ")
                return executor.isTerminated
            }

            override fun get(): Observable<F322Response>
            {
                return executor.submit(callable).get()
            }

            override fun get(timeout: Long, unit: TimeUnit?): Observable<F322Response>
            {
                return executor.submit(callable).get(timeout, unit)
            }

        }
    }
    companion object
    {
        private const val TAG = "SelectShopRepository"
    }
}