package com.global_water_polo.play_abroad.data.repository

import com.global_water_polo.play_abroad.core.utils.networking.NetworkError
import com.global_water_polo.play_abroad.core.utils.networking.Resource
import com.global_water_polo.play_abroad.core.utils.networking.map
import com.global_water_polo.play_abroad.core.utils.networking.retrieveAPIResponse
import com.global_water_polo.play_abroad.data.mapper.toTestModel
import com.global_water_polo.play_abroad.data.remote.dto.TestResponseDto
import com.global_water_polo.play_abroad.data.remote.services.ApiService
import com.global_water_polo.play_abroad.domain.model.TestModel
import com.global_water_polo.play_abroad.domain.repository.BaseRepository

//TODO: When making a new project rename BaseRepositoryImpl to <AppName>RepositoryImpl

class BaseRepositoryImpl(
    private val apiService: ApiService
) : BaseRepository {
    override suspend fun getEmptyErrorTest(): Resource<Unit, NetworkError, String?> {
        return retrieveAPIResponse {
            apiService.getEmptyErrorTest()
        }
    }

    override suspend fun getHTMLErrorTest(): Resource<Unit, NetworkError, String?> {
        return retrieveAPIResponse {
            apiService.getHTMLErrorTest()
        }
    }

    override suspend fun getCustomMessageErrorTest(): Resource<Unit, NetworkError, String?> {
        return retrieveAPIResponse {
            apiService.getCustomMessageErrorTest()
        }
    }

    /**
    When whe have an API that returns data, the retrieveAPIResponse fun
    needs to be called with <ResponseDto>
     **/
    override suspend fun getDataResponseTest(): Resource<TestModel, NetworkError, String?> {
        return retrieveAPIResponse<TestResponseDto> {
            apiService.getDataResponseTest()
        }.map { response ->
            response.toTestModel()
        }
    }
}
