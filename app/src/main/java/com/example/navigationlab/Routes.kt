package com.example.navigationlab

object Routes {
    const val  HOME = "home"
    const val PROFILE = "profile"
    const val DETAIL  = "detail/{studentId}"
    const val ABOUT = "about"
    const val FAVORITES = "favorites"
    const val LOCATION = "location"

    fun detail(studentId: Int): String = "detail/$studentId"
}
