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

enum class ShuffleMode(
    val internalName: String,
    @StringRes val displayNameResId: Int,
    @com.h6ah4i.android.musicvisualizerapi.annotation.ShuffleMode val apiValue: Int,
) {
    TOGGLE(internalName = "Toggle", displayNameResId = R.string.shuffle_mode_display_name_toggle, apiValue = MusicVisualizerAPI.SHUFFLE_ROTATE),
    ON(internalName = "On", displayNameResId = R.string.shuffle_mode_display_name_on, apiValue = MusicVisualizerAPI.SHUFFLE_ENABLED),
    OFF(internalName = "Off", displayNameResId = R.string.shuffle_mode_display_name_off, apiValue = MusicVisualizerAPI.SHUFFLE_DISABLED),
    ;

    companion object {
        fun fromInternalName(name: String) = values().find { it.internalName == name }!!
    }
}

@TaskerInputRoot
class ShuffleCommandInput @JvmOverloads constructor(
    @field:TaskerInputField(key = "mode", labelResId = R.string.tasker_plugin_input_label_shuffle_mode)
    var mode: String = ShuffleMode.TOGGLE.internalName,
)

class ShuffleCommandRunner: MusicVisualizerServiceCommandRunner<ShuffleCommandInput>(MusicVisualizerAPI.COMMAND_SET_SHUFFLE, false) {
    override fun onCreateCommandIntent(context: Context, command: String, input: TaskerInput<ShuffleCommandInput>): Intent {
        return super.onCreateCommandIntent(context, command, input).apply {
            putExtra(MusicVisualizerAPI.EXTRA_SHUFFLE_MODE, ShuffleMode.fromInternalName(input.regular.mode).apiValue)
        }
    }
}

class ShuffleCommandActionHelper(
    config: TaskerPluginConfig<ShuffleCommandInput>,
) : TaskerPluginConfigHelper<ShuffleCommandInput, Unit, ShuffleCommandRunner>(config) {
    override val runnerClass = ShuffleCommandRunner::class.java
    override val inputClass = ShuffleCommandInput::class.java
    override val outputClass = Unit::class.java
}

class ShuffleModePickerDialogFragment: TaskerPluginConfigDialogFragment() {
    private val viewModel: ShuffleConfigActivityViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val valueItems = ShuffleMode.values().map { it.internalName }.toTypedArray()
        val displayNameItems = ShuffleMode.values().map { getString(it.displayNameResId) }.toTypedArray()
        val checkedItem = valueItems.indexOf(viewModel.mode)

        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_title_shuffle_mode))
            .setSingleChoiceItems(displayNameItems, checkedItem, null)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                val selected = (dialog as AlertDialog).listView.checkedItemPosition
                viewModel.mode = valueItems[selected]
                dismissAllowingStateLoss()
            }
            .create()
    }
}

class ShuffleConfigActivityViewModel: ViewModel() {
    var mode: String = ShuffleMode.TOGGLE.internalName
}

class ShuffleConfigAction: TaskerPluginActionConfigActivity<ShuffleCommandInput, Unit, ShuffleCommandRunner>() {
    override val taskerHelper by lazy { ShuffleCommandActionHelper(this) }
    private val binding by lazy { ActivityDialogLauncherBinding.inflate(layoutInflater) }
    private val viewModel: ShuffleConfigActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            ShuffleModePickerDialogFragment().show(supportFragmentManager, "dialog")
        }
    }

    override fun onCreateContentView(savedInstanceState: Bundle?) = binding.root

    override val inputForTasker
        get() = TaskerInput(ShuffleCommandInput(
            mode = viewModel.mode
        ))

    override fun assignFromInput(input: TaskerInput<ShuffleCommandInput>) {
        viewModel.mode = input.regular.mode
    }
}
