/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.config

import com.intellij.openapi.application.ApplicationManager

private val IDEA_CLASSES = listOf(
    "com.intellij.ide.IdeEventQueue",
    "com.intellij.debugger.PositionManager"
)

private const val APPLICATION_MANAGER_CLASS_NAME = "com.intellij.openapi.application.ApplicationManager"

val isJps: Boolean by lazy {
    // We run JPS tests with the whole IDEA in the classpath
    isApplicationInitialized()?.let { return@lazy !it }

    // Normally, JPS shouldn't have any of the `IDEA_CLASSES` classes in the classpath
    return@lazy !IDEA_CLASSES.all(::doesClassExists)
}

private fun isApplicationInitialized(): Boolean? {
    if (!doesClassExists(APPLICATION_MANAGER_CLASS_NAME)) {
        return null
    }

    // An initialized application say we're inside the IDEA process
    return ApplicationManager.getApplication() != null
}

private fun doesClassExists(fqName: String): Boolean {
    val classPath = fqName.replace('.', '/') + ".class"
    return {}.javaClass.classLoader.getResource(classPath) != null
}