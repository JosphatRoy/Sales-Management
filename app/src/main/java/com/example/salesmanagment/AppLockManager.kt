package com.example.salesmanagment

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

object AppLockManager : DefaultLifecycleObserver {

    private var isLocked = true
    private var isAppInBackground = false

    fun init(application: Application) {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                checkLock(activity)
            }
            override fun onActivityStarted(activity: Activity) {
                checkLock(activity)
            }
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        // App comes to foreground
        if (isAppInBackground) {
            isLocked = true
            isAppInBackground = false
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        // App goes to background
        isAppInBackground = true
    }

    private fun checkLock(activity: Activity) {
        if (isLocked && activity !is LockActivity && activity !is SplashActivity) {
            val intent = Intent(activity, LockActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivity(intent)
        }
    }

    fun unlock() {
        isLocked = false
    }
    
    fun isAppLocked(): Boolean = isLocked
}
