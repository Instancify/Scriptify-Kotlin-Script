package com.instancify.scriptify.kts.script.configuration

import com.instancify.scriptify.api.script.security.ScriptSecurityManager
import com.instancify.scriptify.kts.script.KtsSecurityClassLoader
import com.instancify.scriptify.kts.script.bridge.KtsBridge
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.dependencies
import kotlin.script.experimental.api.providedProperties
import kotlin.script.experimental.jvm.JvmDependencyFromClassLoader
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

class KtsScriptCompilationConfiguration(
    securityManager: ScriptSecurityManager
) : ScriptCompilationConfiguration({
    jvm {
        dependencies(JvmDependencyFromClassLoader({ KtsSecurityClassLoader(securityManager) }))
        dependenciesFromCurrentContext(wholeClasspath = true)
    }
    providedProperties(
        "__bridge__" to KtsBridge::class
    )
})