package com.example.rstaak.req_res.f321_location

import com.google.gson.annotations.SerializedName
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class DehshahrRequest @AssistedInject constructor(@SerializedName("bakhsh.id") @Assisted var bakhshId: String)
{
    @AssistedFactory
    interface DehshahrRequestFactory
    {
        fun create(bakhshId: String): DehshahrRequest
    }
}