package com.example.rstaak.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rstaak.model.f41_shop_details.F41Model1
import com.example.rstaak.repository.F41ShopDetailsRepository
import com.example.rstaak.req_res.f41_shop_details.F41Response
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class F41ShopDetailsViewModel @Inject constructor(var f41ShopDetailsRepository: F41ShopDetailsRepository): ViewModel()
{

    private var compositeDisposable = CompositeDisposable()
    var isLoading = MutableLiveData(false)
    var error = MutableLiveData(false)
    var f41Models1 = ArrayList<F41Model1>()
    var f41Models1Data = MutableLiveData<ArrayList<F41Model1>>()

    fun getShopsSpecial(shopId: String)
    {
        isLoading.value = true

        compositeDisposable.add(f41ShopDetailsRepository.getShopsSpecial(shopId).get().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<F41Response>()
            {
                override fun onNext(t: F41Response)
                {
                    isLoading.value = false
                    f41Models1.clear()

                    Log.i(TAG, "onNext: ${t.message.result}")
                    if(t.status == 200)
                    {
                        f41Models1.addAll(t.message.result)
                        f41Models1Data.value = f41Models1
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
        private const val TAG = "F41ViewModel"
    }

    override fun onCleared()
    {
        super.onCleared()
        compositeDisposable.clear()
    }
}