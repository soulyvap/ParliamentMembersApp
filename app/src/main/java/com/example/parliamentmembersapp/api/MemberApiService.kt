package com.example.parliamentmembersapp.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://users.metropolia.fi/~peterh/"

private val moshi = Moshi.Builder() // create an instance of Moshi
    .add(KotlinJsonAdapterFactory())
    .build()
private val retrofit = Retrofit.Builder() // create an instance of Retrofit and pass an instance of MoshiConverter
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()
interface MemberApiService {
    @GET("mps.json")
    suspend fun getMemberRecords(): List<MemberInfo>
}
object MemberApi {
    val retrofitService : MemberApiService by lazy {
        retrofit.create(MemberApiService::class.java) }
}