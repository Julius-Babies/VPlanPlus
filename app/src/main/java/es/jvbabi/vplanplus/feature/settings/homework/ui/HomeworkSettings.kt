package es.jvbabi.vplanplus.feature.settings.homework.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.IconSettingsState
import es.jvbabi.vplanplus.ui.common.Setting
import es.jvbabi.vplanplus.ui.common.SettingsSetting
import es.jvbabi.vplanplus.ui.common.SettingsType
import es.jvbabi.vplanplus.ui.common.TimePicker
import es.jvbabi.vplanplus.util.toBlackAndWhite
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HomeworkSettingsScreen(
    navHostController: NavHostController,
    viewModel: HomeworkSettingsViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    HomeworkSettingsContent(
        onBack = { navHostController.navigateUp() },
        onToggleNotificationOnNewHomework = viewModel::onToggleNotificationOnNewHomework,
        onToggleRemindUserOnUnfinishedHomework = viewModel::onToggleRemindUserOnUnfinishedHomework,
        onSetDefaultRemindTime = viewModel::onSetDefaultRemindTime,
        state = state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeworkSettingsContent(
    onBack: () -> Unit,
    onToggleNotificationOnNewHomework: () -> Unit,
    onToggleRemindUserOnUnfinishedHomework: () -> Unit,
    onSetDefaultRemindTime: (Int, Int) -> Unit,
    state : HomeworkSettingsState
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.settingsHomework_title))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        BackIcon()
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsSetting(
                icon = Icons.Default.NotificationAdd,
                title = stringResource(id = R.string.settingsHomework_showNotificationOnNewHomeworkTitle),
                subtitle = stringResource(id = R.string.settingsHomework_showNotificationOnNewHomeworkSubtitle),
                type = SettingsType.TOGGLE,
                checked = state.notificationOnNewHomework,
                doAction = onToggleNotificationOnNewHomework
            )
            SettingsSetting(
                icon = Icons.Default.NotificationImportant,
                title = stringResource(id = R.string.settingsHomework_reminderNotificationEnabledTitle),
                subtitle = stringResource(id = R.string.settingsHomework_reminderNotificationEnabledSubtitle),
                type = SettingsType.TOGGLE,
                checked = state.remindUserOnUnfinishedHomework,
                doAction = onToggleRemindUserOnUnfinishedHomework
            )
            TimePicker(
                IconSettingsState(
                    imageVector = Icons.Default.AccessTime,
                    title = stringResource(id = R.string.settingsHomework_defaultNotificationTimeTitle),
                    subtitle = stringResource(
                        id = R.string.settingsHomework_defaultNotificationTimeSubtitle,
                        state.defaultNotificationTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                    ),
                    type = SettingsType.FUNCTION,
                    enabled = state.remindUserOnUnfinishedHomework,
                    doAction = {
                        val time = (it as String).split(":")
                        onSetDefaultRemindTime(time[0].toInt(), time[1].toInt())
                    }
                ),
                state.defaultNotificationTime.hour,
                state.defaultNotificationTime.minute
            )

            Setting(
                IconSettingsState(
                    title = stringResource(id = R.string.settingsHomework_exceptionsTitle),
                    subtitle = stringResource(id = R.string.settingsHomework_exceptionsSubtitle),
                    type = SettingsType.DISPLAY,
                    imageVector = Icons.Default.MoreTime,
                    customContent = {
                        LazyRow {
                            item { Spacer(modifier = Modifier.size((16+50).dp)) }
                            items(7) {
                                DayCard(
                                    modifier = Modifier.padding(end = 8.dp),
                                    dayOfWeek = DayOfWeek.of(it+1),
                                    enabled = it % 2 == 0,
                                    time = state.defaultNotificationTime
                                )
                            }
                        }
                    }
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeworkSettingsScreenPreview() {
    HomeworkSettingsContent(
        onBack = {},
        onToggleNotificationOnNewHomework = {},
        onToggleRemindUserOnUnfinishedHomework = {},
        onSetDefaultRemindTime = { _, _ -> },
        state = HomeworkSettingsState(
            notificationOnNewHomework = true
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun DayCardPreview() {
    DayCard(
        dayOfWeek = DayOfWeek.THURSDAY,
        enabled = true,
        time = LocalDateTime.of(1970, 1, 1, 16, 45, 0)
    )
}

@Composable
private fun DayCard(
    modifier: Modifier = Modifier,
    dayOfWeek: DayOfWeek,
    enabled: Boolean,
    time: LocalDateTime
) {
    Column(
        modifier = modifier
            .width(60.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(top = 8.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val colorScheme = MaterialTheme.colorScheme
        val primaryColor = if (enabled) colorScheme.primary else colorScheme.primary.toBlackAndWhite()
        Box(
            modifier = Modifier
                .size(40.dp)
                .drawWithContent {
                    drawCircle(
                        color = primaryColor,
                        radius = 20.dp.toPx()
                    )
                    drawCircle(
                        color = colorScheme.surfaceContainerHigh,
                        radius = 17.dp.toPx()
                    )
                    drawContent()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault()),
                style = MaterialTheme.typography.labelLarge,
                color = primaryColor
            )
        }

        Text(
            text = time.format(DateTimeFormatter.ofPattern("HH:mm")),
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.onPrimaryContainer,
            modifier = Modifier
                .padding(top = 15.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp)
        )
    }
}