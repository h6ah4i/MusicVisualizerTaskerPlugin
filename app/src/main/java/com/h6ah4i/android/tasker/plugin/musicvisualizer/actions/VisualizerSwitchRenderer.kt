package com.h6ah4i.android.tasker.plugin.musicvisualizer.actions

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import com.h6ah4i.android.musicvisualizerapi.MusicVisualizerAPI
import com.h6ah4i.android.tasker.plugin.music_visualizer.BuildConfig
import com.h6ah4i.android.tasker.plugin.music_visualizer.R
import com.h6ah4i.android.tasker.plugin.music_visualizer.databinding.ActivityDialogLauncherBinding
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerAction
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelper
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputField
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputRoot
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultError
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginRunner

enum class SwitchRendererMode(
    val internalName: String,
    @StringRes val displayNameResId: Int,
    val apiValue: String,
) {
    OpenSelectorDialog(internalName = "OpenSelectorDialog", displayNameResId = R.string.switch_renderer_open_selector_dialog, apiValue = "*open dialog*"),
    Randomized(internalName = MusicVisualizerAPI.RENDERER_RANDOMIZED, displayNameResId = R.string.renderer_randomized, apiValue = MusicVisualizerAPI.RENDERER_RANDOMIZED),
    Waveform(internalName = MusicVisualizerAPI.RENDERER_WAVEFORM, displayNameResId = R.string.renderer_waveform, apiValue = MusicVisualizerAPI.RENDERER_WAVEFORM),
    ShinyParticles(internalName = MusicVisualizerAPI.RENDERER_SHINY_PARTICLES, displayNameResId = R.string.renderer_shiny_particles, apiValue = MusicVisualizerAPI.RENDERER_SHINY_PARTICLES),
    NoiseFlow(internalName = MusicVisualizerAPI.RENDERER_NOISE_FLOW, displayNameResId = R.string.renderer_noise_flow, apiValue = MusicVisualizerAPI.RENDERER_NOISE_FLOW),
    ColorfulOrb(internalName = MusicVisualizerAPI.RENDERER_COLORFUL_ORB, displayNameResId = R.string.renderer_colorful_orb, apiValue = MusicVisualizerAPI.RENDERER_COLORFUL_ORB),
    SimpleBars(internalName = MusicVisualizerAPI.RENDERER_SIMPLE_BARS, displayNameResId = R.string.renderer_simple_bars, apiValue = MusicVisualizerAPI.RENDERER_SIMPLE_BARS),
    HeartBeats(internalName = MusicVisualizerAPI.RENDERER_HEART_BEATS, displayNameResId = R.string.renderer_heart_beats, apiValue = MusicVisualizerAPI.RENDERER_HEART_BEATS),
    Laser(internalName = MusicVisualizerAPI.RENDERER_LASER, displayNameResId = R.string.renderer_laser, apiValue = MusicVisualizerAPI.RENDERER_LASER),
    DigitalEqualizer(internalName = MusicVisualizerAPI.RENDERER_DIGITAL_EQUALIZER, displayNameResId = R.string.renderer_digital_equalizer, apiValue = MusicVisualizerAPI.RENDERER_DIGITAL_EQUALIZER),
    HexTiles(internalName = MusicVisualizerAPI.RENDERER_HEX_TILES, displayNameResId = R.string.renderer_hex_tiles, apiValue = MusicVisualizerAPI.RENDERER_HEX_TILES),
    EnergySphere(internalName = MusicVisualizerAPI.RENDERER_ENERGY_SPHERE, displayNameResId = R.string.renderer_energy_sphere, apiValue = MusicVisualizerAPI.RENDERER_ENERGY_SPHERE),
    AlbumArt(internalName = MusicVisualizerAPI.RENDERER_ALBUM_ART, displayNameResId = R.string.renderer_album_art, apiValue = MusicVisualizerAPI.RENDERER_ALBUM_ART),
    SwitchSequentially(internalName = "SwitchSequentially", displayNameResId = R.string.switch_renderer_sequentially, apiValue = MusicVisualizerAPI.RENDERER_SELECT_SEQUENTIAL),
    SwitchRandomly(internalName = "SwitchRandomly", displayNameResId = R.string.switch_renderer_randomly, apiValue = MusicVisualizerAPI.RENDERER_SELECT_RANDOM),
    ;

    companion object {
        fun fromInternalName(name: String) = values().find { it.internalName == name } ?: throw IllegalArgumentException("Unknown renderer name specified: $name")
    }
}

//
// Input
//
@TaskerInputRoot
open class SwitchRendererCommandInput @JvmOverloads constructor(
    @field:TaskerInputField(key = "mode", labelResId = R.string.tasker_plugin_input_label_renderer_type)
    var mode: String = SwitchRendererMode.OpenSelectorDialog.internalName,
)

//
// Runner
//
abstract class AbstractSwitchRendererCommandRunner: TaskerPluginRunnerAction<SwitchRendererCommandInput, Unit>() {
    abstract val targetVisualizer: String

    override fun run(context: Context, input: TaskerInput<SwitchRendererCommandInput>): TaskerPluginResult<Unit> {
        return if (input.regular.mode == SwitchRendererMode.OpenSelectorDialog.internalName) {
                val uri = Uri.Builder()
                    .scheme(BuildConfig.TASKER_CALLBACK_SCHEME)
                    .authority("visualizer-switch-renderer-trampoline")
                    .appendQueryParameter("visualizer", targetVisualizer)
                    .build()
                TaskerPluginResultSucess(callbackUri = uri)
            } else {
                val intent = MusicVisualizerAPI.createVisualizerSwitchRendererBroadcastIntent(
                    context, targetVisualizer, SwitchRendererMode.fromInternalName(input.regular.mode).apiValue)
                context.sendBroadcast(intent)
                TaskerPluginResultSucess()
            }
    }
}

class MainAppSwitchRendererCommandRunner: AbstractSwitchRendererCommandRunner() {
    override val targetVisualizer = MusicVisualizerAPI.VISUALIZER_MAIN_APP
}

class LiveWallpaperSwitchRendererCommandRunner: AbstractSwitchRendererCommandRunner() {
    override val targetVisualizer = MusicVisualizerAPI.VISUALIZER_LIVE_WALLPAPER
}

class ScreensaverSwitchRendererCommandRunner: AbstractSwitchRendererCommandRunner() {
    override val targetVisualizer = MusicVisualizerAPI.VISUALIZER_SCREENSAVER
}

//
// Helper
//
abstract class AbstractSwitchRendererCommandActionHelper<TRunner: TaskerPluginRunner<SwitchRendererCommandInput, Unit>>(
    config: TaskerPluginConfig<SwitchRendererCommandInput>,
) : TaskerPluginConfigHelper<SwitchRendererCommandInput, Unit, TRunner>(config) {
    override val inputClass = SwitchRendererCommandInput::class.java
    override val outputClass = Unit::class.java
    abstract val supportsOpenSelectorDialog: Boolean
}

class MainAppSwitchRendererCommandActionHelper(config: TaskerPluginConfig<SwitchRendererCommandInput>) : AbstractSwitchRendererCommandActionHelper<MainAppSwitchRendererCommandRunner>(config) {
    override val runnerClass = MainAppSwitchRendererCommandRunner::class.java
    override val supportsOpenSelectorDialog = true
}

class LiveWallpaperSwitchRendererCommandActionHelper(config: TaskerPluginConfig<SwitchRendererCommandInput>) : AbstractSwitchRendererCommandActionHelper<LiveWallpaperSwitchRendererCommandRunner>(config) {
    override val runnerClass = LiveWallpaperSwitchRendererCommandRunner::class.java
    override val supportsOpenSelectorDialog = true
}

class ScreensaverSwitchRendererCommandActionHelper(config: TaskerPluginConfig<SwitchRendererCommandInput>) : AbstractSwitchRendererCommandActionHelper<ScreensaverSwitchRendererCommandRunner>(config) {
    override val runnerClass = ScreensaverSwitchRendererCommandRunner::class.java
    override val supportsOpenSelectorDialog = false
}

//
// Action
//
class SwitchRendererConfigActivityViewModel: ViewModel() {
    var supportsOpenSelectorDialog: Boolean = true
    var type: SwitchRendererMode = SwitchRendererMode.OpenSelectorDialog
}

class SwitchRendererModePickerDialogFragment: TaskerPluginConfigDialogFragment() {
    private val viewModel: SwitchRendererConfigActivityViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val valueItems = SwitchRendererMode.values()
        val displayNameItems = SwitchRendererMode.values().map { getString(it.displayNameResId) }.toTypedArray()
        val checkedItem = valueItems.indexOf(viewModel.type)

        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_title_renderer_type))
            .setSingleChoiceItems(displayNameItems, checkedItem, null)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                val selected = (dialog as AlertDialog).listView.checkedItemPosition
                viewModel.type = valueItems[selected]
                dismissAllowingStateLoss()
            }
            .create()
    }
}

abstract class AbstractSwitchRendererConfigAction<TRunner: TaskerPluginRunner<SwitchRendererCommandInput, Unit>>: TaskerPluginActionConfigActivity<SwitchRendererCommandInput, Unit, TRunner>() {
    private val binding by lazy { ActivityDialogLauncherBinding.inflate(layoutInflater) }
    private val viewModel: SwitchRendererConfigActivityViewModel by viewModels()
    private val supportsOpenSelectorDialog
        get() = (taskerHelper as AbstractSwitchRendererCommandActionHelper).supportsOpenSelectorDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(SwitchRendererModePickerDialogFragment(), "dialog")
                .commit()
        }
    }

    override fun onCreateContentView(savedInstanceState: Bundle?) = binding.root

    override val inputForTasker
        get() = TaskerInput(SwitchRendererCommandInput(
            mode = viewModel.type.internalName
        ))

    override fun assignFromInput(input: TaskerInput<SwitchRendererCommandInput>) {
        val defaultType = if (supportsOpenSelectorDialog) SwitchRendererMode.OpenSelectorDialog else SwitchRendererMode.Randomized

        viewModel.supportsOpenSelectorDialog = supportsOpenSelectorDialog
        viewModel.type = SwitchRendererMode.values().find { it.internalName == input.regular.mode } ?: defaultType
    }
}

class MainAppSwitchRendererConfigAction: AbstractSwitchRendererConfigAction<MainAppSwitchRendererCommandRunner>() {
    override val taskerHelper by lazy { MainAppSwitchRendererCommandActionHelper(this) }
}

class LiveWallpaperSwitchRendererConfigAction: AbstractSwitchRendererConfigAction<LiveWallpaperSwitchRendererCommandRunner>() {
    override val taskerHelper by lazy { LiveWallpaperSwitchRendererCommandActionHelper(this) }
}

class ScreensaverSwitchRendererConfigAction: AbstractSwitchRendererConfigAction<ScreensaverSwitchRendererCommandRunner>() {
    override val taskerHelper by lazy { ScreensaverSwitchRendererCommandActionHelper(this) }
}

//
// Trampoline Activity
//
class VisualizerSwitchRendererTrampolineActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val visualizer = intent.data!!.getQueryParameter("visualizer")

        startActivity(MusicVisualizerAPI.createVisualizerRendererSelectorActivityIntent(this, visualizer))
        finish()
    }
}
