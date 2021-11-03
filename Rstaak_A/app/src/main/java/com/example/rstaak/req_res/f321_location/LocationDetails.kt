package com.example.rstaak.req_res.f321_location

import android.os.Parcelable
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.android.parcel.Parcelize

@Parcelize
class LocationDetails @AssistedInject constructor(@Assisted("name") var name: String,@Assisted("id") var id: String,@Assisted("type") var type: String): Parcelable
{
    @AssistedFactory
    interface Factory
    {
        fun create(@Assisted("name") name: String,@Assisted("id") id: String,@Assisted("type") type: String):LocationDetails
    }
}