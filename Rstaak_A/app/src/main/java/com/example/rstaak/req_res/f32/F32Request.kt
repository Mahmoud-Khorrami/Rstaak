package com.example.rstaak.req_res.f32

import android.os.Parcelable
import com.example.rstaak.req_res.f321_location.LocationDetails
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.android.parcel.Parcelize

@Parcelize
data class F32Request @AssistedInject constructor(
    @Assisted("cellNumber") var cellNumber: String,
    @Assisted("title") var title: String,
    @Assisted("shopId") var shopId: String,
    @Assisted("categoryId") var categoryId: String,
    @Assisted("price") var price: Int,
    @Assisted("description") var description: String,
    @Assisted var ifUsed: Boolean,
    @Assisted("rostaakLocation") var rostaakLocation: LocationDetails,
    @Assisted var imageList: List<String>): Parcelable
{
    @AssistedFactory
    interface Factory
    {
        fun create(
            @Assisted("cellNumber") cellNumber: String,
            @Assisted("title") title: String,
            @Assisted("shopId") shopId: String,
            @Assisted("categoryId") categoryId: String,
            @Assisted("price") price: Int,
            @Assisted("description") description: String,
            ifUsed: Boolean,
            @Assisted("rostaakLocation") rostaakLocation: LocationDetails,
            imageList: List<String>
        ): F32Request
    }
}