package com.gonol.nginx.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Application configuration loaded from app.properties.
 */
public class AppConfig {
    private final String logFilePath;
    private final int reportTopIpCount;
    private final String filterUserAgent;
    private final String outputTarget;
    private final String outputFilePath;

    public AppConfig(String logFilePath, int reportTopIpCount, String filterUserAgent,
                     String outputTarget, String outputFilePath) {
        this.logFilePath = logFilePath;
        this.reportTopIpCount = reportTopIpCount;
        this.filterUserAgent = filterUserAgent;
        this.outputTarget = outputTarget;
        this.outputFilePath = outputFilePath;
    }

    /**
     * Load config: first tries file ./app.properties, then classpath resource app.properties.
     */
    public static AppConfig load() throws IOException {
        Properties p = new Properties();
        // try local file first (allows override)
        try (InputStream is = new FileInputStream("app.properties")) {
            p.load(is);
        } catch (IOException ignored) {
            // try classpath
            try (InputStream is = AppConfig.class.getClassLoader().getResourceAsStream("app.properties")) {
                if (is == null) {
                    throw new IOException("app.properties not found in working directory or classpath");
                }
                p.load(is);
            }
        }

        String logFilePath = p.getProperty("log.file.path", "./data/access.log");
        int top = Integer.parseInt(p.getProperty("report.top.ip.count", "10"));
        String ua = p.getProperty("filter.user.agent", "");
        String out = p.getProperty("output.target", "STDOUT");
        String outPath = p.getProperty("output.file.path", "./data/report.json");

        return new AppConfig(logFilePath, top, ua, out, outPath);
    }

    public String getLogFilePath() { return logFilePath; }
    public int getReportTopIpCount() { return reportTopIpCount; }
    public String getFilterUserAgent() { return filterUserAgent; }
    public String getOutputTarget() { return outputTarget; }
    public String getOutputFilePath() { return outputFilePath; }
}
