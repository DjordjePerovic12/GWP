package com.global_water_polo.play_abroad.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * Label above a 58dp rounded input, blue focus ring, as the prototype's `.field` / `.in`.
 *
 * Built on the state-based BasicTextField: keyboard edits land in the field's own TextFieldState and
 * are reported through [onValueChange]. The value-based field lost keystrokes on iOS whenever they came
 * faster than a recomposition ("Jordan" saved as "Jo"). [value] is still honoured when it changes from
 * outside (a saved answer, a reset); values the field reported itself are never written back.
 */
@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    readOnly: Boolean = false,
    imeAction: ImeAction = ImeAction.Next
) {
    val colors = GWPPATheme.colors
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val focusManager = LocalFocusManager.current
    val shape = RoundedCornerShape(14.dp)
    val field = rememberTextFieldState(value)
    val sent = remember { ArrayDeque<String>() }
    val report by rememberUpdatedState(onValueChange)

    LaunchedEffect(field) {
        snapshotFlow { field.text.toString() }.collect { text ->
            if (text == value && sent.isEmpty()) return@collect
            sent.addLast(text)
            if (sent.size > 64) sent.removeFirst()
            report(text)
        }
    }
    LaunchedEffect(value) {
        val i = sent.indexOf(value)
        if (i >= 0) repeat(i + 1) { sent.removeFirst() }
        else if (value != field.text.toString()) field.setTextAndPlaceCursorAtEnd(value)
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = label, style = GWPPATheme.typography.archivoSemiBold13, color = colors.slateGrey)
        BasicTextField(
            state = field,
            readOnly = readOnly,
            lineLimits = TextFieldLineLimits.SingleLine,
            interactionSource = interaction,
            textStyle = GWPPATheme.typography.archivoSemiBold18.copy(color = colors.inkBlack),
            cursorBrush = SolidColor(colors.chlorineBlue),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                capitalization = capitalization,
                imeAction = imeAction
            ),
            // Next goes to the field below; Done closes the keyboard so the pinned Continue is in reach.
            onKeyboardAction = {
                if (imeAction == ImeAction.Done) focusManager.clearFocus() else focusManager.moveFocus(FocusDirection.Down)
            },
            decorator = { inner ->
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 58.dp)
                        .background(colors.colorWhite, shape)
                        .border(1.5.dp, if (focused) colors.chlorineBlue else colors.lineGrey, shape)
                        .padding(horizontal = 16.dp)
                ) {
                    if (field.text.isEmpty() && placeholder.isNotEmpty()) {
                        Text(text = placeholder, style = GWPPATheme.typography.archivoSemiBold18, color = colors.mistGrey)
                    }
                    inner()
                }
            }
        )
    }
}
