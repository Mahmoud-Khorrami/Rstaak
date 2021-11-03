package com.example.rstaak.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.rstaak.general.MyResponse
import com.example.rstaak.general.MyResult
import com.example.rstaak.req_res.f3_product.*
import com.example.rstaak.retrofit.ApiInterface
import com.example.rstaak.retrofit.ChatApiInterface
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import java.util.concurrent.*
import javax.inject.Inject
import kotlinx.coroutines.flow.flow as flow

class F3ProductRepository @Inject constructor(
    var apiInterface: ApiInterface,
    var executor: ExecutorService,
    var sharedPreferences: SharedPreferences,
    var f3Request1Factory: F3Request1.ProductRequestFactory,
    var f3LDVRequestFactory: F3LDVRequest.ProductLDVRequestFactory,
    var f3Request2: F3Request2.Factory,
    var chatApiInterface: ChatApiInterface,
    var f3Request3Factory: F3Request3.Factory): MyResponse()
{

    fun getProducts1(factor: Int): Future<Observable<F3Response>>
    {
        val f3Request1: F3Request1 = f3Request1Factory.create(50, factor, sharedPreferences.getString("userId", null).toString())

        val callable = Callable { apiInterface.getProducts1(f3Request1) }

        return object : Future<Observable<F3Response>>
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

            override fun get(): Observable<F3Response>
            {
                Log.i(TAG, "get: ")
                return executor.submit(callable).get()
            }

            override fun get(timeout: Long, unit: TimeUnit?): Observable<F3Response>
            {
                Log.i(TAG, "get: $timeout + $unit")
                return executor.submit(callable).get(timeout, unit)
            }

        }
    }

    fun getProducts2(factor: Int, shopId: String, categoryId: String): Future<Observable<F3Response>>
    {
        val request = f3Request2.create(50, factor, sharedPreferences.getString("userId", null).toString(), shopId, categoryId)

        val callable = Callable { apiInterface.getProducts2(request) }

        return object : Future<Observable<F3Response>>
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

            override fun get(): Observable<F3Response>
            {
                Log.i(TAG, "get: ")
                return executor.submit(callable).get()
            }

            override fun get(timeout: Long, unit: TimeUnit?): Observable<F3Response>
            {
                Log.i(TAG, "get: $timeout + $unit")
                return executor.submit(callable).get(timeout, unit)
            }

        }
    }

    fun likeProduct(productId: String): Future<Observable<F3LDVResponse>>
    {
        val productLDVRequest = f3LDVRequestFactory.create(productId, sharedPreferences.getString("userId", null)
            .toString())

        val callable = Callable { apiInterface.likeProduct(productLDVRequest) }

        return object : Future<Observable<F3LDVResponse>>
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

            override fun get(): Observable<F3LDVResponse>
            {
                Log.i(TAG, "get: ")
                return executor.submit(callable).get()
            }

            override fun get(timeout: Long, unit: TimeUnit?): Observable<F3LDVResponse>
            {
                Log.i(TAG, "get: $timeout + $unit")
                return executor.submit(callable).get(timeout, unit)
            }
        }
    }

    fun dislikeProduct(productId: String): Future<Observable<F3LDVResponse>>
    {
        val productLDVRequest = f3LDVRequestFactory.create(productId, sharedPreferences.getString("userId", null)
            .toString())

        val callable = Callable { apiInterface.dislikeProduct(productLDVRequest) }

        return object : Future<Observable<F3LDVResponse>>
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

            override fun get(): Observable<F3LDVResponse>
            {
                Log.i(TAG, "get: ")
                return executor.submit(callable).get()
            }

            override fun get(timeout: Long, unit: TimeUnit?): Observable<F3LDVResponse>
            {
                Log.i(TAG, "get: $timeout + $unit")
                return executor.submit(callable).get(timeout, unit)
            }
        }
    }

    fun viewProduct(productId: String): Future<Observable<F3LDVResponse>>
    {
        val productLDVRequest = f3LDVRequestFactory.create(productId, sharedPreferences.getString("userId", null)
            .toString())

        val callable = Callable { apiInterface.viewProduct(productLDVRequest) }

        return object : Future<Observable<F3LDVResponse>>
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

            override fun get(): Observable<F3LDVResponse>
            {
                Log.i(TAG, "get: ")
                return executor.submit(callable).get()
            }

            override fun get(timeout: Long, unit: TimeUnit?): Observable<F3LDVResponse>
            {
                Log.i(TAG, "get: $timeout + $unit")
                return executor.submit(callable).get(timeout, unit)
            }
        }
    }

    suspend fun chatStatus(ownerId: String, productId: String): Flow<MyResult<F3Response3>>
    {
        val users = ArrayList<String>()
        users.add(sharedPreferences.getString("userId", null).toString())
        users.add(ownerId)
        val request = f3Request3Factory.create(users, productId)

        return flow {

            emit(MyResult.loading())
            emit(getResult { chatApiInterface.chatStatus(request) })

        }.flowOn(Dispatchers.IO)
    }


    companion object
    {
        private const val TAG = "ProductRepository"
    }
}