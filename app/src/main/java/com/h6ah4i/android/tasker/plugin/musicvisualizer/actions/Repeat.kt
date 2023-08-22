package com.h6ah4i.android.tasker.plugin.musicvisualizer.actions

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import com.h6ah4i.android.musicvisualizerapi.MusicVisualizerAPI
import com.h6ah4i.android.tasker.plugin.music_visualizer.R
import com.h6ah4i.android.tasker.plugin.music_visualizer.databinding.ActivityDialogLauncherBinding
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelper
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputField
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputRoot

enum class RepeatMode(
    val internalName: String,
    @StringRes val displayNameResId: Int,
    @com.h6ah4i.android.musicvisualizerapi.annotation.RepeatMode val apiValue: Int,
) {
    TOGGLE(internalName = "Rotate", displayNameResId = R.string.repeat_mode_display_name_rotate, apiValue = MusicVisualizerAPI.REPEAT_ROTATE),
    ALL(internalName = "All", displayNameResId = R.string.repeat_mode_display_name_all, apiValue = MusicVisualizerAPI.REPEAT_ALL),
    ONE(internalName = "One", displayNameResId = R.string.repeat_mode_display_name_one, apiValue = MusicVisualizerAPI.REPEAT_ONE),
    DISABLED(internalName = "Disabled", displayNameResId = R.string.repeat_mode_display_name_disabled, apiValue = MusicVisualizerAPI.REPEAT_DISABLED),
    ;

    companion object {
        fun fromInternalName(name: String) = entries.find { it.internalName == name }!!
    }
}

@TaskerInputRoot
class RepeatCommandInput @JvmOverloads constructor(
    @field:TaskerInputField(key = "mode", labelResIdName = "tasker_plugin_input_label_repeat_mode")
    var mode: String = RepeatMode.TOGGLE.internalName,
)

class RepeatCommandRunner: MusicVisualizerServiceCommandRunner<RepeatCommandInput>(MusicVisualizerAPI.COMMAND_SET_REPEAT, false) {
    override fun onCreateCommandIntent(context: Context, command: String, input: TaskerInput<RepeatCommandInput>): Intent {
        return super.onCreateCommandIntent(context, command, input).apply {
            putExtra(MusicVisualizerAPI.EXTRA_REPEAT_MODE, RepeatMode.fromInternalName(input.regular.mode).apiValue)
        }
    }
}

class RepeatCommandActionHelper(
    config: TaskerPluginConfig<RepeatCommandInput>,
) : TaskerPluginConfigHelper<RepeatCommandInput, Unit, RepeatCommandRunner>(config) {
    override val runnerClass = RepeatCommandRunner::class.java
    override val inputClass = RepeatCommandInput::class.java
    override val outputClass = Unit::class.java
}

class RepeatModePickerDialogFragment: TaskerPluginConfigDialogFragment() {
    private val viewModel: RepeatConfigActivityViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val valueItems = RepeatMode.entries.map { it.internalName }.toTypedArray()
        val displayNameItems = RepeatMode.entries.map { getString(it.displayNameResId) }.toTypedArray()
        val checkedItem = valueItems.indexOf(viewModel.mode)

        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_title_repeat_mode))
            .setSingleChoiceItems(displayNameItems, checkedItem, null)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                val selected = (dialog as AlertDialog).listView.checkedItemPosition
                viewModel.mode = valueItems[selected]
                dismissAllowingStateLoss()
            }
            .create()
    }
}

class RepeatConfigActivityViewModel: ViewModel() {
    var mode: String = RepeatMode.TOGGLE.internalName
}

class RepeatConfigAction: TaskerPluginActionConfigActivity<RepeatCommandInput, Unit, RepeatCommandRunner>() {
    override val taskerHelper by lazy { RepeatCommandActionHelper(this) }
    private val binding by lazy { ActivityDialogLauncherBinding.inflate(layoutInflater) }
    private val viewModel: RepeatConfigActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            RepeatModePickerDialogFragment().show(supportFragmentManager, "dialog")
        }
    }

    override fun onCreateContentView(savedInstanceState: Bundle?) = binding.root

    override val inputForTasker
        get() = TaskerInput(RepeatCommandInput(
            mode = viewModel.mode
        ))

    override fun assignFromInput(input: TaskerInput<RepeatCommandInput>) {
        viewModel.mode = input.regular.mode
    }
}
