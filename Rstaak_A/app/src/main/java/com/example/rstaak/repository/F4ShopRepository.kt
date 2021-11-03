package com.example.rstaak.repository

import android.content.SharedPreferences
import com.example.rstaak.req_res.f4_shop.F4LDVRequest
import com.example.rstaak.req_res.f4_shop.F4LDVResponse
import com.example.rstaak.req_res.f4_shop.F4Request
import com.example.rstaak.req_res.f4_shop.F4Response
import com.example.rstaak.retrofit.ApiInterface
import io.reactivex.Observable
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class F4ShopRepository @Inject constructor(
    var apiInterface: ApiInterface,
    var executor: ExecutorService,
    var sharedPreferences: SharedPreferences,
    private var f4RequestFactory: F4Request.Factory,
    private var f4LDVRequestFactory: F4LDVRequest.Factory)
{

    fun getShops(factor: Int): Future<Observable<F4Response>>
    {
        val request = f4RequestFactory.create(50, factor, sharedPreferences.getString("userId", null).toString())

        val callable = Callable { apiInterface.getShops1(request) }

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

    fun likeShop(shopId: String): Future<Observable<F4LDVResponse>>
    {
        val request = f4LDVRequestFactory.create(shopId, sharedPreferences.getString("userId", null)
            .toString())

        val callable = Callable { apiInterface.likeShop(request) }

        return object : Future<Observable<F4LDVResponse>>
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

            override fun get(): Observable<F4LDVResponse>
            {
                return executor.submit(callable).get()
            }

            override fun get(timeout: Long, unit: TimeUnit?): Observable<F4LDVResponse>
            {
                return executor.submit(callable).get(timeout, unit)
            }
        }
    }

    fun dislikeShop(shopId: String): Future<Observable<F4LDVResponse>>
    {
        val request = f4LDVRequestFactory.create(shopId, sharedPreferences.getString("userId", null)
            .toString())

        val callable = Callable { apiInterface.dislikeShop(request) }

        return object : Future<Observable<F4LDVResponse>>
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

            override fun get(): Observable<F4LDVResponse>
            {
                return executor.submit(callable).get()
            }

            override fun get(timeout: Long, unit: TimeUnit?): Observable<F4LDVResponse>
            {
                return executor.submit(callable).get(timeout, unit)
            }
        }
    }

    fun viewShop(shopId: String): Future<Observable<F4LDVResponse>>
    {
        val request = f4LDVRequestFactory.create(shopId, sharedPreferences.getString("userId", null)
            .toString())

        val callable = Callable { apiInterface.viewShop(request) }

        return object : Future<Observable<F4LDVResponse>>
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

            override fun get(): Observable<F4LDVResponse>
            {
                return executor.submit(callable).get()
            }

            override fun get(timeout: Long, unit: TimeUnit?): Observable<F4LDVResponse>
            {
                return executor.submit(callable).get(timeout, unit)
            }
        }
    }

    companion object
    {
        private const val TAG = "ShopsRepository"
    }
}