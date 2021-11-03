package com.example.rstaak.req_res.f322_shop_select

import com.google.gson.annotations.SerializedName

class F322Response(var message: F322Message, var status: Int, @SerializedName("found records") var foundRecord: Int)