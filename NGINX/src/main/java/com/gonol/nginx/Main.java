package com.gonol.nginx;

import com.gonol.nginx.config.AppConfig;
import com.gonol.nginx.parser.LogParser;
import com.gonol.nginx.service.AnalyzerService;
import com.gonol.nginx.util.JsonReportWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Точка входа приложения Nginx Log Analyzer.
 */
public class Main {
    public static void main(String[] args) {
        try {
            AppConfig cfg = AppConfig.load(); // читает app.properties из ресурсов или из корня, см. реализацию
            AnalyzerService analyzer = new AnalyzerService(cfg);

            // читаем файл построчно как Stream<String>
            try (Stream<String> lines = Files.lines(Paths.get(cfg.getLogFilePath()))) {
                lines.map(LogParser::parseLine)
                        .filter(java.util.Optional::isPresent)
                        .map(java.util.Optional::get)
                        .forEach(analyzer::addEntry);
            }

            var report = analyzer.buildReport();
            if ("FILE".equalsIgnoreCase(cfg.getOutputTarget())) {
                JsonReportWriter.writeToFile(report, cfg.getOutputFilePath());
            } else {
                JsonReportWriter.writeToStdout(report);
            }

            System.out.println("Done.");

        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
            System.exit(2);
        } catch (Exception e) {
            System.err.println("Unhandled error: " + e.getMessage());
            System.exit(3);
        }
    }
}