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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Label above a 58dp rounded input, blue focus ring, as the prototype's `.field` / `.in`. */
@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    password: Boolean = false,
    readOnly: Boolean = false,
    imeAction: ImeAction = ImeAction.Next
) {
    val colors = GWPPATheme.colors
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val shape = RoundedCornerShape(14.dp)
    // The field owns its text while focused, so fast typing is never overwritten by a state update
    // that is one keystroke behind; outside changes are taken over whenever the field is not in use.
    var text by remember { mutableStateOf(value) }
    if (!focused && text != value) text = value
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = label, style = GWPPATheme.typography.archivoSemiBold13, color = colors.slateGrey)
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onValueChange(it)
            },
            readOnly = readOnly,
            singleLine = true,
            interactionSource = interaction,
            textStyle = GWPPATheme.typography.archivoSemiBold18.copy(color = colors.inkBlack),
            cursorBrush = SolidColor(colors.chlorineBlue),
            visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (password) KeyboardType.Password else keyboardType,
                capitalization = capitalization,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions.Default,
            decorationBox = { inner ->
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 58.dp)
                        .background(colors.colorWhite, shape)
                        .border(1.5.dp, if (focused) colors.chlorineBlue else colors.lineGrey, shape)
                        .padding(horizontal = 16.dp)
                ) {
                    if (text.isEmpty() && placeholder.isNotEmpty()) {
                        Text(text = placeholder, style = GWPPATheme.typography.archivoSemiBold18, color = colors.mistGrey)
                    }
                    inner()
                }
            }
        )
    }
}
