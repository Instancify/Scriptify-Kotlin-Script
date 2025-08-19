package com.instancify.scriptify.kts.script

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