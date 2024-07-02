package com.example.studysmart

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.example.studysmart.domain.model.Session
import com.example.studysmart.domain.model.Subject
import com.example.studysmart.domain.model.Task
import com.example.studysmart.presentation.dashboard.DashboardScreen
import com.example.studysmart.presentation.subject.SubjectScreen
import com.example.studysmart.presentation.task.TaskScreen
import com.example.studysmart.presentation.theme.StudySmartTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudySmartTheme {
                TaskScreen()
            }
        }
    }
}
val subjects = listOf(
    Subject(name = "Fretboard", goalHours = 10f, colors = Subject.subjectCardColors[0], subjectId = 0),
    Subject(name = "Music Theory", goalHours = 10f, colors = Subject.subjectCardColors[1], subjectId = 1),
    Subject(name = "Ear Training", goalHours = 10f, colors = Subject.subjectCardColors[2], subjectId = 2),
    Subject(name = "Improvising", goalHours = 10f, colors = Subject.subjectCardColors[3], subjectId = 3),
    Subject(name = "Arpeggios", goalHours = 10f, colors = Subject.subjectCardColors[4], subjectId = 4),
)
val tasks = listOf(
    Task(
        title = "Major Scale",
        description = "",
        dueDate = 0L,
        priority = 0,
        relatedToSubject = "",
        isComplete = false,
        taskSubjectId = 0,
        taskId = 1
    ),
    Task(
        title = "Natural notes",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        isComplete = true,
        taskSubjectId = 1,
        taskId = 2
    ),
    Task(
        title = "Find the key",
        description = "",
        dueDate = 0L,
        priority = 2,
        relatedToSubject = "",
        isComplete = false,
        taskSubjectId = 2,
        taskId = 3
    ),
    Task(
        title = "Minor improv",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        isComplete = false,
        taskSubjectId = 3,
        taskId = 4
    ),
    Task(
        title = "Shape-1",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        isComplete = true,
        taskSubjectId = 4,
        taskId = 5
    ),
)
val sessions = listOf(
    Session(
        relatedToSubject = "Fretboard",
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0
    ),
    Session(
        relatedToSubject = "Fretboard",
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0
    ),
    Session(
        relatedToSubject = "Fretboard",
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0
    )

)

