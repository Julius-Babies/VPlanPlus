package es.jvbabi.vplanplus.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.SettingsSetting
import es.jvbabi.vplanplus.ui.common.SettingsType
import es.jvbabi.vplanplus.ui.screens.Screen

@Composable
@Preview(showBackground = true)
fun SettingsScreenPreview() {
    SettingsScreen(rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(id = R.string.home_menuSettings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(
                                id = R.string.back
                            )
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val settings = listOf(
                SettingEntry(
                    icon = Icons.Default.Build,
                    title = stringResource(id = R.string.settings_generalSettingsTitle),
                    subtitle = stringResource(id = R.string.settings_generalSettingsSubtitle),
                    onClick = {
                        navController.navigate(Screen.SettingsGeneralSettingsScreen.route)
                    }
                ),
                SettingEntry(
                    icon = Icons.Default.Person,
                    title = stringResource(id = R.string.settings_profileTitle),
                    subtitle = stringResource(id = R.string.settings_profileSubtitle),
                    onClick = {
                        navController.navigate(Screen.SettingsProfileScreen.route)
                    }
                ),
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(settings) {
                    SettingsSetting(
                        icon = it.icon,
                        title = it.title,
                        subtitle = it.subtitle,
                        doAction = it.onClick,
                        type = SettingsType.FUNCTION
                    )
                }
            }
        }
    }
}

private data class SettingEntry(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)