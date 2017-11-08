package com.packt.splittestdemo

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class MainActivity : AppCompatActivity(), OnCompleteListener<Void> {

    val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    var firebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById(R.id.sign_up_button).setOnClickListener { onSignup() }

        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        firebaseRemoteConfig.setConfigSettings(configSettings)
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults)

        val token = FirebaseInstanceId.getInstance().getToken()
        Log.i(javaClass.simpleName, "token = ${token}")

        val cacheExpiration = 1L
        Log.i(javaClass.simpleName,"fetch")
        firebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(this)
    }

    override fun onComplete(task: Task<Void>) {
        if (task.isSuccessful){
            Log.i(javaClass.simpleName, "complete success")
            firebaseRemoteConfig.activateFetched()
        }
        else{
            Log.i(javaClass.simpleName, "complete no success")
        }
        applyRemoteConfiguration()
    }

    private fun applyRemoteConfiguration(){

        val variant = firebaseRemoteConfig.getString("experiment_variant")
        Log.i(javaClass.simpleName, "experiment = ${variant}")
        firebaseAnalytics?.setUserProperty("Experiment", variant)

        val onboardingColor = firebaseRemoteConfig.getString("onboarding_color")
        Log.i(javaClass.simpleName, "onboarding color= ${onboardingColor}")

        if (onboardingColor=="blue") {
            findViewById(R.id.sign_up_button).setBackgroundColor(Color.parseColor("#0000ff"))
        }
        else{
            findViewById(R.id.sign_up_button).setBackgroundColor(Color.parseColor("#00ff00"))
        }

        val onboardingText = firebaseRemoteConfig.getString("onboarding_text")
        Log.i(javaClass.simpleName, "onboarding text= ${onboardingText}")
        (findViewById(R.id.sign_up_text) as TextView).text = onboardingText

        val onboardingBackground = firebaseRemoteConfig.getString("onboarding_background")
        Log.i(javaClass.simpleName, "onboarding bg= ${onboardingBackground}")

        if (onboardingBackground=="strawberry") {
            (findViewById(R.id.image).setBackgroundResource(R.drawable.strawberry))
        }
        else{
            (findViewById(R.id.image).setBackgroundResource(R.drawable.oranges))
        }
    }

    private fun onSignup(){
        logEvent("signUp")
        Log.i(javaClass.simpleName, "sign up button clicked")
    }

    private fun logEvent(eventName: String){
        firebaseAnalytics?.logEvent(eventName, Bundle())
    }
}
