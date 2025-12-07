package com.gonol.nginx;

import com.gonol.nginx.model.LogEntry;
import com.gonol.nginx.parser.LogParser;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class LogParserTest {

    @Test
    void parseValidLine() {
        String line = "192.168.1.2 - - [10/Nov/2025:10:00:01 +0100] \"GET /api/user HTTP/1.1\" 404 150 \"-\" \"curl/7.68.0\"";
        Optional<LogEntry> opt = LogParser.parseLine(line);
        assertTrue(opt.isPresent());
        LogEntry e = opt.get();
        assertEquals("192.168.1.2", e.getIp());
        assertEquals("/api/user", e.getPath());
        assertEquals(404, e.getStatus());
        assertEquals("curl/7.68.0", e.getUserAgent());
    }

    @Test
    void parseInvalidLine() {
        Optional<LogEntry> opt = LogParser.parseLine("garbage line");
        assertTrue(opt.isEmpty());
    }
}

