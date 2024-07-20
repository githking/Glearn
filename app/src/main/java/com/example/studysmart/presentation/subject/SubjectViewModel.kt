package com.example.studysmart.presentation.subject

import android.annotation.SuppressLint
import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmart.domain.model.Subject
import com.example.studysmart.domain.model.Task
import com.example.studysmart.domain.repository.SessionRepository
import com.example.studysmart.domain.repository.SubjectRepository
import com.example.studysmart.domain.repository.TaskRepository
import com.example.studysmart.presentation.navArgs
import com.example.studysmart.util.SnackbarEvent
import com.example.studysmart.util.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val taskRepository: TaskRepository,
    private val sessionRepository: SessionRepository,
    savedStateHandle: SavedStateHandle

): ViewModel() {

    private  val navArgs: SubjectScreenNavArgs = savedStateHandle.navArgs()


    private val _state = MutableStateFlow(SubjectState())
    val state = combine(
        _state,
        taskRepository.getUpcomingTasksForSubject(navArgs.subjectId),
        taskRepository.getCompletedTasksForSubjects(navArgs.subjectId),
        sessionRepository.getRecentTenSessionsForSubject(navArgs.subjectId),
        sessionRepository.getTotalSessionsDurationBySubject(navArgs.subjectId)
    ) {state, upcomingTasks, completedTask, recentSessions, totalSessionsDuration ->
        state.copy(
            upcomingTasks = upcomingTasks,
            completedTasks = completedTask,
            recentSessions = recentSessions,
            studiedHours = totalSessionsDuration.toHours()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SubjectState()
    )

    private val _snackbarEventFLow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFLow.asSharedFlow()

    init {
        fetchSubject()
    }

    fun onEvent(event: SubjectEvent){
        when(event){
            is SubjectEvent.OnSubjectCardColorChange -> {
                _state.update {
                    it.copy(subjectCardColors = event.color)
                }
            }
            is SubjectEvent.OnSubjectNameChange -> {
                _state.update {
                    it.copy(subjectName = event.name)
                }
            }
            is SubjectEvent.OnGoalStudyHoursChange -> {
                _state.update {
                    it.copy(goalStudyHours = event.hours)
                }
            }
            SubjectEvent.UpdateSubject -> updateSubject()
            SubjectEvent.DeleteSession -> {}
            SubjectEvent.DeleteSubject -> deleteSubject()
            is SubjectEvent.OnDeleteSessionButtonClick -> {}
            is SubjectEvent.OnTaskIsCompleteChange -> {
                updateTask(event.task)
            }
            SubjectEvent.UpdateProgress -> {
                val goalStudyHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f
                _state.update {
                    it.copy(
                        progress = (state.value.studiedHours / goalStudyHours).coerceIn(0f,1f)
                    )
                }
            }
        }
    }

    private fun updateSubject() {
            viewModelScope.launch {
                try{
                subjectRepository.upsertSubject(
                    subject = Subject(
                        subjectId = state.value.currentSubjectId,
                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                        colors = state.value.subjectCardColors.map { it.toArgb() }
                    )
                )
                _snackbarEventFLow.emit(
                    SnackbarEvent.ShowSnackbar(message = "Subject updated successfully.")
                )
            }catch (e: Exception){
                    _snackbarEventFLow.emit(
                        SnackbarEvent.ShowSnackbar(
                            "Couldn't update subject ${e.message}",
                            SnackbarDuration.Long
                        )
                    )
            }
        }
    }

    private  fun fetchSubject(){
        viewModelScope.launch {
            subjectRepository
                .getSubjectById(navArgs.subjectId)?.let { subject ->
                    _state.update {
                        it.copy(
                            subjectName = subject.name,
                            goalStudyHours = subject.goalHours.toString(),
                            subjectCardColors = subject.colors.map { Color(it) },
                            currentSubjectId = subject.subjectId
                        )
                    }

                }
         }
    }
    @SuppressLint("SuspiciousIndentation")
    private fun deleteSubject(){
        viewModelScope.launch {
            try{
                val currentSubjectId = state.value.currentSubjectId
                    if (currentSubjectId != null) {
                        withContext(Dispatchers.IO) {
                            subjectRepository.deleteSubject(subjectId = currentSubjectId)
                            _snackbarEventFLow.emit(
                                SnackbarEvent.ShowSnackbar("Subject deleted successfully")
                            )
                        }
                        _snackbarEventFLow.emit(SnackbarEvent.NavigateUp)
                    } else{
                        _snackbarEventFLow.emit(
                            SnackbarEvent.ShowSnackbar("No Subject to delete")
                        )
                    }
            } catch (e: Exception){
                _snackbarEventFLow.emit(
                    SnackbarEvent.ShowSnackbar("Couldn't delete subject. ${e.message}",
                    duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }
    private fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.upsertTask(
                    task = task.copy(isComplete = !task.isComplete)
                )
                if(task.isComplete) {
                    _snackbarEventFLow.emit(
                        SnackbarEvent.ShowSnackbar(message = "Saved in upcoming tasks.")
                    )
                }else{
                    _snackbarEventFLow.emit(
                        SnackbarEvent.ShowSnackbar(message = "Saved in completed tasks.")
                    )
                }
            } catch (e: Exception){
                _snackbarEventFLow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Couldn't update task ${e.message}",
                        SnackbarDuration.Long
                    )
                )
            }
        }
    }

}