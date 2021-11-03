package com.example.rstaak.model.f5

import android.os.Parcelable
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.android.parcel.Parcelize

@Parcelize
data class F5Model @AssistedInject constructor(
    @Assisted("address") var address: String? = null,
    @Assisted("createdDatetime") var createdDatetime: String,
    @Assisted("description") var description: String? = null,
    @Assisted("imageList") var imageList: List<String>,
    @Assisted("likeFlag") var likeFlag: Boolean,
    @Assisted("likedNumber") var likedNumber: Int,
    @Assisted("modified_on") var modified_on: String? = null,
    @Assisted("owner") var owner: String,
    @Assisted("phoneNumber") var phoneNumber: String? = null,
    @Assisted("productsList") var productsList: List<String>,
    @Assisted("shopId") var shopId: String,
    @Assisted("shopLink") var shopLink: String,
    @Assisted("title") var title: String,
    @Assisted("viewedNumber") var viewedNumber: Int): F5ParentModel(Main), Parcelable
{
        @AssistedFactory
        interface Factory
        {
            fun create(
                @Assisted("address") address: String? = null,
                @Assisted("createdDatetime") createdDatetime: String,
                @Assisted("description") description: String? = null,
                @Assisted("imageList") imageList: List<String>,
                @Assisted("likeFlag") likeFlag: Boolean,
                @Assisted("likedNumber") likedNumber: Int,
                @Assisted("modified_on") modified_on: String? = null,
                @Assisted("owner") owner: String,
                @Assisted("phoneNumber") phoneNumber: String? = null,
                @Assisted("productsList") productsList: List<String>,
                @Assisted("shopId") shopId: String,
                @Assisted("shopLink") shopLink: String,
                @Assisted("title") title: String,
                @Assisted("viewedNumber") viewedNumber: Int): F5Model
        }
    }
