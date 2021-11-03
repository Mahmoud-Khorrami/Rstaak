package com.example.rstaak.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rstaak.model.f321_location.F321Model
import com.example.rstaak.repository.F321LocationRepository
import com.example.rstaak.req_res.f321_location.LocationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class F321LocationViewModel @Inject constructor(var f321LocationRepository: F321LocationRepository): ViewModel()
{

    private var compositeDisposable = CompositeDisposable()
    var isLoading = MutableLiveData(false)
    var error = MutableLiveData(false)
    var notFound = MutableLiveData(false)
    @Inject
    lateinit var f321ModelFactory: F321Model.Factory
    var locationModels = ArrayList<F321Model>()
    var locationModelsData = MutableLiveData<ArrayList<F321Model>>()

    init
    {
        getOstan()
    }

    fun getOstan()
    {
        isLoading.value = true
        locationModels.clear()

        compositeDisposable.add(f321LocationRepository.getOstan().get().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<LocationResponse>()
            {
                override fun onNext(t: LocationResponse)
                {
                    isLoading.value = false
                    if(t.foundRecords>0)
                    {
                        for (item in t.message.result)
                        {
                            val model = f321ModelFactory.create( ostanId = item.id, ostanName = item.name, type = item.type)
                            locationModels.add(model)
                        }

                        locationModelsData.value = locationModels
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

    fun getShahrestan(id: String)
    {
        locationModels.clear()
        isLoading.value = true

        compositeDisposable.add(f321LocationRepository.getShahrestan(id).get().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<LocationResponse>()
            {
                override fun onNext(t: LocationResponse)
                {
                    isLoading.value = false

                    if(t.foundRecords>0)
                    {
                        for (item in t.message.result)
                        {
                            val model = f321ModelFactory.create( ostanId = item.ostan.id, ostanName = item.ostan.name, type = item.type, shahrestanId = item.id, shahrestanName = item.name)
                            locationModels.add(model)
                        }

                        locationModelsData.value = locationModels
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

    fun getBakhsh(id: String)
    {
        locationModels.clear()
        isLoading.value = true

        compositeDisposable.add(f321LocationRepository.getBakhsh(id).get().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<LocationResponse>()
            {
                override fun onNext(t: LocationResponse)
                {
                    isLoading.value = false

                    if(t.foundRecords>0)
                    {
                        for (item in t.message.result)
                        {
                            val model = f321ModelFactory.create( ostanId = item.ostan.id, ostanName = item.ostan.name, type = item.type, shahrestanId = item.shahrestan.id, shahrestanName = item.shahrestan.name, bakhshId = item.id, bakhshName = item.name)
                            locationModels.add(model)
                        }

                        locationModelsData.value = locationModels
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

    fun getDehshahr(id: String)
    {
        locationModels.clear()
        isLoading.value = true

        compositeDisposable.add(f321LocationRepository.getDehshahr(id).get().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<LocationResponse>()
            {
                override fun onNext(t: LocationResponse)
                {
                    isLoading.value = false

                    if(t.foundRecords>0)
                    {
                        for (item in t.message.result)
                        {
                            val model = f321ModelFactory.create( ostanId = item.ostan.id, ostanName = item.ostan.name, type = item.type, shahrestanId = item.shahrestan.id, shahrestanName = item.shahrestan.name, bakhshId = item.bakhsh.id, bakhshName = item.bakhsh.name, dehshahrId = item.id, dehshahrName = item.name)
                            locationModels.add(model)
                        }

                        locationModelsData.value = locationModels
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

    fun getAbadi(id: String)
    {
        locationModels.clear()
        isLoading.value = true

        compositeDisposable.add(f321LocationRepository.getAbadi(id).get().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<LocationResponse>()
            {
                override fun onNext(t: LocationResponse)
                {
                    isLoading.value = false

                    if(t.foundRecords>0)
                    {
                        for (item in t.message.result)
                        {
                            val model = f321ModelFactory.create( ostanId = item.ostan.id, ostanName = item.ostan.name, type = item.type, shahrestanId = item.shahrestan.id, shahrestanName = item.shahrestan.name, bakhshId = item.bakhsh.id, bakhshName = item.bakhsh.name, dehshahrId = item.dehestan.id, dehshahrName = item.dehestan.name, abadiId = item.id, abadiName = item.name)
                            locationModels.add(model)
                        }

                        locationModelsData.value = locationModels
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

    fun locationRegex(newText: String)
    {
        locationModels.clear()
        isLoading.value = true

        compositeDisposable.add(f321LocationRepository.locationRegex(newText).get().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<LocationResponse>()
            {
                override fun onNext(t: LocationResponse)
                {
                    isLoading.value = false

                    if(t.status == 200)
                    {
                        for (item in t.message.result)
                        {
                            if(item.type == "abadi")
                            {
                                val model = f321ModelFactory.create(ostanId = item.ostan.id, ostanName = item.ostan.name, type = item.type, shahrestanId = item.shahrestan.id, shahrestanName = item.shahrestan.name, bakhshId = item.bakhsh.id, bakhshName = item.bakhsh.name, dehshahrId = item.dehestan.id, dehshahrName = item.dehestan.name, abadiId = item.id, abadiName = item.name)
                                locationModels.add(model)
                            }
                            else if(item.type == "shahr")
                            {
                                val model = f321ModelFactory.create(ostanId = item.ostan.id, ostanName = item.ostan.name, type = item.type, shahrestanId = item.shahrestan.id, shahrestanName = item.shahrestan.name, bakhshId = item.bakhsh.id, bakhshName = item.bakhsh.name, dehshahrId = item.id, dehshahrName = item.name)
                                locationModels.add(model)
                            }
                        }

                        locationModelsData.value = locationModels
                    }
                    else if(t.status == 204)
                        notFound.value = true

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
        private const val TAG = "LocationViewModel"
    }

    override fun onCleared()
    {
        super.onCleared()
        compositeDisposable.clear()
    }
}