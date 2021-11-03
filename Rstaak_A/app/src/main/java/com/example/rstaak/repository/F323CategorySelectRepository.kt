package com.example.rstaak.repository

import android.util.Log
import com.example.rstaak.req_res.f323_category_select.F323Response
import com.example.rstaak.retrofit.ApiInterface
import io.reactivex.Observable
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class F323CategorySelectRepository @Inject constructor(
    var apiInterface: ApiInterface,
    var executor: ExecutorService)
{
    fun getCategory(): Future<Observable<F323Response>>
    {
        val callable = Callable { apiInterface.getCategory() }

        return object : Future<Observable<F323Response>>
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

            override fun get(): Observable<F323Response>
            {
                return executor.submit(callable).get()
            }

            override fun get(timeout: Long, unit: TimeUnit?): Observable<F323Response>
            {
                return executor.submit(callable).get(timeout, unit)
            }

        }
    }

    companion object
    {
        private const val TAG = "CategorySelectRepositor"
    }

}