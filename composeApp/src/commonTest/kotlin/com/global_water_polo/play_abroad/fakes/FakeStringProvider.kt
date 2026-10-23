package com.global_water_polo.play_abroad.fakes

import org.jetbrains.compose.resources.StringResource
import com.global_water_polo.play_abroad.core.utils.StringProvider

/**
  Validation logic reads its messages through StringProvider. Tests don't load
  Compose resources, so every string resolves to a fixed message.
 **/

class FakeStringProvider : StringProvider {
    override suspend fun getString(res: StringResource): String = "Test message"

    override suspend fun getStringWithArgs(res: StringResource, vararg args: Any): String = "Test message"
}
