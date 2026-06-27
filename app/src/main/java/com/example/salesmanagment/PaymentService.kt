package com.example.salesmanagment

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface MpesaService {
    @GET("oauth/v1/generate?grant_type=client_credentials")
    fun getAccessToken(@Header("Authorization") auth: String): Call<MpesaTokenResponse>

    @POST("mpesa/stkpush/v1/query")
    fun initiateSTKPush(
        @Header("Authorization") auth: String,
        @Body request: STKPushRequest
    ): Call<STKPushResponse>
}

interface PayPalService {
    @POST("v2/checkout/orders")
    fun createOrder(
        @Header("Authorization") auth: String,
        @Body request: PayPalOrderRequest
    ): Call<Any>
}
