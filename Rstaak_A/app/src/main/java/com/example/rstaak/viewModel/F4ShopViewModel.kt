package com.example.rstaak.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rstaak.model.f4_shop.F4ShopModel
import com.example.rstaak.repository.F4ShopRepository
import com.example.rstaak.req_res.f4_shop.F4LDVResponse
import com.example.rstaak.req_res.f4_shop.F4Response
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class F4ShopViewModel @Inject constructor(var f4ShopRepository: F4ShopRepository): ViewModel()
{
    private var compositeDisposable = CompositeDisposable()
    var models = ArrayList<F4ShopModel>()
    var modelsLiveData = MutableLiveData<ArrayList<F4ShopModel>>()
    var isLoading = MutableLiveData(false)
    var foundRecord = MutableLiveData(true)
    var error = MutableLiveData(false)
    var factor = 1

    init
    {
        getShops()
    }

    fun getShops()
    {
        isLoading.value = true

        compositeDisposable.add(f4ShopRepository.getShops(factor).get().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<F4Response>()
            {
                override fun onNext(t: F4Response)
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
                    error.value = true
                    Log.i(TAG, "onError: $e")
                }

                override fun onComplete()
                {
                    Log.i(TAG, "onComplete: ")
                }

            }))

    }

    fun likeShop(shopId: String)
    {
        compositeDisposable.add(f4ShopRepository.likeShop(shopId).get()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<F4LDVResponse>()
            {
                override fun onNext(t: F4LDVResponse)
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

    fun dislikeShop(shopId: String)
    {
        compositeDisposable.add(f4ShopRepository.dislikeShop(shopId).get()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<F4LDVResponse>()
            {
                override fun onNext(t: F4LDVResponse)
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

    fun viewShop(shopId: String)
    {
        compositeDisposable.add(f4ShopRepository.viewShop(shopId).get()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<F4LDVResponse>()
            {
                override fun onNext(t: F4LDVResponse)
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

    fun setLikeView(boolean: Boolean, f4LDVResponse: F4LDVResponse)
    {
        for (item in models)
        {
            if(item.shopId == f4LDVResponse.message.shopId)
            {
                item.likedNumber = f4LDVResponse.message.likedNumber
                item.viewedNumber = f4LDVResponse.message.viewedNumber
                item.likeFlag = boolean
                modelsLiveData.value = models
                break
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