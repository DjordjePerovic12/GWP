package com.global_water_polo.play_abroad.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.global_water_polo.play_abroad.domain.repository.DataStoreRepository

//TODO: Remove language related functions if not necessary

class DataStoreRepositoryImpl(
    private val datastore: DataStore<Preferences>
) : DataStoreRepository {

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("ACCESS_TOKEN")
        val REFRESH_TOKEN = stringPreferencesKey("REFRESH_TOKEN")
        val USER_PREFERRED_LANGUAGE = stringPreferencesKey("USER_PREFERRED_LANGUAGE")
    }

    override suspend fun saveAccessToken(token: String) {
        datastore.edit { preferences ->
            preferences[ACCESS_TOKEN] = token
        }
    }

    override suspend fun getAccessToken(): Flow<String> =
        datastore.data.map { preferences -> preferences[ACCESS_TOKEN] ?: "" }

    override suspend fun saveRefreshToken(token: String) {
        datastore.edit { preferences ->
            preferences[REFRESH_TOKEN] = token
        }
    }

    override suspend fun getRefreshToken(): Flow<String> =
        datastore.data.map { preferences -> preferences[REFRESH_TOKEN] ?: "" }

    override suspend fun savePreferredLanguage(languageTag: String) {
        datastore.edit { preferences ->
            preferences[USER_PREFERRED_LANGUAGE] = languageTag
        }
    }

    override suspend fun getPreferredLanguage(): Flow<String> =
        datastore.data.map { preferences -> preferences[USER_PREFERRED_LANGUAGE] ?: "" }

    override suspend fun clearDatastore() {
        datastore.edit {
            it.clear()
        }
    }
}
