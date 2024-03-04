package es.jvbabi.vplanplus.feature.grades.domain.usecase

data class GradeUseCases(
    val isEnabledUseCase: IsEnabledUseCase,
    val getGradesUseCase: GetGradesUseCase,
    val showBannerUseCase: ShowBannerUseCase,
    val hideBannerUseCase: HideBannerUseCase,
    val calculateAverageUseCase: CalculateAverageUseCase
)