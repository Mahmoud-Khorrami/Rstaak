package com.example.rstaak.model.f5

open class F5ParentModel(var currentType: Int)
{
    companion object
    {
        const val Main = 100
        const val Loading = 200
        const val Retry = 300
    }
}