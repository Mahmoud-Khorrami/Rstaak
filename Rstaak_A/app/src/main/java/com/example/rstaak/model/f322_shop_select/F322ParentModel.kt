package com.example.rstaak.model.f322_shop_select

open class F322ParentModel(var currentType: Int)
{
    companion object
    {
        const val Main = 100
        const val Loading = 200
        const val Retry = 300
    }
}