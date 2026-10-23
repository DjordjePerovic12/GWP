package com.global_water_polo.play_abroad.fakes

import com.global_water_polo.play_abroad.core.utils.networking.NetworkError
import com.global_water_polo.play_abroad.core.utils.networking.Resource
import com.global_water_polo.play_abroad.domain.model.TestModel
import com.global_water_polo.play_abroad.domain.repository.BaseRepository

class FakeBaseRepository : BaseRepository {
    var dataResponse: Resource<TestModel, NetworkError, String?> = Resource.Success(TestModel(id = 1, name = "Test"))
    var errorResponse: Resource<Unit, NetworkError, String?> = Resource.Error(NetworkError.BAD_REQUEST, "Bad request")

    override suspend fun getEmptyErrorTest() = errorResponse
    override suspend fun getHTMLErrorTest() = errorResponse
    override suspend fun getCustomMessageErrorTest() = errorResponse
    override suspend fun getDataResponseTest() = dataResponse
}
