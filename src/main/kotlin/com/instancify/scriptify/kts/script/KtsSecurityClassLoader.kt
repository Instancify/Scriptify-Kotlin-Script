package com.instancify.scriptify.kts.script

import com.instancify.scriptify.api.script.security.ScriptSecurityManager
import com.instancify.scriptify.api.script.security.SecurityClassAccessor
import com.instancify.scriptify.api.script.security.exclude.ClassSecurityExclude
import com.instancify.scriptify.api.script.security.exclude.PackageSecurityExclude
import com.instancify.scriptify.api.script.security.exclude.SecurityExclude

/**
 * Secure class loader for Kotlin scripts to control access to classes.
 */
class KtsSecurityClassLoader(
    private val securityManager: ScriptSecurityManager
) : ClassLoader(), SecurityClassAccessor {

    private val allowedClasses: MutableSet<String> = mutableSetOf()
    private val allowedPackages: MutableSet<String> = mutableSetOf()

    init {
        for (exclude in securityManager.excludes) {
            if (exclude is ClassSecurityExclude) {
                allowedClasses.add(exclude.value)
            } else if (exclude is PackageSecurityExclude) {
                allowedPackages.add(exclude.value)
            }
        }
        addEssentialKotlinClasses()
    }

    private fun addEssentialKotlinClasses() {
        // Add packages that should be allowed by default
        allowedPackages.add("kotlin.")
        allowedPackages.add("kotlinx.")

        allowedPackages.add("java.lang.")
        allowedPackages.add("java.io.")

        allowedPackages.add("com.instancify.scriptify.kts.script.")
    }

    override fun getExcludes(): Set<SecurityExclude> {
        return securityManager.excludes
    }

    private fun isClassAllowed(className: String): Boolean {
        if (!securityManager.securityMode) {
            return true;
        }
        if (allowedClasses.contains(className)) {
            return true
        }
        for (allowedPackage in allowedPackages) {
            if (className.startsWith(allowedPackage)) {
                return true
            }
        }

        return false
    }

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        if (!this.isClassAllowed(name)) {
            throw ClassNotFoundException("Access to class $name is restricted by security policy")
        }
        return super.loadClass(name, resolve)
    }
}