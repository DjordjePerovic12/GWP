package com.global_water_polo.play_abroad.data.mapper

import com.global_water_polo.play_abroad.data.remote.dto.TestResponseDto
import com.global_water_polo.play_abroad.domain.model.TestModel

/**
Example mapper created to test networking
 **/

fun TestResponseDto.toTestModel(): TestModel {
    return TestModel(
        id = this.id ?: -1,
        name = this.name ?: ""
    )
}
