package com.lagradost.cloudstream3.data.api

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val name: String,
    val url: String,
    val posterUrl: String?,
    val apiName: String,
    val type: String = "movie"
)

@Serializable
data class LoadResponse(
    val name: String,
    val url: String,
    val posterUrl: String?,
    val apiName: String,
    val plot: String?,
    val episodes: List<Episode> = emptyList()
)

@Serializable
data class Episode(
    val name: String?,
    val url: String,
    val episode: Int? = null,
    val season: Int? = null
)

abstract class MainAPI {
    abstract val name: String
    abstract val mainUrl: String
    
    open suspend fun search(query: String): List<SearchResponse> = emptyList()
    open suspend fun load(url: String): LoadResponse? = null
    open suspend fun loadLinks(data: String): List<String> = emptyList()
}
