package com.example.rstaak.req_res.f3_product

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class RstaakLocation(var name: String,
                var type: String,
                var ostan: String,
                var shahrestan: String,
                var bakhsh: String,
                var dehestan: String): Parcelable