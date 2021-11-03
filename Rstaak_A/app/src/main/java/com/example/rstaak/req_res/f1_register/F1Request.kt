package com.example.rstaak.req_res.f1_register

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class F1Request @AssistedInject constructor(@Assisted var cellNumber: String)
{
    @AssistedFactory
    interface Factory
    {
        fun create(cellNumber: String): F1Request
    }
}