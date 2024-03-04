package es.jvbabi.vplanplus.feature.grades.ui.calculator

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.grades.ui.calculator.components.CollectionGroup
import es.jvbabi.vplanplus.feature.grades.ui.components.Average
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.InfoCard

@Composable
fun GradeCalculatorScreen(
    navHostController: NavHostController,
    viewModel: GradeCalculatorViewModel = hiltViewModel(),
    grades: List<GradeCollection>
) {
    val state = viewModel.state.value
    LaunchedEffect(grades) { viewModel.init(grades, true) }
    GradeCalculatorContent(
        state = state,
        onBack = { navHostController.popBackStack() },
        onAddGrade = { collection, grade -> viewModel.addGrade(collection, grade) },
        onRemoveGrade = { collection, index -> viewModel.removeGradeByIndex(collection, index) },
        onRestoreGrades = viewModel::restore
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeCalculatorContent(
    onBack: () -> Unit = {},
    onAddGrade: (String, Float) -> Unit = { _, _ -> },
    onRemoveGrade: (String, Int) -> Unit = { _, _ -> },
    onRestoreGrades: () -> Unit = {},
    state: GradeCalculatorState
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.gradesCalculator_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) { BackIcon() }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
        ) {
            Box(Modifier.fillMaxWidth()) {
                Average(Modifier.align(Alignment.Center), avg = state.avg)
            }
            InfoCard(
                imageVector = Icons.Default.Info,
                title = stringResource(id = R.string.info),
                text = stringResource(id = R.string.gradesCalculator_text),
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
            )
            OutlinedButton(
                onClick = onRestoreGrades,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                Text(text = stringResource(id = R.string.gradesCalculator_restore))
            }
            state.grades.forEach { collection ->
                CollectionGroup(
                    group = collection.name,
                    grades = collection.grades,
                    avg = collection.grades.map { it.first }.average(),
                    onAddGrade = { value -> onAddGrade(collection.name, value) },
                    onRemoveGrade = { index -> onRemoveGrade(collection.name, index) }
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun GradeCalculatorScreenPreview() {
    GradeCalculatorContent(
        state = GradeCalculatorState(
            grades = emptyList(),
            avg = 2.4
        )
    )
}