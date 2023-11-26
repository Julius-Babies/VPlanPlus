package es.jvbabi.vplanplus.ui.screens.home.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.usecase.Keys
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.VPlanUseCases
import es.jvbabi.vplanplus.util.DateUtils
import es.jvbabi.vplanplus.util.Worker
import es.jvbabi.vplanplus.worker.SyncWorker
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val app: Application,
    private val profileUseCases: ProfileUseCases,
    private val vPlanUseCases: VPlanUseCases,
    private val planRepository: PlanRepository,
    private val keyValueRepository: KeyValueRepository,
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private val dataSyncJobs: HashMap<LocalDate, Job> = HashMap()

    private var version = 0L

    private var homeUiSyncJob: Job? = null

    init {
        if (homeUiSyncJob == null) homeUiSyncJob = viewModelScope.launch {
            combine(
                profileUseCases.getProfiles(),
                keyValueRepository.getFlow(Keys.ACTIVE_PROFILE),
                keyValueRepository.getFlow(Keys.LAST_SYNC_TS),
                keyValueRepository.getFlow(Keys.LESSON_VERSION_NUMBER),
                Worker.isWorkerRunningFlow("SyncWork", app.applicationContext)
            ) { profiles, activeProfileId, lastSyncTs, v, isSyncing ->
                Log.d("HomeViewModel.MainFlow", "activeProfile ID: $activeProfileId")
                version = v?.toLong()?:0
                _state.value.copy(
                    profiles = profiles,
                    activeProfile = profiles.find { it.id == activeProfileId?.toLong() },
                    lastSync = if (lastSyncTs != null) DateUtils.getDateTimeFromTimestamp(lastSyncTs.toLong()) else null,
                    syncing = isSyncing
                )
            }.collect {
                _state.value = it
                killUiSyncJobs()
                startLessonUiSync(state.value.date, 5)
            }
        }
    }

    /**
     * Called when the user changes the page
     * @param date The date of the new page
     */
    fun onPageChanged(date: LocalDate) {
        startLessonUiSync(date, 5)
        _state.value = _state.value.copy(date = date)
    }

    /**
     * Called when user clicks notification and gets redirected to specific page
     * @param date The date of the page to show
     */
    fun onInitPageChanged(date: LocalDate) {
        _state.value = _state.value.copy(initDate = date)
        onPageChanged(date)
    }

    /**
     * Starts the UI sync for the given date
     * @param date The date to sync
     * @param neighbors The number of neighbors to sync as well
     */
    private fun startLessonUiSync(date: LocalDate, neighbors: Int) {
        if (dataSyncJobs.containsKey(date) || getActiveProfile() == null) return
        dataSyncJobs[date] = viewModelScope.launch {
            planRepository.getDayForProfile(getActiveProfile()!!, date, version).distinctUntilChanged().collect { day ->
                _state.value = _state.value.copy(lessons = _state.value.lessons.plus(
                    date to day
                ))
            }
        }
        repeat(neighbors/2) {
            startLessonUiSync(date.plusDays(it.toLong()), 0)
            startLessonUiSync(date.minusDays(it.toLong()), 0)
        }
    }

    fun getVPlanData(context: Context) {
        viewModelScope.launch {

            if (_state.value.syncing) {
                Log.d("HomeViewModel", "getVPlanData; already syncing")
                return@launch
            }

            val syncWork = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .addTag("SyncWork")
                .addTag("ManualSyncWork")
                .build()
            WorkManager.getInstance(context).enqueue(syncWork)
        }
    }

    fun deletePlans() {
        viewModelScope.launch {
            vPlanUseCases.deletePlans()
        }
    }

    fun onProfileSelected(profileId: Long) {
        Log.d("HomeViewMode.ChangedProfile", "onProfileSelected: $profileId")
        viewModelScope.launch {
            killUiSyncJobs()
            clearLessons()
            keyValueRepository.set(Keys.ACTIVE_PROFILE, profileId.toString())
        }
    }

    fun setViewType(viewType: ViewType) {
        _state.value = _state.value.copy(viewMode = viewType)
    }

    private fun getActiveProfile() = _state.value.activeProfile

    private fun clearLessons() {
        _state.value = _state.value.copy(lessons = mapOf())
    }

    private fun killUiSyncJobs() {
        dataSyncJobs.forEach { it.value.cancel() }
        dataSyncJobs.clear()
    }
}

data class HomeState(
    val lessons: Map<LocalDate, Day> = mapOf(),
    val isLoading: Boolean = false,
    val profiles: List<Profile> = listOf(),
    val activeProfile: Profile? = null,
    val initDate: LocalDate = LocalDate.now(),
    val date: LocalDate = LocalDate.now(),
    val viewMode: ViewType = ViewType.DAY,
    val notificationPermissionGranted: Boolean = false,
    val syncing: Boolean = false,

    val lastSync: LocalDateTime? = null
)

enum class ViewType {
    WEEK, DAY
}