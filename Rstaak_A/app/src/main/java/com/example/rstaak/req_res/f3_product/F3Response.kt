package com.example.rstaak.req_res.f3_product

import com.google.gson.annotations.SerializedName

class F3Response(var message: F3Message, var status: Int, @SerializedName("found records") var foundRecord: Int)