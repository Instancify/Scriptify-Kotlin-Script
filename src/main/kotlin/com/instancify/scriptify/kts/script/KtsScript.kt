package com.instancify.scriptify.kts.script

import com.instancify.scriptify.api.exception.ScriptException
import com.instancify.scriptify.api.script.Script
import com.instancify.scriptify.api.script.constant.ScriptConstantManager
import com.instancify.scriptify.api.script.function.ScriptFunctionManager
import com.instancify.scriptify.api.script.security.ScriptSecurityManager
import com.instancify.scriptify.core.script.constant.StandardConstantManager
import com.instancify.scriptify.core.script.function.StandardFunctionManager
import com.instancify.scriptify.core.script.security.StandardSecurityManager
import com.instancify.scriptify.kts.script.bridge.KtsBridge
import com.instancify.scriptify.kts.script.bridge.KtsPreludeBuilder
import com.instancify.scriptify.kts.script.configuration.KtsScriptCompilationConfiguration
import com.instancify.scriptify.kts.script.configuration.KtsScriptEvaluationConfiguration
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.StringScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

/**
 * Implementation of Scriptify for Kotlin Script.
 */
class KtsScript : Script<EvaluationResult?> {

    private val securityManager: ScriptSecurityManager = StandardSecurityManager()
    private var functionManager: ScriptFunctionManager = StandardFunctionManager()
    private var constantManager: ScriptConstantManager = StandardConstantManager()
    private val extraScript = mutableListOf<String>()

    override fun getSecurityManager() = securityManager

    override fun getFunctionManager() = functionManager

    override fun getConstantManager() = constantManager

    override fun setFunctionManager(functionManager: ScriptFunctionManager) {
        this.functionManager = functionManager
    }

    override fun setConstantManager(constantManager: ScriptConstantManager) {
        this.constantManager = constantManager
    }

    override fun addExtraScript(script: String) {
        extraScript.add(script)
    }

    @Throws(ScriptException::class)
    override fun eval(script: String): EvaluationResult? {
        val host = BasicJvmScriptingHost()
        val bridge = KtsBridge(this)

        val source = StringScriptSource(KtsPreludeBuilder.build(this, buildString {
            // Building full script including extra script code
            for (extra in extraScript) {
                append("$extra\n")
            }
            append(script)
        }))

        val result = host.eval(
            source,
            KtsScriptCompilationConfiguration(securityManager),
            KtsScriptEvaluationConfiguration(bridge)
        )

        if (result is ResultWithDiagnostics.Success) {
            if (result.value.returnValue is ResultValue.Error) {
                throw ScriptException((result.value.returnValue as ResultValue.Error).error)
            }
            return result.value
        }

        result.reports.forEach {
            if (it.isError()) {
                throw if (it.exception != null) {
                    ScriptException(it.exception)
                } else {
                    ScriptException(it.message)
                }
            }
        }
        return null
    }
}