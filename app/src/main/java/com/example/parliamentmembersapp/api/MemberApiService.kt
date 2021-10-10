package com.example.parliamentmembersapp.api

import com.example.parliamentmembersapp.database.Member
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

/*
* Date: 22.8.2021
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: Api service for fetching information about MPs as a list of Member objects from the
* a JSON file
*/

private const val BASE_URL = "https://users.metropolia.fi/~peterh/"

//create an instance of Moshi
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

//create an instance of Retrofit and pass an instance of MoshiConverter
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

//setting up getter and data fetching function
interface MemberApiService {
    @GET("mps.json")
    suspend fun getMemberRecords(): List<Member>
}

//creating retrofit service
object MemberApi {
    val retrofitService : MemberApiService by lazy {
        retrofit.create(MemberApiService::class.java) }
}