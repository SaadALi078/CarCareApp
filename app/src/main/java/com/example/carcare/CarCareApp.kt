package com.example.carcare
import android.app.Application
import com.google.firebase.FirebaseApp

class CarCareApp:Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

    }

}