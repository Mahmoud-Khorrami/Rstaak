package com.example.rstaak.model

import com.google.gson.annotations.SerializedName

class F1RegisterModel(@SerializedName("phone number") var phoneNumber: String,
                        @SerializedName("register code") var registerCode: String,
                        @SerializedName("userId") var userId: String,
                        @SerializedName("error") var error: String)