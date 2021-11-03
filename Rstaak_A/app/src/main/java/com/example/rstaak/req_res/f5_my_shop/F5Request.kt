package com.example.rstaak.req_res.f5_my_shop

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

data class F5Request @AssistedInject constructor(@Assisted var ownerId: String)
{
    @AssistedFactory
    interface Factory
    {
        fun create(ownerId: String): F5Request
    }
}
