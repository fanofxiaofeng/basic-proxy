package com.test.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LineReader {

    private final String fileName;

    LineReader(String fileName) {
        this.fileName = fileName;
    }

    public List<String> readLines() throws IOException {
        List<String> result;

        if (!new File(fileName).exists()) {
            throw new RuntimeException("file doesn't exist!");
        }

        try (FileInputStream f = new FileInputStream(fileName)) {
            String[] lines = new String(f.readAllBytes()).split(System.lineSeparator());
            result = Arrays.stream(lines).collect(Collectors.toList());
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        LineReader lineReader = new LineReader("a.txt");
        for (String line : lineReader.readLines()) {
            System.out.println(line);
        }
    }
}
