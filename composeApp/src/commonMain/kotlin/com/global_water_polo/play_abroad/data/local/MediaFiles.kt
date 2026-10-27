package com.global_water_polo.play_abroad.data.local

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.copyTo
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.name
import kotlin.time.Clock

/**
 * Keeps picked photos, videos and documents in the app's own storage. The system pickers only lend a file
 * for a while (Android content URIs, iOS temporary copies), so a saved profile would point at nothing after
 * a restart. Nothing leaves the device; uploading comes with the backend.
 */
object MediaFiles {
    private val dir: PlatformFile get() = FileKit.filesDir / "media"

    /** A copy of [file] in app storage, or [file] itself if copying fails (it still works until a restart). */
    suspend fun keep(file: PlatformFile): PlatformFile = runCatching {
        if (!dir.exists()) dir.createDirectories()
        val copy = dir / "${Clock.System.now().toEpochMilliseconds()}_${file.name}"
        file copyTo copy
        copy
    }.getOrElse { file }

    /** Deletes every kept file (sign out). */
    suspend fun clear() {
        runCatching { if (dir.exists()) dir.delete(mustExist = false, recursively = true) }
    }
}
