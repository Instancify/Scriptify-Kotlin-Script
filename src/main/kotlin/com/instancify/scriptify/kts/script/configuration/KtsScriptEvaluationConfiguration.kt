package com.instancify.scriptify.kts.script.configuration

import com.instancify.scriptify.kts.script.bridge.KtsBridge
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.providedProperties

class KtsScriptEvaluationConfiguration(bridge: KtsBridge) : ScriptEvaluationConfiguration({
    providedProperties(mapOf("__bridge__" to bridge))
})