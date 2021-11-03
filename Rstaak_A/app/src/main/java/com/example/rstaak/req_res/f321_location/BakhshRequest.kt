package com.example.rstaak.req_res.f321_location

import com.google.gson.annotations.SerializedName
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class BakhshRequest @AssistedInject constructor(@SerializedName("shahrestan.id") @Assisted var shahrestanId: String)
{
    @AssistedFactory
    interface BakhshRequestFactory
    {
        fun create(shahrestanId: String): BakhshRequest
    }
}