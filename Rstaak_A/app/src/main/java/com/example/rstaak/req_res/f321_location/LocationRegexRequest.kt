package com.example.rstaak.req_res.f321_location

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class LocationRegexRequest @AssistedInject constructor(@Assisted var all: String)
{
    @AssistedFactory
    interface Factory
    {
        fun create(all: String): LocationRegexRequest
    }
}