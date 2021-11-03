package com.example.rstaak.req_res.f51_new_shop

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

data class F51Request @AssistedInject constructor(
    @Assisted("cellNumber") var cellNumber: String,
    @Assisted("shopId") var shopId: String? = null,
    @Assisted("title") var title: String,
    @Assisted("description") var description: String,
    @Assisted("address") var address: String,
    @Assisted("phoneNumber") var phoneNumber: String,
    @Assisted() var imageList: List<String>)
{
    @AssistedFactory
    interface Factory
    {
        fun create (
            @Assisted("cellNumber") cellNumber: String,
            @Assisted("shopId") shopId: String? = null,
            @Assisted("title") title: String,
            @Assisted("description") description: String,
            @Assisted("address") address: String,
            @Assisted("phoneNumber") phoneNumber: String,
            imageList: List<String>): F51Request
    }
}
