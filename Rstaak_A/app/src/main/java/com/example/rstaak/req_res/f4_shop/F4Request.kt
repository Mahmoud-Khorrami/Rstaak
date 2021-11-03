package com.example.rstaak.req_res.f4_shop

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class F4Request  @AssistedInject constructor(
    @Assisted("number") var number: Int,
    @Assisted("factor")  var factor: Int,
    @Assisted var userId: String)
{
    @AssistedFactory
    interface Factory
    {
        fun create(@Assisted("number") number: Int, @Assisted("factor") factor: Int, userId: String): F4Request
    }
}