package com.example.parliamentmembersapp.api

data class MemberInfo(val personNumber: Int,
                      val seatNumber: Int,
                      val last: String,
                      val first: String,
                      val party: String,
                      val minister: Boolean,
                      val picture:String,
                      val twitter: String,
                      val bornYear: Int,
                      val constituency: String)