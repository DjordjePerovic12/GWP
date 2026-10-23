package com.global_water_polo.play_abroad.domain.repository

import com.global_water_polo.play_abroad.core.utils.networking.NetworkError
import com.global_water_polo.play_abroad.core.utils.networking.Resource
import com.global_water_polo.play_abroad.domain.model.TestModel

//TODO: When creating a new project rename BaseRepository to <AppName>Repository

interface BaseRepository {
    suspend fun getEmptyErrorTest(): Resource<Unit, NetworkError, String?>
    suspend fun getHTMLErrorTest(): Resource<Unit, NetworkError, String?>
    suspend fun getCustomMessageErrorTest(): Resource<Unit, NetworkError, String?>
    suspend fun getDataResponseTest(): Resource<TestModel, NetworkError, String?>
}
