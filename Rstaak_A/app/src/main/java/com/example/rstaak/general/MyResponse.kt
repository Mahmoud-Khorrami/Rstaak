package com.example.rstaak.general

import retrofit2.Response
import java.lang.Exception

abstract class MyResponse
{
    protected suspend fun <T> getResult(call: suspend() -> Response<T>): MyResult<T>
    {
        try
        {
            val response = call()

            if(response.isSuccessful)
            {
                val body = response.body()
                if(body != null)
                    return MyResult.success(body)
            }

            return error("${response.code()} ${response.message()}")

        }
        catch (e: Exception)
        {
            return error(e.message?: e.toString())
        }
    }

    private fun <T> error(message: String): MyResult<T>
    {
        return MyResult.error("Network Call Has Failed: $message", null)
    }
}