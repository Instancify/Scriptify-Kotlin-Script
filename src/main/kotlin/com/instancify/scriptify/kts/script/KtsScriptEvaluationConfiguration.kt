package com.instancify.scriptify.kts.script

import kotlin.script.experimental.api.ScriptEvaluationConfiguration

object KtsScriptEvaluationConfiguration : ScriptEvaluationConfiguration({

}) {
    private fun readResolve(): Any = KtsScriptEvaluationConfiguration
}