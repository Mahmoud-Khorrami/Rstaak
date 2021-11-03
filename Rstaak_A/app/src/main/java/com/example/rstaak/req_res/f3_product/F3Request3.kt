package com.example.rstaak.req_res.f3_product

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

data class F3Request3 @AssistedInject constructor(@Assisted var users: List<String>, @Assisted var productId: String)
{
    @AssistedFactory
    interface Factory
    {
        fun create(users: List<String>, productId: String): F3Request3
    }
}
