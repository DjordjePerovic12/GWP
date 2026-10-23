package com.global_water_polo.play_abroad.presentation.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.domain.model.TestModel
import com.global_water_polo.play_abroad.resources.Res
import com.global_water_polo.play_abroad.resources.example_custom_message_error
import com.global_water_polo.play_abroad.resources.example_data_response
import com.global_water_polo.play_abroad.resources.example_empty_error
import com.global_water_polo.play_abroad.resources.example_html_error
import com.global_water_polo.play_abroad.resources.example_login_demo
import com.global_water_polo.play_abroad.resources.example_no_data
import com.global_water_polo.play_abroad.resources.example_title
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

@Composable
fun ExampleScreenContent(
    modifier: Modifier = Modifier,
    state: ExampleState,
    onDataResponseClick: () -> Unit,
    onEmptyResponseClick: () -> Unit,
    onHTMLResponseClick: () -> Unit,
    onCustomMessageResponseClick: () -> Unit,
    onLoginDemoClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(GWPPATheme.colors.colorWhite)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = stringResource(Res.string.example_title),
            style = GWPPATheme.typography.defaultRegular16,
            color = GWPPATheme.colors.colorBlack
        )
        Text(
            text = state.testData?.let { "#${it.id} ${it.name}" } ?: stringResource(Res.string.example_no_data),
            style = GWPPATheme.typography.defaultRegular14,
            color = GWPPATheme.colors.colorBlack
        )
        ExampleButton(text = stringResource(Res.string.example_data_response), onClick = onDataResponseClick)
        ExampleButton(text = stringResource(Res.string.example_empty_error), onClick = onEmptyResponseClick)
        ExampleButton(text = stringResource(Res.string.example_html_error), onClick = onHTMLResponseClick)
        ExampleButton(text = stringResource(Res.string.example_custom_message_error), onClick = onCustomMessageResponseClick)
        ExampleButton(text = stringResource(Res.string.example_login_demo), onClick = onLoginDemoClick)
    }
}

@Composable
private fun ExampleButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = text, style = GWPPATheme.typography.defaultRegular14)
    }
}

@Preview(showBackground = true)
@Composable
private fun ExampleScreenContentPreview() {
    GWPPATheme {
        ExampleScreenContent(
            state = ExampleState(testData = TestModel(id = 1, name = "Amplitudo")),
            onDataResponseClick = {},
            onEmptyResponseClick = {},
            onHTMLResponseClick = {},
            onCustomMessageResponseClick = {},
            onLoginDemoClick = {}
        )
    }
}
