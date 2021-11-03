package com.example.rstaak.req_res.f3_product

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class F3Request2  @AssistedInject constructor(
    @Assisted("number") var number: Int,
    @Assisted("factor")  var factor: Int,
    @Assisted("userId") var userId: String,
    @Assisted("shopId") var shopId: String,
    @Assisted("categoryId") var categoryId: String)
{
    @AssistedFactory
    interface Factory
    {
        fun create(@Assisted("number") number: Int,
            @Assisted("factor") factor: Int,
            @Assisted("userId") userId: String,
            @Assisted("shopId") shopId: String,
            @Assisted("categoryId") categoryId: String): F3Request2
    }
}