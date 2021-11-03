package com.example.rstaak.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rstaak.model.f4_shop.F4ShopModel
import com.example.rstaak.repository.F5MyShopRepository
import com.example.rstaak.req_res.f4_shop.F4Response
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class F5MyShopViewModel @Inject constructor(var f5MyShopRepository: F5MyShopRepository): ViewModel()
{
    private var compositeDisposable = CompositeDisposable()
    var models = ArrayList<F4ShopModel>()
    var modelsLiveData = MutableLiveData<ArrayList<F4ShopModel>>()
    var isLoading1 = MutableLiveData(false)
    var isLoading2 = MutableLiveData(false)
    var error1 = MutableLiveData(false)
    var error2 = MutableLiveData(false)
    var f4ModelData = MutableLiveData<F4ShopModel>()

    init
    {
        getShops()
    }

    fun getShops()
    {
        isLoading1.value = true

        compositeDisposable.add(f5MyShopRepository.getShops().get().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<F4Response>()
            {
                override fun onNext(t: F4Response)
                {
                    isLoading1.value = false
                    if(t.foundRecord>0)
                    {
                        models.addAll(t.message.result)
                        modelsLiveData.value = models
                    }
                }

                override fun onError(e: Throwable)
                {
                    isLoading1.value = false
                    error1.value = true
                    Log.i(TAG, "onError: $e")
                }

                override fun onComplete()
                {
                    Log.i(TAG, "onComplete: ")
                }

            }))

    }

    fun getShop(shopId: String)
    {
        isLoading2.value = true

        compositeDisposable.add(f5MyShopRepository.getShop(shopId).get().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<F4Response>()
            {
                override fun onNext(t: F4Response)
                {
                    isLoading2.value = false
                    if(t.foundRecord>0)
                    {
                        f4ModelData.value = t.message.result[0]
                    }

                    Log.i(TAG, "onNext: ${t.foundRecord}")
                }

                override fun onError(e: Throwable)
                {
                    isLoading2.value = false
                    error2.value = true
                    error2.value = false
                    Log.i(TAG, "onError: $e")
                }

                override fun onComplete()
                {
                    Log.i(TAG, "onComplete: ")
                }

            }))

    }

    companion object
    {
        private const val TAG = "F5MyShopViewModel"
    }

    override fun onCleared()
    {
        super.onCleared()
        compositeDisposable.clear()
    }
}