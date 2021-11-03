package com.example.rstaak.model.f3_product

import android.os.Parcelable
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.android.parcel.Parcelize

@Parcelize
data class F3ChatStatus @AssistedInject constructor(
    @Assisted("chatId") var chatId: String?,
    @Assisted("productId") var productId: String,
    @Assisted("ownerId") var ownerId: String,
    @Assisted("productTitle") var productTitle: String,
    @Assisted("productImage") var productImage: String): Parcelable
{
    @AssistedFactory
    interface Factory
    {
        fun create(
            @Assisted("chatId") chatId: String?,
            @Assisted("productId") productId: String,
            @Assisted("ownerId") ownerId: String,
            @Assisted("productTitle") productTitle: String,
            @Assisted("productImage") productImage: String): F3ChatStatus
    }
}
