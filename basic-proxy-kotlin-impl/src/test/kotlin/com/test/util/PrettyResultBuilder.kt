package com.test.util

import org.objectweb.asm.ClassReader
import org.objectweb.asm.util.TraceClassVisitor
import java.io.ByteArrayOutputStream
import java.io.PrintWriter

class PrettyResultBuilder(val debug: Boolean) {

    fun build(raw: ByteArray): List<List<String>> {
        val outputStream = ByteArrayOutputStream()

        ClassReader(raw).accept(TraceClassVisitor(PrintWriter(outputStream)), 0)
        val lines = outputStream.toString().splitToSequence(System.lineSeparator()).toList()
//        val lines = outputStream.toString().split(System.lineSeparator().toList()

        if (debug) {
            println()
            lines.forEach { println(it) }
            println()
        }

        return split(lines)
    }

    private fun split(lines: List<String>): List<List<String>> {
        val result = mutableListOf<List<String>>()

        var i = 0
        while (i < lines.size) {
            var line = lines[i]
            if (!line.startsWith("  //")) {
                i++
                continue
            }

            val begin = i
            var end = i
            while (end < lines.size) {
                line = lines[end]
                if (line.isEmpty() || line == "}") {
                    break
                }
                end++
            }

            result.add(lines.subList(begin, end))
            i = end + 1
        }
        return result
    }
}