package com.yandex.practicum.middle_homework_5.gradle_plugins

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class FindUntranslatedStringsTask : DefaultTask() {
    @TaskAction
    fun findUntranslatedStrings() {
        println("""Hello from FindUntranslatedStringsTask ¯\_(ツ)_/¯""")
    }
}