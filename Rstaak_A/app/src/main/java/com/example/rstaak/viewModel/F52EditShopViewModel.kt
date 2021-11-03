package com.example.rstaak.viewModel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rstaak.model.f4_shop.F4ShopModel
import com.example.rstaak.repository.F4ShopRepository
import com.example.rstaak.repository.F52EditShopRepository
import com.example.rstaak.repository.F5MyShopRepository
import com.example.rstaak.req_res.f4_shop.F4LDVResponse
import com.example.rstaak.req_res.f4_shop.F4Response
import com.example.rstaak.req_res.f51_new_shop.F51Request
import com.example.rstaak.req_res.f51_new_shop.F51Response
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class F52EditShopViewModel @Inject constructor(var f52EditShopRepository: F52EditShopRepository,
    var sharedPreferences: SharedPreferences): ViewModel()
{

    var title = MutableLiveData("")
    var description = MutableLiveData("")
    var address = MutableLiveData("")
    var phoneNumber = MutableLiveData("")
    var isLoading = MutableLiveData(false)
    var error = MutableLiveData(false)
    private var compositeDisposable = CompositeDisposable()
    @Inject
    lateinit var f51RequestFactory: F51Request.Factory
    var errorMessage1 = MutableLiveData("")
    var errorMessage2 = MutableLiveData("")
    var errorMessage3 = MutableLiveData("")
    var response = MutableLiveData(0)

    fun editShop(imageList: List<String>, shopId: String)
    {
        when
        {
            title.value!!.isEmpty() -> errorMessage1.value = "نام فروشگاه را مشخص کنید."
            description.value!!.isEmpty() -> errorMessage2.value = "توضیحاتی درباره فروشگاه خود بنویسید."
            address.value!!.isEmpty() -> errorMessage3.value = "آدرس فروشگاه را مشخص کنید."

            else ->
            {
                isLoading.value = true

                val request= f51RequestFactory.create(cellNumber = sharedPreferences.getString("phoneNumber",null).toString(), shopId = shopId, title = title.value!!, description = description.value!!, address = address.value!!, phoneNumber = phoneNumber.value!!, imageList = imageList)

                compositeDisposable.add(f52EditShopRepository.editShop(request).get()
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(object : DisposableObserver<F51Response>()
                    {
                        override fun onNext(t: F51Response)
                        {
                            isLoading.value = false
                            response.value = t.status
                        }

                        override fun onError(e: Throwable)
                        {
                            isLoading.value = false
                            error.value = true
                            error.value = false
                            Log.i(TAG, "onError: $e")
                        }

                        override fun onComplete()
                        {
                            isLoading.value = false
                            Log.i(TAG, "onComplete: ")
                        }

                    }))

            }
        }

    }

    companion object
    {
        private const val TAG = "F52EditShopViewModel"
    }

    override fun onCleared()
    {
        super.onCleared()
        compositeDisposable.clear()
    }
}