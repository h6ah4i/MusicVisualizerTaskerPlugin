package com.h6ah4i.android.tasker.plugin.musicvisualizer.actions

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelper
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginRunner

abstract class TaskerPluginActionConfigActivity<TInput: Any, TOutput: Any, TRunner: TaskerPluginRunner<TInput, TOutput>>: TaskerPluginConfig<TInput>, AppCompatActivity() {
    override val context: Context get() = applicationContext
    abstract val taskerHelper: TaskerPluginConfigHelper<TInput, TOutput, TRunner>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (taskerHelper.inputClass == Unit::class.java) {
            taskerHelper.finishForTasker()
            return
        }

        setContentView(onCreateContentView(savedInstanceState))
        taskerHelper.onCreate()
    }

    open fun onCreateContentView(savedInstanceState: Bundle?): View {
        throw IllegalStateException()
    }

    override fun onBackPressed() {
        taskerHelper.onBackPressed()
    }
}

abstract class TaskerPluginConfigDialogFragment: DialogFragment() {
    private var inOnDestroyView = false

    override fun onDestroyView() {
        inOnDestroyView = true
        super.onDestroyView()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (!inOnDestroyView) {
            (requireActivity() as TaskerPluginActionConfigActivity<*, *, *>).taskerHelper.finishForTasker()
        }
    }
}