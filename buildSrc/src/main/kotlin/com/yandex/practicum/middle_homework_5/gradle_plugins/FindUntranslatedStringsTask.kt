package com.yandex.practicum.middle_homework_5.gradle_plugins

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.io.path.listDirectoryEntries

abstract class FindUntranslatedStringsTask : DefaultTask() {
    @TaskAction
    fun findUntranslatedStrings() {
        val resDir = File(project.projectDir, "src/main/res")
        val folders = resDir.toPath().listDirectoryEntries("values-*")

        val strings = File(resDir, "values/strings.xml")
        val nodes = retrieveNodes(strings)
        val baseStrings = retrieveStrings(strings)
        folders.forEach { folder ->
            val localStrings = File(folder.toFile(), "strings.xml")
            val localVals = retrieveStrings(localStrings)
            val diff = baseStrings.subtract(localVals.toSet())
            if (diff.isNotEmpty()) {
                val stringBuilderErrorText =
                    StringBuilder("Missing translations").append(System.lineSeparator())
                buildList<Node> {
                    (0 until nodes!!.length).forEach {
                        if (nodes.item(it).attributes?.getNamedItem("name")?.nodeValue in diff) add(
                            nodes.item(it)
                        )
                    }
                }.forEach {
                    stringBuilderErrorText.append("=== ${it.attributes?.getNamedItem("name")?.nodeValue} ===")
                        .append(System.lineSeparator()).append(it.firstChild.nodeValue)
                        .append(System.lineSeparator())
                }
                throw GradleException(stringBuilderErrorText.toString())
            }
        }
    }

    private fun retrieveNodes(folder: File): NodeList? {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(folder)
            .getElementsByTagName("string")

    }

    private fun retrieveStrings(folder: File): List<String> {
        val stringsFromXml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(folder)
            .getElementsByTagName("string")
        return stringsFromXml.let { nodeList ->
            (0 until nodeList.length).map { i ->
                val node = nodeList.item(i)
                val name = node.attributes?.getNamedItem("name")?.nodeValue ?: ""
                name
            }
        }
    }
}