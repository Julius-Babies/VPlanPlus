package es.jvbabi.vplanplus.ui.screens.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import es.jvbabi.vplanplus.ui.preview.Lessons

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LessonCard(
    displayMode: ProfileType,
    lesson: Lesson,
    isCompactMode: Boolean,
    showFindAvailableRoom: Boolean = false,
    onFindAvailableRoomClicked: () -> Unit
) {

    var height = if (!lesson.info.isNullOrBlank() || isCompactMode) 100.dp else 70.dp

    if (showFindAvailableRoom) height += 20.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    ) {
        if (!isCompactMode) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f, false),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Row {
                            Text(
                                text = lesson.displaySubject,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (lesson.subjectIsChanged) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = " • ",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = if (lesson.rooms.isNotEmpty()) lesson.rooms.joinToString(
                                    ", "
                                ) { it } else "-",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (lesson.roomIsChanged) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = " • ",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = if (lesson.teachers.isNotEmpty()) lesson.teachers.joinToString(
                                    ", "
                                ) { it } else "-",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (lesson.teacherIsChanged) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        if (!lesson.info.isNullOrBlank()) Text (
                            text = lesson.info,
                            maxLines = 1,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .basicMarquee(
                                    // Animate forever.
                                    iterations = Int.MAX_VALUE,
                                    velocity = 80.dp,
                                    spacing = MarqueeSpacing(12.dp)
                                )
                        )
                        if (showFindAvailableRoom) {
                            AssistChip(
                                label = {
                                    Text(text = stringResource(id = R.string.lesson_cancelFindRoom))
                                },
                                onClick = { onFindAvailableRoomClicked() },
                                leadingIcon = { Icon(imageVector = Icons.Default.MeetingRoom, contentDescription = null) }
                            )

                        }
                    }
                SubjectIcon(subject = lesson.displaySubject, modifier = Modifier
                    .padding(end = 16.dp)
                    .size(50.dp),
                    tint = if (lesson.subjectIsChanged) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = lesson.displaySubject, style = MaterialTheme.typography.labelLarge)
                when (displayMode) {
                    ProfileType.STUDENT -> {
                        Text(text = lesson.rooms.joinToString(", "), style = MaterialTheme.typography.labelLarge)
                        Text(text = lesson.teachers.joinToString(", "), style = MaterialTheme.typography.labelLarge)
                    }
                    ProfileType.TEACHER -> {
                        Text(text = lesson.`class`.name, style = MaterialTheme.typography.labelLarge)
                        Text(text = lesson.rooms.joinToString(", "), style = MaterialTheme.typography.labelLarge)
                    }
                    ProfileType.ROOM -> {
                        Text(text = lesson.`class`.name, style = MaterialTheme.typography.labelLarge)
                        Text(text = lesson.teachers.joinToString(", "), style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun LessonCardPreview() {
    LessonCard(displayMode = ProfileType.STUDENT, lesson = Lessons.generateLessons(1, true).first(), isCompactMode = false, onFindAvailableRoomClicked = { })
}

@Preview(showBackground = true)
@Composable
private fun CompactLessonCardPreview() {
    LessonCard(displayMode = ProfileType.STUDENT, lesson = Lessons.generateLessons(1).first(), isCompactMode = true, onFindAvailableRoomClicked = { })
}

@Preview(showBackground = true)
@Composable
private fun CancelledLessonCard() {
    LessonCard(displayMode = ProfileType.STUDENT, lesson = Lessons.generateCanceledLesson(), isCompactMode = false, onFindAvailableRoomClicked = { }, showFindAvailableRoom = true)
}