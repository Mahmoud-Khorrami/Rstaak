package com.example.rstaak.model.f321_location

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class F321Model @AssistedInject constructor(
    @Assisted("ostanId") var ostanId: String,
    @Assisted("ostanName")  var ostanName: String,
    @Assisted("shahrestanId")  var shahrestanId: String? = null,
    @Assisted("shahrestanName")  var shahrestanName: String? = null,
    @Assisted("bakhshId")  var bakhshId: String? = null,
    @Assisted("bakhshName")  var bakhshName: String? = null,
    @Assisted("dehshahrId")  var dehshahrId: String? = null,
    @Assisted("dehshahrName")  var dehshahrName: String? = null,
    @Assisted("abadiId")  var abadiId: String? = null,
    @Assisted("abadiName")  var abadiName: String? = null,
    @Assisted("type")  var type: String): F321ParentModel(Main)
{
    @AssistedFactory
    interface Factory
    {
        fun create(@Assisted("ostanId") ostanId: String, @Assisted("ostanName") ostanName: String, @Assisted("shahrestanId") shahrestanId: String? = null, @Assisted("shahrestanName") shahrestanName: String? = null, @Assisted("bakhshId") bakhshId: String? = null, @Assisted("bakhshName") bakhshName: String? = null, @Assisted("dehshahrId") dehshahrId: String? = null, @Assisted("dehshahrName") dehshahrName: String? = null, @Assisted("abadiId") abadiId: String? = null, @Assisted("abadiName") abadiName: String? = null, @Assisted("type") type: String): F321Model
    }
}