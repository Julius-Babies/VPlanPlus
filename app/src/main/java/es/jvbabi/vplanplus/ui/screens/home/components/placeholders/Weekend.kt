package es.jvbabi.vplanplus.ui.screens.home.components.placeholders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@Composable
fun WeekendPlaceholder(
    compactMode: Boolean,
    type: WeekendType
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (compactMode) Arrangement.SpaceBetween else Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Weekend,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (compactMode) stringResource(id = R.string.home_weekendTitle).replace("/", " ").split("").joinToString("\n") else stringResource(id = R.string.home_weekendTitle),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            if (!compactMode) when (type) {
                WeekendType.TODAY -> Text(
                    text = stringResource(id = R.string.home_weekendText),
                    textAlign = TextAlign.Center
                )
                WeekendType.COMING_UP -> Text(
                    text = stringResource(id = R.string.home_weekendCommingUpText),
                    textAlign = TextAlign.Center
                )
                WeekendType.OVER -> Text(
                    text = stringResource(id = R.string.home_weekendOverText),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

enum class WeekendType {
    TODAY, COMING_UP, OVER
}

@Composable
@Preview(showBackground = true)
fun WeekendPlaceholderPreview() {
    WeekendPlaceholder(type = WeekendType.TODAY, compactMode = false)
}

@Composable
@Preview(showBackground = true)
fun WeekendPlaceholderPreview2() {
    WeekendPlaceholder(type = WeekendType.COMING_UP, compactMode = false)
}

@Composable
@Preview(showBackground = true)
fun WeekendPlaceholderPreview3() {
    WeekendPlaceholder(type = WeekendType.OVER, compactMode = false)
}

@Preview(showBackground = true)
@Composable
fun WeekendPlaceholderCompactPreview() {
    WeekendPlaceholder(compactMode = true, type = WeekendType.TODAY)
}