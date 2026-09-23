package com.lagradost.cloudstream3.ui

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    object Home : Screen()
    
    @Serializable
    object Search : Screen()
    
    @Serializable
    object Library : Screen()
    
    @Serializable
    object Downloads : Screen()
    
    @Serializable
    object Settings : Screen()
    
    @Serializable
    data class Result(val url: String, val apiName: String) : Screen()
    
    @Serializable
    data class Player(val url: String, val title: String) : Screen()
}
