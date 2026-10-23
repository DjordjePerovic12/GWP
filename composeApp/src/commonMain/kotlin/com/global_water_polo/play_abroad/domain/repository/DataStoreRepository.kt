package com.global_water_polo.play_abroad.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {

    suspend fun saveAccessToken(token: String)

    suspend fun getAccessToken(): Flow<String>

    suspend fun saveRefreshToken(token: String)

    suspend fun getRefreshToken(): Flow<String>

    //TODO: Remove language related functions if not necessary

    suspend fun savePreferredLanguage(languageTag: String)

    suspend fun getPreferredLanguage(): Flow<String>

    suspend fun clearDatastore()
}
