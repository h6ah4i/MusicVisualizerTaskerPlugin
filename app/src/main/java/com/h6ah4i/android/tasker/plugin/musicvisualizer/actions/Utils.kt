package com.h6ah4i.android.tasker.plugin.musicvisualizer.actions

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.h6ah4i.android.musicvisualizerapi.MusicVisualizerAPI
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerAction
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultError
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess

abstract class MusicVisualizerServiceCommandRunner<TInput: Any>(
    @com.h6ah4i.android.musicvisualizerapi.annotation.Command private val command: String,
    private val startForeground: Boolean,
    ) : TaskerPluginRunnerAction<TInput, Unit>() {
    override fun run(context: Context, input: TaskerInput<TInput>): TaskerPluginResult<Unit> {
        return try {
            val intent = onCreateCommandIntent(context, command, input)
            if (startForeground) {
                ContextCompat.startForegroundService(context, intent)
            } else {
                context.startService(intent)
            }
            TaskerPluginResultSucess()
        } catch (e: Throwable) {
            TaskerPluginResultError(e)
        }
    }

    protected open fun onCreateCommandIntent(context: Context, command: String, input: TaskerInput<TInput>): Intent {
        return MusicVisualizerAPI.createPlayerCommandServiceIntent(context, command)
    }
}
