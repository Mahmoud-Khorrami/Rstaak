package com.example.rstaak.hilt

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.example.rstaak.R
import com.example.rstaak.database.MyDAO
import com.example.rstaak.database.MyDataBase
import com.example.rstaak.general.Constants
import com.example.rstaak.model.F32Model
import com.example.rstaak.retrofit.ApiInterface
import com.example.rstaak.retrofit.ChatApiInterface
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule
{
    @Provides
    @Singleton
    fun provideRetrofit(): ApiInterface
    {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideChatRetrofit(): ChatApiInterface
    {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.CHAT_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ChatApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideExecutor(): ExecutorService
    {
        return Executors.newSingleThreadExecutor()
    }

    @Provides
    @Singleton
    fun provideSharePreference(@ApplicationContext context: Context): SharedPreferences
    {
        return context.getSharedPreferences("MySharePreferences",Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideSharePreferenceEditor(sharedPreferences: SharedPreferences): SharedPreferences.Editor
    {
        return sharedPreferences.edit()
    }

    @Provides
    @Singleton
    fun provideDecimalFormat(): DecimalFormat
    {
        return  DecimalFormat ( "#,###" )
    }

    @Provides
    fun providePicasso(): Picasso
    {
        return Picasso.get()
    }

    @Provides
    @Singleton
    @Named("container id")
    fun provideContainerId(): Int
    {
        return R.id.navigation_host
    }

    @Provides
    fun provideFragment9Models(): ArrayList<F32Model>
    {
        return ArrayList()
    }

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context) = MyDataBase.getInstance(context)

    @Singleton
    @Provides
    fun provideDAO(db: MyDataBase) = db.myDAO

    @Provides
    fun provideIntent(): Intent
    {
        return Intent()
    }
}