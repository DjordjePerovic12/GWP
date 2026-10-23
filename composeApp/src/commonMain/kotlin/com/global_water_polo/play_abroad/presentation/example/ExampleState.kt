package com.global_water_polo.play_abroad.presentation.example

import com.global_water_polo.play_abroad.domain.model.TestModel
import com.global_water_polo.play_abroad.presentation.base.BaseError
import com.global_water_polo.play_abroad.presentation.base.BaseViewState

data class ExampleState(
    override val isLoading: Boolean = false,
    override val error: BaseError? = null,
    val testData: TestModel? = null
) : BaseViewState(isLoading, error)
