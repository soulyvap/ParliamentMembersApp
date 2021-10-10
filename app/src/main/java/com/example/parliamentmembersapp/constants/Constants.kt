package com.example.parliamentmembersapp.constants

/*
* Date: 21.8.2021
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: Object containing various constants needed in the app
*/

object Constants {
    const val KEY_SUBGROUP = "subgroup"
    const val VAL_PARTIES = "parties"
    const val VAL_CONSTITUENCIES = "constituencies"
    const val KEY_PERSON_NUM = "person_num"
    const val KEY_SUBGROUP_TYPE = "subgroup_type"
    const val SP_USERNAME = "userPref"
    const val SP_KEY_USERNAME = "username"
    const val ORDER_FIRST = "First name"
    const val ORDER_LAST = "Last name"
    const val ORDER_AGE = "Age"
    const val ORDER_PARTY = "Party"
    const val ORDER_CONSTITUENCY = "Constituency"
    const val ORDER_POSITION = "Position"
    private const val ORDER_SEAT = "Seat number"
    val ORDER_LIST = listOf(ORDER_FIRST, ORDER_LAST, ORDER_AGE,
        ORDER_PARTY, ORDER_CONSTITUENCY, ORDER_POSITION, ORDER_SEAT)
}