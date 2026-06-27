package com.example.salesmanagment

import com.google.gson.annotations.SerializedName

// M-Pesa Models
data class MpesaTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val expiresIn: String
)

data class STKPushRequest(
    @SerializedName("BusinessShortCode") val businessShortCode: String,
    @SerializedName("Password") val password: String,
    @SerializedName("Timestamp") val timestamp: String,
    @SerializedName("TransactionType") val transactionType: String = "CustomerPayBillOnline",
    @SerializedName("Amount") val amount: String,
    @SerializedName("PartyA") val partyA: String,
    @SerializedName("PartyB") val businessShortCodeB: String,
    @SerializedName("PhoneNumber") val phoneNumber: String,
    @SerializedName("CallBackURL") val callBackUrl: String,
    @SerializedName("AccountReference") val accountReference: String,
    @SerializedName("TransactionDesc") val transactionDesc: String
)

data class STKPushResponse(
    @SerializedName("MerchantRequestID") val merchantRequestId: String,
    @SerializedName("CheckoutRequestID") val checkoutRequestId: String,
    @SerializedName("ResponseCode") val responseCode: String,
    @SerializedName("ResponseDescription") val responseDescription: String,
    @SerializedName("CustomerMessage") val customerMessage: String
)

// PayPal Models
data class PayPalOrderRequest(
    val intent: String = "CAPTURE",
    val purchaseUnits: List<PayPalPurchaseUnit>
)

data class PayPalPurchaseUnit(
    val amount: PayPalAmount
)

data class PayPalAmount(
    val currencyCode: String = "USD",
    val value: String
)
