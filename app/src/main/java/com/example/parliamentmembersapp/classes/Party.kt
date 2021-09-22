package com.example.parliamentmembersapp.classes

import com.example.parliamentmembersapp.R

data class Party (val name: String, val codeName: String, val logoId: Int)

object Parties {
    val list = listOf(
        Party("Suomen Kristillisdemokraatit", "kd", R.drawable.kd),
        Party("Suomen Keskusta", "kesk", R.drawable.kesk),
        Party("Kansallinen Kokoomus", "kok", R.drawable.kok),
        Party("Liike Nyt", "liik", R.drawable.liik),
        Party("Perussuomalaiset", "ps", R.drawable.ps),
        Party("Ruotsalainen kansanpuolue", "r", R.drawable.r),
        Party("Suomen Sosialidemokraattinen puolue", "sd", R.drawable.sd),
        Party("Vasemmistoliitto", "vas", R.drawable.vas),
        Party("Vihreä liitto", "vihr", R.drawable.vihr)
    )
}
