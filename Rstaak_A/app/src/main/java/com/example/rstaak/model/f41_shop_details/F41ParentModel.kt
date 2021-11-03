package com.example.rstaak.model.f41_shop_details

open class F41ParentModel(var currentType: Int)
{
    companion object
    {
        const val Main = 100
        const val Loading = 200
        const val Retry = 300
        const val ShowCase = 400
    }
}