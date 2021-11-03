package com.example.rstaak.req_res.f41_shop_details

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

data class F41Request @AssistedInject constructor(@Assisted var shopId: String)
{
    @AssistedFactory
    interface Factory
    {
        fun create(shopId: String):F41Request
    }
}