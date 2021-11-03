package com.example.rstaak.req_res.f4_shop

import com.google.gson.annotations.SerializedName

class F4Response(var message: F4Message, var status: Int, @SerializedName("found records") var foundRecord: Int)