package com.example.rstaak.repository

import com.example.rstaak.req_res.f41_shop_details.F41Request
import com.example.rstaak.req_res.f41_shop_details.F41Response
import com.example.rstaak.retrofit.ApiInterface
import io.reactivex.Observable
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class F41ShopDetailsRepository@Inject constructor(
    var apiInterface: ApiInterface,
    var executor: ExecutorService,
    var f41RequestFactory: F41Request.Factory)
{
    fun getShopsSpecial(shopId: String): Future<Observable<F41Response>>
    {
        val request = f41RequestFactory.create(shopId)
        val callable = Callable { apiInterface.getShopsSpecial(request) }

        return object : Future<Observable<F41Response>>
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

            override fun get(): Observable<F41Response>
            {
                return executor.submit(callable).get()
            }

            override fun get(timeout: Long, unit: TimeUnit?): Observable<F41Response>
            {
                return executor.submit(callable).get(timeout, unit)
            }

        }
    }
}