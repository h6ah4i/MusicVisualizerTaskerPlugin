package com.h6ah4i.android.tasker.plugin.musicvisualizer.actions

import com.h6ah4i.android.musicvisualizerapi.MusicVisualizerAPI
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelperNoOutputOrInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput

class PreviousCommandRunner: MusicVisualizerServiceCommandRunner<Unit>(MusicVisualizerAPI.COMMAND_PREVIOUS, true)
class PreviousCommandActionHelper(config: TaskerPluginConfig<Unit>) : TaskerPluginConfigHelperNoOutputOrInput<PreviousCommandRunner>(config) {
    override val runnerClass = PreviousCommandRunner::class.java
}
class PreviousConfigAction: TaskerPluginActionConfigActivity<Unit, Unit, PreviousCommandRunner>() {
    override val taskerHelper by lazy { PreviousCommandActionHelper(this) }
    override val inputForTasker = TaskerInput(Unit)
    override fun assignFromInput(input: TaskerInput<Unit>) = Unit
}
