package com.example.rstaak.viewModel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rstaak.repository.F32Repository
import com.example.rstaak.req_res.f32.F32Request
import com.example.rstaak.req_res.f32.F32Response
import com.example.rstaak.req_res.f321_location.LocationDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class F32ViewModel @Inject constructor(var f32Repository: F32Repository,
    var sharedPreferences: SharedPreferences): ViewModel()
{

    var locationName = MutableLiveData("")
    var shopName = MutableLiveData("")
    var categoryName = MutableLiveData("")
    var title = MutableLiveData("")
    var price = MutableLiveData("")
    var description = MutableLiveData("")
    var isLoading = MutableLiveData(false)
    var error = MutableLiveData(false)
    private var compositeDisposable = CompositeDisposable()
    @Inject
    lateinit var f32RequestFactory: F32Request.Factory
    var errorMessage1 = MutableLiveData("")
    var errorMessage2 = MutableLiveData("")
    var errorMessage3 = MutableLiveData("")
    var errorMessage4 = MutableLiveData("")
    var errorMessage5 = MutableLiveData("")
    var response = MutableLiveData(0)

    fun createNewProduct(shopId: String, categoryId: String, ifUsed: Boolean, locationDetails: LocationDetails,imageList: List<String>, price: Int)
    {
        when
        {
            locationName.value!!.isEmpty() -> errorMessage1.value = "نام شهر یا روستا را مشخص کنید."
            shopName.value!!.isEmpty() -> errorMessage2.value = "نام فروشگاه راانتخاب کنید."
            categoryName.value!!.isEmpty() -> errorMessage3.value = "دسته را انتخاب کنید."
            title.value!!.isEmpty() -> errorMessage4.value = "عنوان محصول یا خدمات را مشخص کنید."
            description.value!!.isEmpty() -> errorMessage5.value = "توضیحاتی درباره محصول خود بنویسید."
            else ->
            {
                isLoading.value = true

                val fragment9Request = f32RequestFactory.create(sharedPreferences.getString("phoneNumber",null).toString(), title = title.value!!, shopId = shopId, categoryId = categoryId, description = description.value!!, ifUsed = ifUsed, rostaakLocation = locationDetails, imageList = imageList, price = price)

                compositeDisposable.add(f32Repository.createNewProduct(fragment9Request).get()
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(object : DisposableObserver<F32Response>()
                    {
                        override fun onNext(t: F32Response)
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
        private const val TAG = "Fragment9ViewModel"
    }

    override fun onCleared()
    {
        super.onCleared()
        compositeDisposable.clear()
    }
}