package com.example.rstaak.repository

import android.util.Log
import com.example.rstaak.req_res.f1_register.F1Request
import com.example.rstaak.req_res.f1_register.F1Response
import com.example.rstaak.retrofit.ApiInterface
import io.reactivex.Observable
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class F1RegisterRepository @Inject constructor(var apiInterface: ApiInterface,var executor: ExecutorService, var f1RequestFactory: F1Request.Factory)
{
    fun register(phoneNumber: String): Future<Observable<F1Response>>
    {
        val registerRequest = f1RequestFactory.create(phoneNumber)
        val callable = Callable {apiInterface.registerUser(registerRequest)}

        return object : Future<Observable<F1Response>>
        {

            override fun cancel(mayInterruptIfRunning: Boolean): Boolean
            {
                Log.i(TAG, "cancel: ")

                if(mayInterruptIfRunning)
                    executor.shutdown()

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


            override fun get(): Observable<F1Response>
            {
                Log.i(TAG, "get: ")
                return executor.submit(callable).get()
            }


            override fun get(timeout: Long, unit: TimeUnit?): Observable<F1Response>
            {
                Log.i(TAG, "get: $timeout + $unit")
                return executor.submit(callable).get(timeout,unit)
            }

        }
    }

    fun approvePhoneNumber(phoneNumber: String): Future<Observable<F1Response>>
    {

        val registerRequest = f1RequestFactory.create(phoneNumber)
        val callable = Callable {apiInterface.approvePhoneNumber(registerRequest)}

        return object : Future<Observable<F1Response>>
        {

            override fun cancel(mayInterruptIfRunning: Boolean): Boolean
            {
                Log.i(TAG, "cancel: ")

                if(mayInterruptIfRunning)
                    executor.shutdown()

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


            override fun get(): Observable<F1Response>
            {
                Log.i(TAG, "get: ")
                return executor.submit(callable).get()
            }


            override fun get(timeout: Long, unit: TimeUnit?): Observable<F1Response>
            {
                Log.i(TAG, "get: $timeout + $unit")
                return executor.submit(callable).get(timeout,unit)
            }

        }
    }

    companion object
    {
        private const val TAG = "RegisterRepository"
    }
}