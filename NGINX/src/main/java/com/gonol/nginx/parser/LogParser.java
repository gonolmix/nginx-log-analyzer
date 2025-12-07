package com.gonol.nginx.parser;

import com.gonol.nginx.model.LogEntry;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for standard Nginx access.log lines.
 */
public final class LogParser {
    // RegEx supports: IP - - [date] "METHOD PATH PROTO" STATUS BYTES "referer" "userAgent"
    private static final Pattern PATTERN = Pattern.compile(
            "^(\\S+) \\S+ \\S+ \\[(.+?)] \"(\\S+) (\\S+) [^\"]+\" (\\d{3}) (\\d+|-) \"[^\"]*\" \"([^\"]*)\".*$"
    );

    public static Optional<LogEntry> parseLine(String line) {
        Matcher m = PATTERN.matcher(line);
        if (!m.matches()) return Optional.empty();
        String ip = m.group(1);
        String datetime = m.group(2);
        String method = m.group(3);
        String path = m.group(4);
        int status = Integer.parseInt(m.group(5));
        String bytesStr = m.group(6);
        long bytes = "-".equals(bytesStr) ? 0L : Long.parseLong(bytesStr);
        String userAgent = m.group(7);
        return Optional.of(new LogEntry(ip, datetime, method, path, status, bytes, userAgent));
    }
}
