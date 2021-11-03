package com.example.rstaak.req_res.f3_product

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class F3Request1  @AssistedInject constructor(
    @Assisted("number") var number: Int,
    @Assisted("factor")  var factor: Int,
    @Assisted var userId: String)
{
    @AssistedFactory
    interface ProductRequestFactory
    {
        fun create(@Assisted("number") number: Int, @Assisted("factor") factor: Int, userId: String): F3Request1
    }
}