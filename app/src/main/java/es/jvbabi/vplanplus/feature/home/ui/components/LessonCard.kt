package es.jvbabi.vplanplus.feature.home.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.AssistChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.util.DateUtils.toZonedLocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun LessonCard(
    modifier: Modifier = Modifier,
    onBookRoomClicked: () -> Unit = {},
    lessons: List<Lesson>,
    time: ZonedDateTime,
) {
    var expanded by rememberSaveable {
        mutableStateOf(lessons.any { it.progress(time) in 0.0..<1.0 })
    }

    LaunchedEffect(time) {
        expanded = lessons.any { it.progress(time) in 0.0..<1.0 }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Text(
            text = pluralStringResource(id = R.plurals.homeLesson_current, lessons.size),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(8.dp)
        )
        Row(
            Modifier
                .padding(start = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LessonNumberAndTime(
                lessons.first().lessonNumber,
                lessons.first().start,
                lessons.first().end
            )
            Column {
                lessons.forEachIndexed { i, lesson ->

                    val defaultStyle =
                        MaterialTheme
                            .typography
                            .titleMedium
                            .toSpanStyle()
                    val headerText = buildAnnotatedString {
                        withStyle(
                            defaultStyle
                                .copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                        ) {
                            if (lesson.subjectIsChanged) {
                                withStyle(
                                    defaultStyle.copy(
                                        color = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    if (lesson.displaySubject == "-") append(
                                        stringResource(
                                            id = R.string.home_activeDayNextLessonCanceled,
                                        )
                                    )
                                    else append(lesson.displaySubject)
                                }
                            } else {
                                if (lesson.displaySubject == "-") append(
                                    stringResource(
                                        id = R.string.home_activeDayNextLessonCanceled,
                                    )
                                )
                                else append(lesson.displaySubject)
                            }

                            if (lesson.displaySubject == "-") return@buildAnnotatedString

                            if (lesson.rooms.isNotEmpty()) {
                                append(" ")
                                append(DOT)
                                append(" ")
                                if (lesson.roomIsChanged) {
                                    withStyle(
                                        defaultStyle.copy(
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        append(lesson.rooms.joinToString(", "))
                                    }
                                } else append(lesson.rooms.joinToString(", "))
                            }

                            if (lesson.teachers.isNotEmpty()) {
                                append(" ")
                                append(DOT)
                                append(" ")
                                if (lesson.teacherIsChanged) {
                                    withStyle(
                                        defaultStyle.copy(
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        append(lesson.teachers.joinToString(", "))
                                    }
                                } else append(lesson.teachers.joinToString(", "))
                            }
                        }
                    }

                    Text(text = headerText)
                    if (!lesson.info.isNullOrBlank()) Text(
                        text = lesson.info,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    AnimatedVisibility(
                        visible = expanded,
                        enter = expandVertically(tween(300)),
                        exit = shrinkVertically(tween(300))
                    ) {
                        LazyRow {
                            if (lesson.rooms.isEmpty()) item {
                                AssistChip(
                                    onClick = onBookRoomClicked,
                                    label = { Text(text = stringResource(id = R.string.home_activeBookRoom)) },
                                    leadingIcon = {
                                        Icon(Icons.Default.MeetingRoom, null)
                                    },
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                            item {
                                AssistChip(
                                    onClick = { /*TODO*/ },
                                    label = { Text(text = stringResource(id = R.string.home_addHomeworkLabel)) },
                                    leadingIcon = {
                                        Icon(Icons.AutoMirrored.Default.MenuBook, null)
                                    },
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        }
                    }

                    if (i != lessons.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(end = 8.dp, bottom = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonNumberAndTime(lessonNumber: Int, start: ZonedDateTime, end: ZonedDateTime) {
    Column(
        modifier = Modifier
            .padding(end = 8.dp)
            .width(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$lessonNumber.",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            text = start.toZonedLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm")),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelSmall,
        )
        Text(
            text = end.toZonedLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm")),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}