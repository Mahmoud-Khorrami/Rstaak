package com.example.rstaak.req_res.f52_edit_shop

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

data class F52Request @AssistedInject constructor(@Assisted var shopId: String)
{
    @AssistedFactory
    interface Factory
    {
        fun create(shopId: String): F52Request
    }
}
