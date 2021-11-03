package com.example.rstaak.model

import android.net.Uri
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

data class F32Model @AssistedInject constructor(@Assisted var id: Int, @Assisted var uri: Uri)
{
    @AssistedFactory
    interface Factory
    {
        fun create(id: Int, uri: Uri): F32Model
    }
}
