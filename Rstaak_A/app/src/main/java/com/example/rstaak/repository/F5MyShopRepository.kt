package com.example.rstaak.repository

import android.content.SharedPreferences
import com.example.rstaak.req_res.f4_shop.F4LDVRequest
import com.example.rstaak.req_res.f4_shop.F4LDVResponse
import com.example.rstaak.req_res.f4_shop.F4Request
import com.example.rstaak.req_res.f4_shop.F4Response
import com.example.rstaak.req_res.f52_edit_shop.F52Request
import com.example.rstaak.req_res.f5_my_shop.F5Request
import com.example.rstaak.retrofit.ApiInterface
import io.reactivex.Observable
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class F5MyShopRepository @Inject constructor(
    var apiInterface: ApiInterface,
    var executor: ExecutorService,
    var sharedPreferences: SharedPreferences,
    private var f5RequestFactory: F5Request.Factory,
    var f52RequestFactory: F52Request.Factory)
{
    fun getShops(): Future<Observable<F4Response>>
    {
        val request = f5RequestFactory.create(sharedPreferences.getString("userId", null).toString())

        val callable = Callable { apiInterface.getShops2(request) }

        return object : Future<Observable<F4Response>>
        {

            override fun cancel(mayInterruptIfRunning: Boolean): Boolean
            {
                if(mayInterruptIfRunning) executor.shutdown()
                return false
            }

            override fun isCancelled(): Boolean
            {
                return executor.isShutdown
            }

            override fun isDone(): Boolean
            {
                return executor.isTerminated
            }

            override fun get(): Observable<F4Response>
            {
                return executor.submit(callable).get()
            }

            override fun get(timeout: Long, unit: TimeUnit?): Observable<F4Response>
            {
                return executor.submit(callable).get(timeout, unit)
            }

        }
    }

    fun getShop(shopId: String): Future<Observable<F4Response>>
    {
        val request = f52RequestFactory.create(shopId)

        val callable = Callable { apiInterface.getShops3(request) }

        return object : Future<Observable<F4Response>>
        {

            override fun cancel(mayInterruptIfRunning: Boolean): Boolean
            {
                if(mayInterruptIfRunning) executor.shutdown()
                return false
            }

            override fun isCancelled(): Boolean
            {
                return executor.isShutdown
            }

            override fun isDone(): Boolean
            {
                return executor.isTerminated
            }

            override fun get(): Observable<F4Response>
            {
                return executor.submit(callable).get()
            }

            override fun get(timeout: Long, unit: TimeUnit?): Observable<F4Response>
            {
                return executor.submit(callable).get(timeout, unit)
            }

        }
    }

    companion object
    {
        private const val TAG = "F5MyShopRepository"
    }
}