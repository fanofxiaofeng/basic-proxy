package com.test.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PrettyResultBuilder {

    private final boolean debug;

    public PrettyResultBuilder(boolean debug) {
        this.debug = debug;
    }

    public List<List<String>> build(byte[] raw) {
        OutputStream outputStream = new ByteArrayOutputStream();

        new ClassReader(raw).accept(new TraceClassVisitor(new PrintWriter(outputStream)), 0);
        List<String> lines =
                Arrays.stream(outputStream.toString().split(System.lineSeparator())).
                        collect(Collectors.toList());

        if (debug) {
            System.out.println();
            lines.forEach(System.out::println);
            System.out.println();
        }

        return split(lines);
    }

    private List<List<String>> split(List<String> lines) {
        List<List<String>> result = new ArrayList<>();

        int i = 0;
        while (i < lines.size()) {
            String line = lines.get(i);
            if (!line.startsWith("  //")) {
                i++;
                continue;
            }

            int begin = i;
            int end = i;
            while (end < lines.size()) {
                line = lines.get(end);
                if (line.isEmpty() || line.equals("}")) {
                    break;
                }
                end++;
            }

            result.add(lines.subList(begin, end));
            i = end + 1;
        }
        return result;
    }
}
