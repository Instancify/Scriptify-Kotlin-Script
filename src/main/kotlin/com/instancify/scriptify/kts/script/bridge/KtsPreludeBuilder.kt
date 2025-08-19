package com.instancify.scriptify.kts.script.bridge

import com.instancify.scriptify.api.script.function.definition.ScriptFunctionExecutor
import com.instancify.scriptify.kts.script.KtsScript
import org.jetbrains.annotations.Nullable

/**
 * This class collects the “head” of the script,
 * which contains all constants and functions registered in the script.
 */
object KtsPreludeBuilder {

    /**
     * Builds the script "head" from KtsScript and the script itself.
     */
    fun build(script: KtsScript, scriptCode: String): String {
        val sb = StringBuilder()

        for (constant in script.constantManager.constants.values) {
            val name = constant.name
            val type = constant.value?.let {
                convertKotlinType(it.javaClass, true)
            } ?: "Any?"
            sb.append("val $name: $type = __bridge__.findConstant(\"$name\") as $type\n")
        }

        sb.append("\n")

        for (definition in script.functionManager.functions.values) {
            val name = definition.function.name
            for (executor in definition.executors) {
                sb.append(functionSignature(name, executor)).append(" {\n")
                sb.append("    " + functionBody(executor, name)).append("\n")
                sb.append("}")
                sb.append("\n")
            }
        }

        sb.append("\n\n")
        sb.append(scriptCode)

        return sb.toString()
    }

    private fun functionSignature(name: String, executor: ScriptFunctionExecutor): String {
        val args = executor.arguments
        val params = args.mapIndexed { i, arg ->
            val lastIndex = args.size - 1
            val paramName = if (arg.name.isNullOrBlank()) "arg$i" else arg.name
            if (i == lastIndex && executor.method.isVarArgs) {
                "vararg $paramName: Any?"
            } else {
                val paramType = convertKotlinType(arg.type, arg.isRequired)
                val default = if (arg.isRequired) "" else " = null"
                "$paramName: $paramType$default"
            }
        }
        val required = !executor.method.annotatedReturnType.isAnnotationPresent(Nullable::class.java)
        return "fun $name(${params.joinToString(", ")}): ${convertKotlinType(executor.method.returnType, required)}"
    }

    private fun functionBody(ex: ScriptFunctionExecutor, name: String): String {
        val lastIndex = ex.arguments.lastIndex
        val callArgs = ex.arguments.mapIndexed { i, a ->
            val paramName = if (a.name.isNullOrBlank()) "arg$i" else a.name
            if (i == lastIndex && ex.method.isVarArgs) "*$paramName" else paramName
        }.joinToString(", ")

        val returnType = convertKotlinType(ex.method.returnType, true)
        return when {
            returnType == "Unit" -> "__bridge__.callFunction(\"$name\", kotlin.arrayOf($callArgs))"
            else -> "return __bridge__.callFunction(\"$name\", kotlin.arrayOf($callArgs)) as $returnType"
        }
    }

    private fun convertKotlinType(type: Class<*>, required: Boolean): String {
        val t = when {
            type.isArray -> when (val comp = type.componentType) {
                Object::class.java -> "Array<Any?>"
                String::class.java -> "Array<String>"
                Int::class.java, Int::class.javaPrimitiveType -> "IntArray"
                Long::class.java, Long::class.javaPrimitiveType -> "LongArray"
                Double::class.java, Double::class.javaPrimitiveType -> "DoubleArray"
                Float::class.java, Float::class.javaPrimitiveType -> "FloatArray"
                Boolean::class.java, Boolean::class.javaPrimitiveType -> "BooleanArray"
                else -> "Array<${comp.simpleName}>"
            }

            type == Void::class.java || type == Void::class.javaPrimitiveType -> "Unit"
            type == Integer::class.java || type == Int::class.javaPrimitiveType -> "Int"
            type == Long::class.java || type == Long::class.javaPrimitiveType -> "Long"
            type == Double::class.java || type == Double::class.javaPrimitiveType -> "Double"
            type == Float::class.java || type == Float::class.javaPrimitiveType -> "Float"
            type == Boolean::class.java || type == Boolean::class.javaPrimitiveType -> "Boolean"
            type == String::class.java -> "String"
            Number::class.java.isAssignableFrom(type) -> "Number"
            List::class.java.isAssignableFrom(type) -> "List<Any?>"
            Set::class.java.isAssignableFrom(type) -> "Set<Any?>"
            Map::class.java.isAssignableFrom(type) -> "Map<Any?, Any?>"
            type == Object::class.java -> "Any"
            else -> type.name
        }
        return if (required) t else "$t?"
    }
}