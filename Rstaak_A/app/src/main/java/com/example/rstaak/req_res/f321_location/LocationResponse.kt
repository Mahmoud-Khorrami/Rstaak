package com.example.rstaak.req_res.f321_location

import com.google.gson.annotations.SerializedName

class LocationResponse(@SerializedName("found records") var foundRecords: Int, var message: LocationMessage, var status: Int)