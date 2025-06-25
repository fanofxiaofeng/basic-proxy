package com.test.util;

import java.util.List;

public class MethodLineMatcher {

    private final List<List<String>> prettyResult;

    public MethodLineMatcher(List<List<String>> prettyResult) {
        this.prettyResult = prettyResult;
    }

    public List<String> match(String name, String desc) {
        for (List<String> lines : prettyResult) {
            int i = 0;
            while (i < lines.size()) {
                String line = lines.get(i);
                if (line.startsWith("  //")) {
                    i++;
                } else {
                    break;
                }
            }
            if (i < lines.size() && lines.get(i).contains(name + desc)) {
                return lines;
            }
        }
        return List.of();
    }
}
