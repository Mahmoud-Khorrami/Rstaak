package com.example.rstaak.req_res.f3_product

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class F3LDVRequest @AssistedInject constructor(@Assisted("productId") var productId: String, @Assisted("userId") var userId: String)
{
    @AssistedFactory
    interface ProductLDVRequestFactory
    {
        fun create(@Assisted("productId") productId: String, @Assisted("userId") userId: String): F3LDVRequest
    }
}