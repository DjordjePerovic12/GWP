package com.global_water_polo.play_abroad.data.remote

import co.touchlab.kermit.Logger as KermitLogger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import com.global_water_polo.play_abroad.BuildKonfig
import com.global_water_polo.play_abroad.core.utils.Constants.REFRESH_TOKEN_ROUTE
import com.global_water_polo.play_abroad.core.utils.networking.networkJson
import com.global_water_polo.play_abroad.data.remote.dto.AuthenticationResponseDto
import com.global_water_polo.play_abroad.domain.repository.DataStoreRepository

/**
  Builds the app's single HttpClient. This replaces OkHttpClient + Retrofit,
  AuthorizationInterceptor and TokenRefreshAuthenticator from the Android base:

  - defaultRequest: BASE_URL from BuildKonfig (gradle.properties / local.properties) and JSON headers
  - Auth bearer: signs requests to our backend with the access token from DataStore and,
    on a 401, refreshes it once (Ktor serialises concurrent refreshes, like the Mutex in the old authenticator).
    A failed refresh clears the session.
  - Logging: request/response bodies through Kermit

  After login/logout call httpClient.authProvider<BearerAuthProvider>()?.clearToken()
  so the cached tokens are reloaded from DataStore.
 **/

object HttpClientFactory {

    fun create(
        engine: HttpClientEngine,
        dataStoreRepository: DataStoreRepository
    ): HttpClient = HttpClient(engine) {
        expectSuccess = false

        install(ContentNegotiation) {
            json(networkJson)
        }

        install(HttpTimeout) {
            connectTimeoutMillis = 30_000L
            requestTimeoutMillis = 30_000L
            socketTimeoutMillis = 30_000L
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    KermitLogger.d(tag = "HttpClient") { message }
                }
            }
            level = LogLevel.BODY
        }

        defaultRequest {
            url(BuildKonfig.BASE_URL)
            contentType(ContentType.Application.Json)
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val accessToken = dataStoreRepository.getAccessToken().first()
                    val refreshToken = dataStoreRepository.getRefreshToken().first()
                    if (accessToken.isBlank()) null else BearerTokens(accessToken, refreshToken)
                }

                // TODO: "This is a default config so that base doesn't crash at compile time. Change accordingly to respective app."
                refreshTokens {
                    val refreshToken = oldTokens?.refreshToken.orEmpty()
                    if (refreshToken.isBlank()) {
                        dataStoreRepository.clearDatastore()
                        return@refreshTokens null
                    }

                    val response = try {
                        client.post(REFRESH_TOKEN_ROUTE) {
                            markAsRefreshTokenRequest()
                            setBody(AuthenticationResponseDto(refreshToken = refreshToken))
                        }
                    } catch (e: Exception) {
                        KermitLogger.e(e) { "Failed to refresh token" }
                        null
                    }

                    val tokens = response
                        ?.takeIf { it.status.isSuccess() }
                        ?.body<AuthenticationResponseDto>()
                    val newAccessToken = tokens?.accessToken
                    val newRefreshToken = tokens?.refreshToken

                    if (newAccessToken != null && newRefreshToken != null) {
                        dataStoreRepository.saveAccessToken(newAccessToken)
                        dataStoreRepository.saveRefreshToken(newRefreshToken)
                        BearerTokens(newAccessToken, newRefreshToken)
                    } else {
                        // Failed or already logged out
                        dataStoreRepository.clearDatastore()
                        null
                    }
                }

                // Only our backend gets the token, never third-party URLs
                sendWithoutRequest { request ->
                    request.url.host == Url(BuildKonfig.BASE_URL).host
                }
            }
        }
    }
}
