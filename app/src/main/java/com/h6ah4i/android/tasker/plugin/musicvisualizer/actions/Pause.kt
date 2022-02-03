package com.h6ah4i.android.tasker.plugin.musicvisualizer.actions

import com.h6ah4i.android.musicvisualizerapi.MusicVisualizerAPI
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelperNoOutputOrInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput

class PauseCommandRunner: MusicVisualizerServiceCommandRunner<Unit>(MusicVisualizerAPI.COMMAND_PAUSE, false)
class PauseCommandActionHelper(config: TaskerPluginConfig<Unit>) : TaskerPluginConfigHelperNoOutputOrInput<PauseCommandRunner>(config) {
    override val runnerClass = PauseCommandRunner::class.java
}
class PauseConfigAction: TaskerPluginActionConfigActivity<Unit, Unit, PauseCommandRunner>() {
    override val taskerHelper by lazy { PauseCommandActionHelper(this) }
    override val inputForTasker = TaskerInput(Unit)
    override fun assignFromInput(input: TaskerInput<Unit>) = Unit
}
