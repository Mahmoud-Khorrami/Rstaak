package com.example.rstaak.req_res.f321_location

import com.google.gson.annotations.SerializedName
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ShahrestanRequest @AssistedInject constructor(@SerializedName("ostan.id") @Assisted var ostanId: String)
{
    @AssistedFactory
    interface ShahrestanRequestFactory
    {
        fun create(ostanId: String): ShahrestanRequest
    }
}