package com.example.tictaktoe.application

import android.app.Application
import com.example.tictaktoe.data.TicTakToeRepository
import com.parse.Parse

class TicTakToeApplication: Application() {
    lateinit var repository: TicTakToeRepository

    override fun onCreate() {
        super.onCreate()

        Parse.initialize(Parse.Configuration.Builder(this)
            .applicationId("PoYtBb8PD7uEEaVjm35wAVtweCZIFEXqEuPVWlRI")
            .clientKey("PRmQ7jrzhZsAYgjD0R9HK4o5HyxMj5cmcyPIuL3r")
//            .server("https://parseapi.back4app.com")
            .server("https://tictaktoeandroid.b4a.io")
            .build())

        repository = TicTakToeRepository()
    }
}