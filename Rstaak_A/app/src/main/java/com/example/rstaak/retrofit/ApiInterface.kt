package com.example.rstaak.retrofit

import com.example.rstaak.req_res.f1_register.F1Request
import com.example.rstaak.req_res.f1_register.F1Response
import com.example.rstaak.req_res.f3_product.*
import com.example.rstaak.req_res.f32.F32Response
import com.example.rstaak.req_res.f323_category_select.F323Response
import com.example.rstaak.req_res.f32.F32Request
import com.example.rstaak.req_res.f321_location.*
import com.example.rstaak.req_res.f322_shop_select.F322Request
import com.example.rstaak.req_res.f322_shop_select.F322Response
import com.example.rstaak.req_res.f4_shop.F4LDVRequest
import com.example.rstaak.req_res.f4_shop.F4LDVResponse
import com.example.rstaak.req_res.f4_shop.F4Request
import com.example.rstaak.req_res.f4_shop.F4Response
import com.example.rstaak.req_res.f41_shop_details.F41Request
import com.example.rstaak.req_res.f41_shop_details.F41Response
import com.example.rstaak.req_res.f51_new_shop.F51Request
import com.example.rstaak.req_res.f51_new_shop.F51Response
import com.example.rstaak.req_res.f52_edit_shop.F52Request
import com.example.rstaak.req_res.f5_my_shop.F5Request
import io.reactivex.Observable
import retrofit2.http.*

interface ApiInterface {

    @POST("v1.0/initiation/post")
    fun registerUser(@Body f1Request: F1Request): Observable<F1Response>

    @POST("v1.0/phoneNumber-approvement/post")
    fun approvePhoneNumber(@Body f1Request: F1Request): Observable<F1Response>

    @POST("v1.0/product-query/get")
    fun getProducts1(@Body f3Request1: F3Request1): Observable<F3Response>

    @POST("v1.0/product-query/get")
    fun getProducts2(@Body f3Request2: F3Request2): Observable<F3Response>

    @PUT("v1.0/like-product/put")
    fun likeProduct(@Body F3LDVRequest: F3LDVRequest):  Observable<F3LDVResponse>

    @PUT("v1.0/dislike-product/put")
    fun dislikeProduct(@Body F3LDVRequest: F3LDVRequest):  Observable<F3LDVResponse>

    @PUT("v1.0/view-product/put")
    fun viewProduct(@Body F3LDVRequest: F3LDVRequest):  Observable<F3LDVResponse>

    @POST("v1.0/ostan-query/get")
    fun getOstan(): Observable<LocationResponse>

    @POST("v1.0/shahrestan-query/get")
    fun getShahrestan(@Body locationRequest: ShahrestanRequest): Observable<LocationResponse>

    @POST("v1.0/bakhsh-query/get")
    fun getBakhsh(@Body bakhshRequest: BakhshRequest): Observable<LocationResponse>

    @POST("v1.0/dehshahr-query/get")
    fun getDehshahr(@Body dehshahrRequest: DehshahrRequest): Observable<LocationResponse>

    @POST("v1.0/abadi-query/get")
    fun getAbadi(@Body abadiRequest: AbadiRequest): Observable<LocationResponse>

    @POST("v1.0/locationRegex-query/get")
    fun locationRegex(@Body locationRegexRequest: LocationRegexRequest): Observable<LocationResponse>

    @POST("v1.0/shop-query/get")
    fun getShop(@Body f322Request: F322Request): Observable<F322Response>

    @POST("v1.0/category-query/get")
    fun getCategory(): Observable<F323Response>

    @POST("v1.0/new-product/post")
    fun createNewProduct(@Body f32Request: F32Request): Observable<F32Response>

    @POST("v1.0/shop-query/get")
    fun getShops1(@Body f4Request: F4Request): Observable<F4Response>

    @POST("v1.0/shop-query/get")
    fun getShops2(@Body f5Request: F5Request): Observable<F4Response>

    @POST("v1.0/shop-query/get")
    fun getShops3(@Body f52Request: F52Request): Observable<F4Response>

    @PUT("v1.0/like-shop/put")
    fun likeShop(@Body f4LDVRequest: F4LDVRequest):  Observable<F4LDVResponse>

    @PUT("v1.0/dislike-shop/put")
    fun dislikeShop(@Body f4LDVRequest: F4LDVRequest):  Observable<F4LDVResponse>

    @PUT("v1.0/view-shop/put")
    fun viewShop(@Body f4LDVRequest: F4LDVRequest):  Observable<F4LDVResponse>

    @POST("v1.0/specialShop-query/get")
    fun getShopsSpecial(@Body f41Request: F41Request): Observable<F41Response>

    @POST("v1.0/new-shop/post")
    fun newShop(@Body f51Request: F51Request): Observable<F51Response>

    @PUT("v1.0/edit-shop/put")
    fun editShop(@Body f51Request: F51Request): Observable<F51Response>
}