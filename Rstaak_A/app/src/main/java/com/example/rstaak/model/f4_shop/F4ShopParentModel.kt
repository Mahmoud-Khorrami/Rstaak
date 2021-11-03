package com.example.rstaak.model.f4_shop

open class F4ShopParentModel(var currentType: Int)
{
    companion object
    {
        const val Main = 100
        const val Loading = 200
        const val Retry = 300
        const val No_More_Data = 400
    }
}