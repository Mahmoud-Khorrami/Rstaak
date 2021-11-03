package com.example.rstaak.repository

import android.content.SharedPreferences
import com.example.rstaak.req_res.f4_shop.F4LDVRequest
import com.example.rstaak.req_res.f4_shop.F4LDVResponse
import com.example.rstaak.req_res.f4_shop.F4Request
import com.example.rstaak.req_res.f4_shop.F4Response
import com.example.rstaak.req_res.f51_new_shop.F51Request
import com.example.rstaak.req_res.f51_new_shop.F51Response
import com.example.rstaak.req_res.f52_edit_shop.F52Request
import com.example.rstaak.req_res.f5_my_shop.F5Request
import com.example.rstaak.retrofit.ApiInterface
import io.reactivex.Observable
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class F52EditShopRepository @Inject constructor(
    var apiInterface: ApiInterface,
    var executor: ExecutorService)
{

    fun editShop(f51Request: F51Request): Future<Observable<F51Response>>
    {
        val callable = Callable { apiInterface.editShop(f51Request) }

        return object : Future<Observable<F51Response>>
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

            override fun get(): Observable<F51Response>
            {
                return executor.submit(callable).get()
            }

            override fun get(timeout: Long, unit: TimeUnit?): Observable<F51Response>
            {
                return executor.submit(callable).get(timeout, unit)
            }

        }
    }
}