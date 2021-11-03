package com.example.rstaak.general

data class MyResult<out T>(val status: Status, val data: T?, val error: Error?, val message: String?) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T?): MyResult<T> {
            return MyResult(Status.SUCCESS, data, null, null)
        }

        fun <T> error(message: String, error: Error?): MyResult<T> {
            return MyResult(Status.ERROR, null, error, message)
        }

        fun <T> loading(data: T? = null): MyResult<T> {
            return MyResult(Status.LOADING, data, null, null)
        }
    }

    override fun toString(): String {
        return "Result(status=$status, data=$data, error=$error, message=$message)"
    }
}