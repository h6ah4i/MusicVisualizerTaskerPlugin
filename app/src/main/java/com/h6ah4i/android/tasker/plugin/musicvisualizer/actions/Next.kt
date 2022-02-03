package com.h6ah4i.android.tasker.plugin.musicvisualizer.actions

import com.h6ah4i.android.musicvisualizerapi.MusicVisualizerAPI
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelperNoOutputOrInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput

class NextCommandRunner: MusicVisualizerServiceCommandRunner<Unit>(MusicVisualizerAPI.COMMAND_NEXT, true)
class NextCommandActionHelper(config: TaskerPluginConfig<Unit>) : TaskerPluginConfigHelperNoOutputOrInput<NextCommandRunner>(config) {
    override val runnerClass = NextCommandRunner::class.java
}
class NextConfigAction: TaskerPluginActionConfigActivity<Unit, Unit, NextCommandRunner>() {
    override val taskerHelper by lazy { NextCommandActionHelper(this) }
    override val inputForTasker = TaskerInput(Unit)
    override fun assignFromInput(input: TaskerInput<Unit>) = Unit
}
