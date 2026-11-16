package com.global_water_polo.play_abroad.presentation.app.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppSheet
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.ChoiceChip
import com.global_water_polo.play_abroad.core.components.FormField
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.presentation.app.feed.FeedState
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * The bookmark on a clip (v29's saveSheet). Elite: pick one of the folders or name a new one; a saved clip
 * can be removed. Without Elite: "Save with Elite."
 */
@Composable
fun SaveSheet(
    story: Int,
    state: FeedState,
    onFolderClick: (String) -> Unit,
    onCreateFolder: (String) -> Unit,
    onRemoveClick: () -> Unit,
    onSeeEliteClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = GWPPATheme.colors
    val kicker = GWPPATheme.typography.archivoBold11.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.16.em)
    AppSheet(onDismiss = onDismiss) {
        if (!state.membership.elite) {
            Text(stringResource(Res.string.save_kicker_folders).uppercase(), style = kicker, color = colors.antiqueGold, modifier = Modifier.padding(top = 6.dp))
            Headline(text = stringResource(Res.string.save_elite_title))
            HelpText(text = stringResource(Res.string.save_elite_body))
            PrimaryButton(text = stringResource(Res.string.mb_see_elite), onClick = onSeeEliteClick, style = ButtonStyle.BALL, modifier = Modifier.padding(top = 8.dp))
            TextLink(text = stringResource(Res.string.save_not_now), onClick = onDismiss, modifier = Modifier.align(Alignment.CenterHorizontally))
            return@AppSheet
        }
        val current = state.folderOf(story)
        Text(stringResource(Res.string.save_kicker_folder).uppercase(), style = kicker, color = colors.antiqueGold, modifier = Modifier.padding(top = 6.dp))
        Headline(text = FeedState.STORIES[story].title)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
            state.folders.forEach { folder ->
                val on = folder == current
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (on) colors.chlorineBlueSoft else colors.colorWhite)
                        .border(if (on) 2.dp else 1.dp, if (on) colors.inkBlack else colors.lineGrey, RoundedCornerShape(14.dp))
                        .clickable { onFolderClick(folder) }
                        .padding(12.dp)
                ) {
                    // `.fd-i`: the folder's icon tile.
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(colors.nightNavy)) {
                        BookmarkIcon(colors.eliteGold, Modifier.size(16.dp))
                    }
                    Text(folder, style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack, modifier = Modifier.weight(1f))
                    if (on) Text("✓", style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
                }
            }
        }
        var name by rememberSaveable { mutableStateOf("") }
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
            FormField(
                label = stringResource(Res.string.save_new_label),
                value = name,
                onValueChange = { name = it.take(FeedState.FOLDER_NAME_MAX) },
                placeholder = stringResource(Res.string.save_new_folder),
                modifier = Modifier.weight(1f),
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            )
            ChoiceChip(
                text = stringResource(Res.string.save_create),
                selected = name.isNotBlank(),
                onClick = { if (name.isNotBlank()) onCreateFolder(name.trim()) }
            )
        }
        if (current != null) {
            TextLink(text = stringResource(Res.string.save_remove, current), onClick = onRemoveClick, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}
