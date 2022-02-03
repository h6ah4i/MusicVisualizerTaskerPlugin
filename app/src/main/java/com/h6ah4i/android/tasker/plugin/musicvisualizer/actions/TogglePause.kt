package com.h6ah4i.android.tasker.plugin.musicvisualizer.actions

import com.h6ah4i.android.musicvisualizerapi.MusicVisualizerAPI
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelperNoOutputOrInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput

class TogglePauseCommandRunner: MusicVisualizerServiceCommandRunner<Unit>(MusicVisualizerAPI.COMMAND_TOGGLE_PAUSE, false)
class TogglePauseCommandActionHelper(config: TaskerPluginConfig<Unit>) : TaskerPluginConfigHelperNoOutputOrInput<TogglePauseCommandRunner>(config) {
    override val runnerClass = TogglePauseCommandRunner::class.java
}
class TogglePauseConfigAction: TaskerPluginActionConfigActivity<Unit, Unit, TogglePauseCommandRunner>() {
    override val taskerHelper by lazy { TogglePauseCommandActionHelper(this) }
    override val inputForTasker = TaskerInput(Unit)
    override fun assignFromInput(input: TaskerInput<Unit>) = Unit
}
