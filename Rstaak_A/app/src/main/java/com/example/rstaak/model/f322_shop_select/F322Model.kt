package com.example.rstaak.model.f322_shop_select

import android.os.Parcelable
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.android.parcel.Parcelize

@Parcelize
class F322Model @AssistedInject constructor(
    @Assisted("id") var id: String,
    @Assisted("title") var title: String,
    @Assisted("image") var image: String): F322ParentModel(Main), Parcelable
{
    @AssistedFactory
    interface Factory
    {
        fun create(
            @Assisted("id") id: String,
            @Assisted("title") title: String,
            @Assisted("image") image: String): F322Model
    }
}