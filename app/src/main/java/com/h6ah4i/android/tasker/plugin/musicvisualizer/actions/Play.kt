package com.h6ah4i.android.tasker.plugin.musicvisualizer.actions

import com.h6ah4i.android.musicvisualizerapi.MusicVisualizerAPI
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelperNoOutputOrInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput

class PlayCommandRunner: MusicVisualizerServiceCommandRunner<Unit>(MusicVisualizerAPI.COMMAND_PLAY, true)
class PlayCommandActionHelper(config: TaskerPluginConfig<Unit>) : TaskerPluginConfigHelperNoOutputOrInput<PlayCommandRunner>(config) {
    override val runnerClass = PlayCommandRunner::class.java
}
class PlayConfigAction: TaskerPluginActionConfigActivity<Unit, Unit, PlayCommandRunner>() {
    override val taskerHelper by lazy { PlayCommandActionHelper(this) }
    override val inputForTasker = TaskerInput(Unit)
    override fun assignFromInput(input: TaskerInput<Unit>) = Unit
}
