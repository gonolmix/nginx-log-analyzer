package com.gonol.nginx.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gonol.nginx.service.AnalyzerService.Report;

import java.io.File;
import java.io.IOException;

/**
 * Утилита для вывода JSON-отчёта.
 */
public final class JsonReportWriter {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void writeToStdout(Report report) throws IOException {
        String json = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(report);
        System.out.println(json);
    }

    public static void writeToFile(Report report, String path) throws IOException {
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(new File(path), report);
    }
}
