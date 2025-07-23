package com.test.util

class MethodLineMatcher(val prettyResult: List<List<String>>) {

    fun match(name: String, desc: String): List<String> {
        for (lines in prettyResult) {
            var i = 0
            while (i < lines.size) {
                val line = lines[i]
                if (line.startsWith("  //")) {
                    i++
                } else {
                    break
                }
            }
            if (i < lines.size && lines[i].contains(name + desc)) {
                return lines
            }
        }

        return listOf()
    }
}
