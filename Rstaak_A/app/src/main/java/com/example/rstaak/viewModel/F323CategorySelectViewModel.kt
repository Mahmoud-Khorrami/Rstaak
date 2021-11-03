package com.example.rstaak.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rstaak.model.f323_category_select.F323Model
import com.example.rstaak.repository.F323CategorySelectRepository
import com.example.rstaak.req_res.f323_category_select.F323Response
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class F323CategorySelectViewModel @Inject constructor(var f323CategorySelectRepository: F323CategorySelectRepository): ViewModel()
{
    private var compositeDisposable = CompositeDisposable()
    var isLoading = MutableLiveData(false)
    var error = MutableLiveData(false)
    @Inject
    lateinit var f323ModelFactory: F323Model.Factory
    var categoryModel = ArrayList<F323Model>()
    var categoryModelData = MutableLiveData<ArrayList<F323Model>>()

    init
    {
        getCategory()
    }

    fun getCategory()
    {
        isLoading.value = true

        compositeDisposable.add(f323CategorySelectRepository.getCategory().get().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<F323Response>()
            {
                override fun onNext(t: F323Response)
                {
                    isLoading.value = false
                    if(t.status == 200)
                    {
                        for (item in t.message.result)
                        {
                            val model = f323ModelFactory.create(categoryId = item.categoryId,title = item.title,imageList = item.imageList,childCategories = item.childCategories, child = false)
                            categoryModel.add(model)
                        }

                        categoryModelData.value = categoryModel
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
        private const val TAG = "CategorySelectViewModel"
    }

    override fun onCleared()
    {
        super.onCleared()
        compositeDisposable.clear()
    }
}