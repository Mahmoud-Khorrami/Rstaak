package com.example.rstaak.viewModel

import android.content.SharedPreferences
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rstaak.req_res.f1_register.F1Response
import com.example.rstaak.repository.F1RegisterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class F1RegisterViewModel  @Inject constructor(var f1RegisterRepository: F1RegisterRepository,
    var sharedPreferencesEditor: SharedPreferences.Editor): ViewModel()
{
    var phoneNumber = MutableLiveData("")
    var errorMessage = MutableLiveData("")
    var isLoading = ObservableField(false)
    private var compositeDisposable = CompositeDisposable()
    var response1 = MutableLiveData<String>()
    var response2 = MutableLiveData<String>()
    var resendVisibility = MutableLiveData(true)
    var timer : MutableLiveData<Long> = MutableLiveData(120)
    var registerCode = MutableLiveData("")
    lateinit var f1Response: F1Response
    var bnvVisibility = ObservableField(false)

    fun checkPhoneNumber()
    {
        if(phoneNumber.value!!.isEmpty())
        {
            errorMessage.value = "شماره همراه را وارد کنید"
        }

        else
            register()
    }

    private fun register()
    {
        isLoading.set(true)

        compositeDisposable.add(f1RegisterRepository.register(phoneNumber.value!!).get()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<F1Response>()
            {
                override fun onNext(t: F1Response)
                {
                    isLoading.set(false)

                    if(t.status == 200 || t.status == 303)
                    {
                        Log.i(TAG, "registerCode: ${t.registerModel.registerCode}")
                        Log.i(TAG, "onNext: ${t.status}")

                        f1Response = t
                        response1.value = "success"
                        errorMessage.value = ""
                        resendVisibility.value = true
                        timer1()
                    }
                }

                override fun onError(e: Throwable)
                {
                    isLoading.set(false)
                    response1.value = e.toString()
                    response2.value = e.toString()
                    Log.i(TAG, "onError: $e")
                }

                override fun onComplete()
                {
                    isLoading.set(false)
                    Log.i(TAG, "onComplete: ")
                }

            }))

    }

    private fun timer1()
    {
        val observable: Observable<Long> = Observable
            .interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io()).takeWhile { aLong ->
                aLong <= 120
            }.observeOn(AndroidSchedulers.mainThread())


        compositeDisposable.add(observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Long>()
            {
                override fun onNext(t: Long)
                {
                    timer.value = 120 - t
                }

                override fun onError(e: Throwable)
                {
                    Log.i(TAG, "onError: $e")
                }

                override fun onComplete()
                {
                    Log.i(TAG, "onComplete: ")
                    resendVisibility.value = false
                }

            }))
    }

    fun resend() = register()

    fun approve()
    {
        if(registerCode.value?.length!! == 0)
            errorMessage.value = "کد فعالسازی را وارد کنید."

        else if(registerCode.value.equals(f1Response.registerModel.registerCode))
        {
            if(f1Response.status == 200)
            {
                isLoading.set(true)

                compositeDisposable.add(f1RegisterRepository.approvePhoneNumber(f1Response.registerModel.phoneNumber)
                    .get().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<F1Response>()
                    {
                        override fun onNext(t: F1Response)
                        {
                            if(t.status == 200)
                            {
                                sharedPreferencesEditor.putString("phoneNumber", t.registerModel.phoneNumber)
                                sharedPreferencesEditor.putString("userId", t.registerModel.userId)
                                sharedPreferencesEditor.apply()

                                response2.value = "success"

                                isLoading.set(false)

                                bnvVisibility.set(true)
                            }

                            else
                                response2.value = t.registerModel.error
                        }

                        override fun onError(e: Throwable)
                        {
                            Log.i(TAG, "onError: $e")
                            response2.value = e.toString()

                            isLoading.set(false)
                        }

                        override fun onComplete()
                        {
                            Log.i(TAG, "onComplete: ")

                            isLoading.set(false)
                        }

                    }))
            }

            else if(f1Response.status == 303)
            {
                sharedPreferencesEditor.putString("phoneNumber", f1Response.registerModel.phoneNumber)
                sharedPreferencesEditor.putString("userId", f1Response.registerModel.userId)
                sharedPreferencesEditor.apply()

                response2.value = "success"
                bnvVisibility.set(true)
            }
        }

        else
            errorMessage.value = "کد فعالسازی اشتباه است."

    }

    companion object
    {
        private const val TAG = "RegisterViewModel"
    }

    override fun onCleared()
    {
        super.onCleared()
        compositeDisposable.clear()
    }
}