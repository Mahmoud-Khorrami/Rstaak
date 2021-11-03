package com.example.rstaak.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rstaak.model.f322_shop_select.F322Model
import com.example.rstaak.repository.F322ShopSelectRepository
import com.example.rstaak.req_res.f322_shop_select.F322Response
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class F322ShopSelectViewModel @Inject constructor(var f322ShopSelectRepository: F322ShopSelectRepository):
    ViewModel()
{
    private var compositeDisposable = CompositeDisposable()
    var isLoading = MutableLiveData(false)
    var error = MutableLiveData(false)
    var shopSelectModelsData = MutableLiveData<ArrayList<F322Model>>()
    var shopSelectModels = ArrayList<F322Model>()
    @Inject
    lateinit var shopSelectModelFactory: F322Model.Factory

    init
    {
        getShop()
    }

    fun getShop()
    {
        isLoading.value = true

        compositeDisposable.add(f322ShopSelectRepository.getShop().get().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<F322Response>()
            {
                override fun onNext(t: F322Response)
                {
                    isLoading.value = false

                    if(t.status == 200)
                    {
                        for (item in t.message.result)
                        {
                            var image = ""
                            if(item.imageList.isNotEmpty())
                                image = item.imageList[0]

                            val model = shopSelectModelFactory.create(id = item.shopId, title = item.title, image = image )
                            shopSelectModels.add(model)
                        }

                        shopSelectModelsData.value = shopSelectModels
                    }
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

    companion object
    {
        private const val TAG = "ShopSelectViewModel"
    }

    override fun onCleared()
    {
        super.onCleared()
        compositeDisposable.clear()
    }
}