package com.instancify.scriptify.kts.script.bridge

import com.instancify.scriptify.api.script.Script
import com.instancify.scriptify.api.script.function.definition.ScriptFunctionDefinition
import com.instancify.scriptify.api.script.function.definition.ScriptFunctionExecutor

/**
 * This class is a bridge between Kotlin script and Scriptify.
 */
class KtsBridge(private val script: Script<*>) {

    /**
     * Call a function and get its result.
     *
     * WARNING: this method is called from the script built by KtsPreludeBuilder,
     * do not delete it, change its name or signature.
     * @see KtsPreludeBuilder
     */
    fun callFunction(functionName: String, args: Array<Any?>): Any? {
        if (script.functionManager == null || script.functionManager.functions == null) {
            throw IllegalArgumentException("No functions registered")
        }

        val definition = script.functionManager.getFunction(functionName)
            ?: throw IllegalArgumentException("Function not found: $functionName")

        val executor = findMatchingExecutor(definition, args)
        val adapted = adaptArgsForExecutor(executor, args)

        return try {
            executor.execute(script, *adapted)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    /**
     * Find a constant and get its value.
     *
     * WARNING: this method is called from the script built by KtsPreludeBuilder,
     * do not delete it, change its name or signature.
     * @see KtsPreludeBuilder
     */
    fun findConstant(constantName: String): Any? {
        val constant = script.constantManager.getConstant(constantName)
            ?: throw IllegalArgumentException("Constant not found: $constantName")
        return constant.value
    }

    private fun findMatchingExecutor(definition: ScriptFunctionDefinition, args: Array<Any?>): ScriptFunctionExecutor {
        for (executor in definition.executors) {
            val paramCount = executor.arguments.size
            val method = executor.method

            if (method.isVarArgs) {
                if (args.size >= paramCount - 1) {
                    return executor
                }
            } else {
                if (args.size == paramCount) {
                    return executor
                }
            }
        }
        throw IllegalArgumentException("No matching executor found for arguments")
    }

    private fun adaptArgsForExecutor(executor: ScriptFunctionExecutor, args: Array<Any?>): Array<Any?> {
        val method = executor.method
        val paramTypes = method.parameterTypes

        if (paramTypes.isEmpty()) {
            return emptyArray()
        }

        if (!method.isVarArgs) {
            return args
        }

        val fixedCount = paramTypes.size - 1
        val finalArgs = arrayOfNulls<Any>(paramTypes.size)

        for (i in 0 until fixedCount) {
            finalArgs[i] = if (i < args.size) args[i] else null
        }

        val varargType = paramTypes[fixedCount].componentType
        val varargCount = (args.size - fixedCount).coerceAtLeast(0)
        val varargArray = java.lang.reflect.Array.newInstance(varargType, varargCount)

        for (i in 0 until varargCount) {
            java.lang.reflect.Array.set(varargArray, i, args[fixedCount + i])
        }

        finalArgs[fixedCount] = varargArray
        return finalArgs
    }
}