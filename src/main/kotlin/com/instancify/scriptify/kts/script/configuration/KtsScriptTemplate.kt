package com.instancify.scriptify.kts.script.configuration

import com.instancify.scriptify.kts.script.bridge.KtsBridge
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(
    displayName = "Scriptify KTS Script",
    fileExtension = "kts",
    compilationConfiguration = KtsScriptCompilationConfiguration::class,
    evaluationConfiguration = KtsScriptEvaluationConfiguration::class
)
abstract class KtsScriptTemplate {
    abstract val __bridge__: KtsBridge
}