package com.example.rstaak.repository

import android.util.Log
import com.example.rstaak.req_res.f321_location.*
import com.example.rstaak.retrofit.ApiInterface
import io.reactivex.Observable
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class F321LocationRepository @Inject constructor(
    var apiInterface: ApiInterface,
    var executor: ExecutorService,
    var shahrestanRequestFactory: ShahrestanRequest.ShahrestanRequestFactory,
    var bakhshRequestFactory: BakhshRequest.BakhshRequestFactory,
    var dehshahrRequestFactory: DehshahrRequest.DehshahrRequestFactory,
    var abadiRequestFactory: AbadiRequest.AbadiRequestFactory,
    var locationRegexRequestFactory: LocationRegexRequest.Factory)
{
    fun getOstan(): Future<Observable<LocationResponse>>
    {
        val callable = Callable { apiInterface.getOstan() }

        return object : Future<Observable<LocationResponse>>
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

            override fun get(): Observable<LocationResponse>
            {
                Log.i(TAG, "get: ")
                return executor.submit(callable).get()
            }

            override fun get(timeout: Long, unit: TimeUnit?): Observable<LocationResponse>
            {
                Log.i(TAG, "get: $timeout + $unit")
                return executor.submit(callable).get(timeout, unit)
            }

        }
    }

    fun getShahrestan(id: String): Future<Observable<LocationResponse>>
    {
        val request = shahrestanRequestFactory.create(id)
        val callable = Callable { apiInterface.getShahrestan(request) }

        return object : Future<Observable<LocationResponse>>
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

            override fun get(): Observable<LocationResponse>
            {
                Log.i(TAG, "get: ")
                return executor.submit(callable).get()
            }

            override fun get(timeout: Long, unit: TimeUnit?): Observable<LocationResponse>
            {
                Log.i(TAG, "get: $timeout + $unit")
                return executor.submit(callable).get(timeout, unit)
            }

        }
    }

    fun getBakhsh(id: String): Future<Observable<LocationResponse>>
    {
        val request = bakhshRequestFactory.create(id)
        val callable = Callable { apiInterface.getBakhsh(request) }

        return object : Future<Observable<LocationResponse>>
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

            override fun get(): Observable<LocationResponse>
            {
                Log.i(TAG, "get: ")
                return executor.submit(callable).get()
            }

            override fun get(timeout: Long, unit: TimeUnit?): Observable<LocationResponse>
            {
                Log.i(TAG, "get: $timeout + $unit")
                return executor.submit(callable).get(timeout, unit)
            }

        }
    }

    fun getDehshahr(id: String): Future<Observable<LocationResponse>>
    {
        val request = dehshahrRequestFactory.create(id)
        val callable = Callable { apiInterface.getDehshahr(request) }

        return object : Future<Observable<LocationResponse>>
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

            override fun get(): Observable<LocationResponse>
            {
                Log.i(TAG, "get: ")
                return executor.submit(callable).get()
            }

            override fun get(timeout: Long, unit: TimeUnit?): Observable<LocationResponse>
            {
                Log.i(TAG, "get: $timeout + $unit")
                return executor.submit(callable).get(timeout, unit)
            }

        }
    }

    fun getAbadi(id: String): Future<Observable<LocationResponse>>
    {
        val request = abadiRequestFactory.create(id)
        val callable = Callable { apiInterface.getAbadi(request) }

        return object : Future<Observable<LocationResponse>>
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

            override fun get(): Observable<LocationResponse>
            {
                Log.i(TAG, "get: ")
                return executor.submit(callable).get()
            }

            override fun get(timeout: Long, unit: TimeUnit?): Observable<LocationResponse>
            {
                Log.i(TAG, "get: $timeout + $unit")
                return executor.submit(callable).get(timeout, unit)
            }

        }
    }

    fun locationRegex(newText: String): Future<Observable<LocationResponse>>
    {
        val request = locationRegexRequestFactory.create(newText)
        val callable = Callable { apiInterface.locationRegex(request) }

        return object : Future<Observable<LocationResponse>>
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

            override fun get(): Observable<LocationResponse>
            {
                Log.i(TAG, "get: ")
                return executor.submit(callable).get()
            }

            override fun get(timeout: Long, unit: TimeUnit?): Observable<LocationResponse>
            {
                Log.i(TAG, "get: $timeout + $unit")
                return executor.submit(callable).get(timeout, unit)
            }

        }
    }

    companion object
    {
        private const val TAG = "LocationRepository"
    }
}