package com.global_water_polo.play_abroad.core.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.PickerResultLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap

/** What the system picker shows: the photo library, videos, or documents (PDF and Word). */
enum class MediaKind(val type: FileKitType) {
    PHOTO(FileKitType.Image),
    VIDEO(FileKitType.Video),
    DOCUMENT(FileKitType.File("pdf", "doc", "docx"))
}

/** Opens the system picker for one slot ([key]) and hands back the file picked for it. */
class MediaPicker internal constructor(
    private val launchers: Map<MediaKind, PickerResultLauncher>,
    private val pending: MutableState<String?>
) {
    fun open(key: String, kind: MediaKind) {
        pending.value = key
        launchers.getValue(kind).launch()
    }
}

/**
 * One picker for a screen with several upload slots. Android uses the system photo picker and document
 * picker, iOS uses PHPicker and the Files picker, so neither needs a permission prompt. Cancelling does nothing.
 */
@Composable
fun rememberMediaPicker(onPicked: (key: String, file: PlatformFile) -> Unit): MediaPicker {
    val pending = remember { mutableStateOf<String?>(null) }
    val report by rememberUpdatedState(onPicked)
    val onResult: (PlatformFile?) -> Unit = { file ->
        val key = pending.value
        pending.value = null
        if (file != null && key != null) report(key, file)
    }
    val photo = rememberFilePickerLauncher(type = MediaKind.PHOTO.type, onResult = onResult)
    val video = rememberFilePickerLauncher(type = MediaKind.VIDEO.type, onResult = onResult)
    val document = rememberFilePickerLauncher(type = MediaKind.DOCUMENT.type, onResult = onResult)
    return remember(photo, video, document) {
        MediaPicker(mapOf(MediaKind.PHOTO to photo, MediaKind.VIDEO to video, MediaKind.DOCUMENT to document), pending)
    }
}

/** A picked photo, decoded off the main thread; nothing is drawn until it is ready. */
@Composable
fun PickedImage(file: PlatformFile, modifier: Modifier = Modifier) {
    val bitmap by produceState<ImageBitmap?>(null, file) {
        value = runCatching { file.toImageBitmap() }.getOrNull()
    }
    bitmap?.let { Image(bitmap = it, contentDescription = null, contentScale = ContentScale.Crop, modifier = modifier) }
}
