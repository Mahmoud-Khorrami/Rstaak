package com.example.rstaak.req_res.f322_shop_select

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class F322Request @AssistedInject constructor(@Assisted var ownerId: String)
{
    @AssistedFactory
    interface Factory
    {
        fun create(ownerId: String): F322Request
    }
}