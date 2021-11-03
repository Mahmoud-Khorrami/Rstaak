package com.example.rstaak.req_res.f1_register

import com.example.rstaak.model.F1RegisterModel
import com.google.gson.annotations.SerializedName

class F1Response(@SerializedName("message") var registerModel: F1RegisterModel,
                        @SerializedName("status") var status: Int)