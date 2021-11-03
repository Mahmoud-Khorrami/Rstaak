package com.example.rstaak.model.f321_location

open class F321ParentModel(var currentType: Int)
{
    companion object
    {
        const val Main = 100
        const val Loading = 200
        const val Retry = 300
        const val Not_Found = 400
    }
}