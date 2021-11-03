package com.example.rstaak.req_res.f321_location

import com.google.gson.annotations.SerializedName
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class AbadiRequest @AssistedInject constructor(@SerializedName("dehestan.id") @Assisted var dehshahrId: String)
{
    @AssistedFactory
    interface AbadiRequestFactory
    {
        fun create(dehshahrId: String): AbadiRequest
    }
}