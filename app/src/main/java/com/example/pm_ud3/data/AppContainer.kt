package com.example.pm_ud3.data

import android.content.Context
import com.example.pm_ud3.data.local.AppDatabase
import com.example.pm_ud3.network.MockApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    private val database: AppDatabase = AppDatabase.getDatabase(context)

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://69415249686bc3ca81668c95.mockapi.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: MockApiService = retrofit.create(MockApiService::class.java)

    val userRepository : UserRepository = DefaultUserRepository(
        local = database.userDao(),
        remote = apiService
    )

}