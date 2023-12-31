package es.jvbabi.vplanplus.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingsCategory(title: String, content: @Composable () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp, top = 24.dp)
        )
        content()
    }
}

@Composable
fun SettingsSetting(
    icon: ImageVector?,
    title: String,
    subtitle: String? = null,
    type: SettingsType,
    checked: Boolean? = null,
    doAction: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (enabled) doAction() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(modifier = Modifier.weight(1f, false), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else Color.Gray,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else Color.Gray
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else Color.Gray
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            when (type) {
                SettingsType.TOGGLE -> {
                    Switch(checked = checked ?: false, onCheckedChange = { doAction() })
                }

                SettingsType.NUMERIC_INPUT -> {}
                SettingsType.SELECT -> {}

                SettingsType.CHECKBOX -> {
                    // TODO
                }

                SettingsType.FUNCTION -> {
                    // TODO
                }
            }
        }
    }
}

enum class SettingsType {
    TOGGLE,
    CHECKBOX,
    FUNCTION,
    NUMERIC_INPUT,
    SELECT
}

@Composable
@Preview(showBackground = true)
fun SettingsOptionPreview() {
    SettingsSetting(
        icon = Icons.Default.ManageAccounts,
        title = "Test",
        subtitle = "Test",
        type = SettingsType.NUMERIC_INPUT,
        checked = true,
        doAction = {},
        enabled = false
    )
}