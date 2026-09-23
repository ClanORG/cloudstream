package com.lagradost.cloudstream3.data.api.providers

import com.lagradost.cloudstream3.data.api.MainAPI
import com.lagradost.cloudstream3.data.api.SearchResponse
import com.lagradost.cloudstream3.data.api.LoadResponse

class DemoProvider : MainAPI() {
    override val name: String = "DemoProvider"
    override val mainUrl: String = "https://demo.com"

    override suspend fun search(query: String): List<SearchResponse> {
        return listOf(
            SearchResponse("Movie 1", "https://demo.com/m1", "https://via.placeholder.com/150", name),
            SearchResponse("Movie 2", "https://demo.com/m2", "https://via.placeholder.com/150", name)
        ).filter { it.name.contains(query, ignoreCase = true) }
    }

    override suspend fun load(url: String): LoadResponse {
        return LoadResponse(
            name = "Movie 1",
            url = url,
            posterUrl = "https://via.placeholder.com/300",
            apiName = name,
            plot = "This is a demo movie plot for testing."
        )
    }
}
