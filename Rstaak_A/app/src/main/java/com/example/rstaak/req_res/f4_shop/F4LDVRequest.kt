package com.example.rstaak.req_res.f4_shop

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

data class F4LDVRequest @AssistedInject constructor(@Assisted("shopId") var shopId: String, @Assisted("userId") var userId: String)
{
    @AssistedFactory
    interface Factory
    {
        fun create(@Assisted("shopId") shopId: String, @Assisted("userId") userId: String): F4LDVRequest
    }
}