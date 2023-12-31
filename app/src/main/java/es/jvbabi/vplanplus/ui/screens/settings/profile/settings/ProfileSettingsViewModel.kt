package es.jvbabi.vplanplus.ui.screens.settings.profile.settings

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.data.model.ProfileCalendarType
import es.jvbabi.vplanplus.domain.model.Calendar
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.CalendarRepository
import es.jvbabi.vplanplus.domain.usecase.KeyValueUseCases
import es.jvbabi.vplanplus.domain.usecase.Keys
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class ProfileSettingsViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val keyValueUseCases: KeyValueUseCases,
    private val calendarRepository: CalendarRepository,
) : ViewModel() {

    private val _state = mutableStateOf(ProfileSettingsState())
    val state: State<ProfileSettingsState> = _state

    @SuppressLint("Range")
    fun init(profileId: UUID) {

        viewModelScope.launch {
            combine(
                profileUseCases.getProfileById(profileId),
                calendarRepository.getCalendars(),
            ) { profile, calendars ->
                if (profile == null) _state.value.copy(initDone = true)
                else _state.value.copy(
                    profile = profile,
                    calendars = calendars,
                    initDone = true,
                    profileCalendar = calendars.firstOrNull { it.id == profile.calendarId })
            }.collect {
                _state.value = it
            }
        }
    }

    private fun setDeleteProfileResult(result: ProfileManagementDeletionResult) {
        viewModelScope.launch {
            _state.value = _state.value.copy(deleteProfileResult = result)
        }
    }

    fun setCalendarMode(calendarMode: ProfileCalendarType) {
        viewModelScope.launch {
            _state.value.profile?.let { profile ->
                profileUseCases.setCalendarType(profile.id, calendarType = calendarMode)
            }
        }
    }

    fun setCalendar(calendarId: Long) {
        viewModelScope.launch {
            _state.value.profile?.let { profile ->
                profileUseCases.setCalendarId(profile.id, calendarId)
            }
        }
    }

    fun setDialogOpen(open: Boolean) {
        _state.value = _state.value.copy(dialogOpen = open)
    }

    fun setDialogCall(call: @Composable () -> Unit) {
        _state.value = _state.value.copy(dialogCall = call)
    }

    fun deleteProfile(context: Context) {
        viewModelScope.launch {
            _state.value.profile?.let { profile ->
                if (profileUseCases.getProfilesBySchoolId(
                        profileUseCases.getSchoolFromProfileId(
                            profile.id
                        ).schoolId
                    ).size == 1
                ) {
                    setDeleteProfileResult(ProfileManagementDeletionResult.LAST_PROFILE)
                    return@launch
                }
                val activeProfile = profileUseCases.getActiveProfile()!!
                if (activeProfile.id == profile.id) {
                    keyValueUseCases.set(Keys.ACTIVE_PROFILE, (profileUseCases.getProfiles().first().find { it.id != profile.id }?.id ?: -1).toString())
                }
                profileUseCases.deleteProfile(profile.id)
                setDeleteProfileResult(ProfileManagementDeletionResult.SUCCESS)
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.deleteNotificationChannel("PROFILE_${profile.originalName}")
            }
        }
    }

    fun renameProfile(name: String) {
        viewModelScope.launch {
            _state.value.profile?.let { profile ->
                profileUseCases.setDisplayName(profile.id, name)
            }
        }
    }
}

data class ProfileSettingsState(
    val initDone: Boolean = false,
    val profile: Profile? = null,
    val profileCalendar: Calendar? = null,
    val deleteProfileResult: ProfileManagementDeletionResult? = null,
    val calendars: List<Calendar> = listOf(),

    val dialogOpen: Boolean = false,
    val dialogCall: @Composable () -> Unit = {}
)


enum class ProfileManagementDeletionResult {
    SUCCESS,
    LAST_PROFILE,
}