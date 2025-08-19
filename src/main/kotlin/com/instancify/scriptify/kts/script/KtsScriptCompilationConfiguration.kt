package com.instancify.scriptify.kts.script

import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.providedProperties
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

object KtsScriptCompilationConfiguration : ScriptCompilationConfiguration({
    jvm {
        dependenciesFromCurrentContext(wholeClasspath = true)
    }
    providedProperties(
        "__bridge__" to KtsBridge::class
    )
}) {
    private fun readResolve(): Any = KtsScriptCompilationConfiguration
}