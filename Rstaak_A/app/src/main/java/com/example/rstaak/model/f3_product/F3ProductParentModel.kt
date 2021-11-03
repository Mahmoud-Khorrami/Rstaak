package com.example.rstaak.model.f3_product

open class F3ProductParentModel(var currentType: Int)
{
    companion object
    {
        const val Main = 100
        const val Loading = 200
        const val Retry = 300
        const val No_More_Data = 400
    }
}