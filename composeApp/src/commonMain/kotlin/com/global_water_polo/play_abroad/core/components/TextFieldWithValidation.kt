package com.global_water_polo.play_abroad.core.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
  Template composable for a TextField with validation support.
  This is a base implementation that should be customized to match your app's design system.

  @param value Current text value
  @param onValueChange Callback for text changes
  @param label Label text for the field
  @param errorMessage Error message to display (null if no error)
  @param keyboardOptions Keyboard configuration options
  @param keyboardActions Keyboard action handlers
  @param modifier Optional modifier for the component
  @param visualTransformation Optional text transformation (e.g., for passwords)
  @param leadingIcon Optional leading icon
 TODO: Consider renaming
  **/

@Composable
fun TextFieldWithValidation(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorMessage: String?,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = errorMessage != null,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            leadingIcon = leadingIcon,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                errorContainerColor = Color.Transparent,
                errorIndicatorColor = GWPPATheme.colors.liveRed
            )
        )

        AnimatedVisibility(
            visible = errorMessage != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = errorMessage ?: "",
                color = GWPPATheme.colors.liveRed,
                style = GWPPATheme.typography.archivoSemiBold13,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
