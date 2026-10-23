package com.global_water_polo.play_abroad.data.remote.services

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

/**
  All API calls of the app. Each function returns the raw HttpResponse;
  repositories wrap it in retrieveAPIResponse<Dto> { apiService.x() }.
  Paths are relative to BuildKonfig.BASE_URL (set in HttpClientFactory).
  The refresh token call lives in HttpClientFactory (Ktor Auth plugin).
 **/

class ApiService(
    private val httpClient: HttpClient
) {
    //Public test endpoints, delete together with the example feature.
    //httpbin - this returns 400 with empty errorBody (absolute URL overrides BASE_URL)
    suspend fun getEmptyErrorTest(): HttpResponse =
        httpClient.get("https://httpbin.org/status/400")

    //dummyjson - this returns 404 with html
    suspend fun getHTMLErrorTest(): HttpResponse =
        httpClient.get("nonexistent-page")

    //dummyjson - this returns 401 with custom message
    suspend fun getCustomMessageErrorTest(): HttpResponse =
        httpClient.get("auth/me")

    //dummyjson - this returns 200 with some mock data (id, name, ...)
    suspend fun getDataResponseTest(): HttpResponse =
        httpClient.get("recipes/1")
}
