package com.global_water_polo.play_abroad.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import com.global_water_polo.play_abroad.data.repository.BaseRepositoryImpl
import com.global_water_polo.play_abroad.data.repository.DataStoreRepositoryImpl
import com.global_water_polo.play_abroad.data.repository.ProfileRepositoryImpl
import com.global_water_polo.play_abroad.domain.repository.BaseRepository
import com.global_water_polo.play_abroad.domain.repository.DataStoreRepository
import com.global_water_polo.play_abroad.domain.repository.ProfileRepository

/**
 Koin module for binding repository implementations to their interfaces.

 This module follows the Repository pattern and Dependency Inversion Principle,
 allowing for loose coupling between data sources and business logic.
**/

val repositoryModule = module {
    singleOf(::BaseRepositoryImpl) bind BaseRepository::class
    singleOf(::DataStoreRepositoryImpl) bind DataStoreRepository::class
    singleOf(::ProfileRepositoryImpl) bind ProfileRepository::class
}
