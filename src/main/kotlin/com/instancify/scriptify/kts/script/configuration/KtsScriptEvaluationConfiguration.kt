package com.instancify.scriptify.kts.script.configuration

import kotlin.script.experimental.api.ScriptEvaluationConfiguration

object KtsScriptEvaluationConfiguration : ScriptEvaluationConfiguration({

}) {
    private fun readResolve(): Any = KtsScriptEvaluationConfiguration
}