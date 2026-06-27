package com.example.salesmanagment

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings

class SalesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Enable Firestore Offline Persistence explicitly
        // This ensures the app works on WiFi, Data, and Offline.
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(PersistentCacheSettings.newBuilder()
                .build()) // Default is enabled, but we can configure cache size here if needed
            .build()
        
        FirebaseFirestore.getInstance().firestoreSettings = settings

        // Initialize App Lock Manager
        AppLockManager.init(this)
    }
}
