package com.example.rstaak.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rstaak.general.MyResult
import com.example.rstaak.model.f3_product.F3ProductModel
import com.example.rstaak.req_res.f3_product.F3Response
import com.example.rstaak.repository.F3ProductRepository
import com.example.rstaak.req_res.f3_product.F3LDVResponse
import com.example.rstaak.req_res.f3_product.F3Response3
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class F3ProductViewModel @Inject constructor(var f3ProductRepository: F3ProductRepository): ViewModel()
{

    private var compositeDisposable = CompositeDisposable()
    var models = ArrayList<F3ProductModel>()
    var modelsLiveData = MutableLiveData<ArrayList<F3ProductModel>>()
    var isLoading = MutableLiveData(false)
    var foundRecord = MutableLiveData(true)
    var error1 = MutableLiveData(false)
    var factor = 1
    var f3Response3Data = MutableLiveData<MyResult<F3Response3>>()

    fun getProducts1()
    {
        isLoading.value = true

        compositeDisposable.add(f3ProductRepository.getProducts1(factor).get().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<F3Response>()
            {
                override fun onNext(t: F3Response)
                {
                    isLoading.value = false
                    if(t.foundRecord>0)
                    {
                        factor += 1
                        models.addAll(t.message.result)
                        modelsLiveData.value = models
                    }

                    else
                        foundRecord.value = false
                }

                override fun onError(e: Throwable)
                {
                    isLoading.value = false
                    error1.value = true
                    Log.i(TAG, "onError: $e")
                }

                override fun onComplete()
                {
                    Log.i(TAG, "onComplete: ")
                }

            }))

    }

    fun getProducts2(shopId: String, categoryId: String)
    {
        isLoading.value = true

        compositeDisposable.add(f3ProductRepository.getProducts2(factor,shopId,categoryId).get().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<F3Response>()
            {
                override fun onNext(t: F3Response)
                {
                    isLoading.value = false
                    if(t.foundRecord>0)
                    {
                        factor += 1
                        models.addAll(t.message.result)
                        modelsLiveData.value = models
                    }

                    else
                        foundRecord.value = false
                }

                override fun onError(e: Throwable)
                {
                    isLoading.value = false
                    error1.value = true
                    Log.i(TAG, "onError: $e")
                }

                override fun onComplete()
                {
                    Log.i(TAG, "onComplete: ")
                }

            }))

    }

    fun likeProduct(productId: String)
    {
        compositeDisposable.add(f3ProductRepository.likeProduct(productId).get()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<F3LDVResponse>()
            {
                override fun onNext(t: F3LDVResponse)
                {
                    setLikeView(true, t)
                }

                override fun onError(e: Throwable)
                {
                    Log.i(TAG, "onError: $e")
                }

                override fun onComplete()
                {
                    Log.i(TAG, "onComplete: ")
                }

            }))
    }

    fun dislikeProduct(productId: String)
    {
        compositeDisposable.add(f3ProductRepository.dislikeProduct(productId).get()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<F3LDVResponse>()
            {
                override fun onNext(t: F3LDVResponse)
                {
                    setLikeView(false, t)
                }

                override fun onError(e: Throwable)
                {
                    Log.i(TAG, "onError: $e")
                }

                override fun onComplete()
                {
                    Log.i(TAG, "onComplete: ")
                }

            }))
    }

    fun viewProduct(productId: String)
    {
        compositeDisposable.add(f3ProductRepository.viewProduct(productId).get()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<F3LDVResponse>()
            {
                override fun onNext(t: F3LDVResponse)
                {
                    setLikeView(false, t)
                }

                override fun onError(e: Throwable)
                {
                    Log.i(TAG, "onError: $e")
                }

                override fun onComplete()
                {
                    Log.i(TAG, "onComplete: ")
                }

            }))
    }

    fun setLikeView(boolean: Boolean, f3LDVResponse: F3LDVResponse)
    {
        for (item in models)
        {
            if(item.productId == f3LDVResponse.message.productId)
            {
                item.likedNumber = f3LDVResponse.message.likedNumber
                item.viewedNumber = f3LDVResponse.message.viewedNumber
                item.likeFlag = boolean
                modelsLiveData.value = models
                break
            }

        }
    }

    fun chatStatus(ownerId: String, productId: String)
    {
        viewModelScope.launch {
          f3ProductRepository.chatStatus(ownerId, productId).collect {
                f3Response3Data.value = it
          }
        }
    }

    companion object
    {
        private const val TAG = "ProductViewModel"
    }

    override fun onCleared()
    {
        super.onCleared()
        compositeDisposable.clear()
    }
}