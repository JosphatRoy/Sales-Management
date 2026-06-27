package com.example.salesmanagment

/**
 * Configuration for Payment Gateways.
 * 
 * IMPORTANT: To go live, you must:
 * 1. Register an account on the Safaricom Developer Portal (https://developer.safaricom.co.ke/)
 * 2. Create a 'Lipa na M-Pesa' app to get your Consumer Key and Consumer Secret.
 * 3. Change [MPESA_BASE_URL] to production URL: "https://api.safaricom.co.ke/"
 */
object PaymentConfig {
    
    // M-Pesa Daraja API Settings
    // Replace these with your actual credentials from Daraja Portal
    const val MPESA_CONSUMER_KEY = "PASTE_YOUR_CONSUMER_KEY_HERE"
    const val MPESA_CONSUMER_SECRET = "PASTE_YOUR_CONSUMER_SECRET_HERE"
    
    // For Sandbox testing, use 174379. For production, use your Till/Paybill number.
    const val MPESA_BUSINESS_SHORT_CODE = "174379" 
    
    // Get this from the 'Test Credentials' tab in Sandbox or via M-Pesa business portal for Production
    const val MPESA_PASSKEY = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919"
    
    // SANDBOX: "https://sandbox.safaricom.co.ke/"
    // PRODUCTION: "https://api.safaricom.co.ke/"
    const val MPESA_BASE_URL = "https://sandbox.safaricom.co.ke/"
    
    // This must be a publicly accessible URL (HTTPS) to receive payment results
    const val MPESA_CALLBACK_URL = "https://your-server-api.com/mpesa/callback"

    // PayPal API Settings
    const val PAYPAL_CLIENT_ID = "YOUR_PAYPAL_CLIENT_ID"
    const val PAYPAL_SECRET = "YOUR_PAYPAL_SECRET"
    const val PAYPAL_BASE_URL = "https://api-m.sandbox.paypal.com/"

    // Mastercard (Gateway Integration)
    const val MASTERCARD_API_KEY = "YOUR_GATEWAY_API_KEY"
}
