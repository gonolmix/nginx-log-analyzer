package com.gonol.nginx;

import com.gonol.nginx.config.AppConfig;
import com.gonol.nginx.model.LogEntry;
import com.gonol.nginx.service.AnalyzerService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AnalyzerServiceTest {

    private AnalyzerService makeService() {
        AppConfig cfg = new AppConfig("./data/access.log", 2, "curl/7.68.0", "STDOUT", "./data/report.json");
        return new AnalyzerService(cfg);
    }

    @Test
    void topIpsAndStatusAggregation() {
        AnalyzerService s = makeService();
        s.addEntry(new LogEntry("1.1.1.1","d","GET","/a",200,100,"curl"));
        s.addEntry(new LogEntry("2.2.2.2","d","GET","/b",404,100,"ua"));
        s.addEntry(new LogEntry("1.1.1.1","d","GET","/c",200,100,"curl"));

        var top = s.topNips(2);
        assertEquals(2, top.size());
        assertEquals("1.1.1.1", top.get(0).getKey());
        Map<Integer, Long> agg = s.aggregateByStatus();
        assertEquals(2L, agg.get(200));
        assertEquals(1L, agg.get(404));
    }

    @Test
    void countByUserAgent() {
        AnalyzerService s = makeService();
        s.addEntry(new LogEntry("1.1.1.1","d","GET","/a",200,100,"curl/7.68.0"));
        s.addEntry(new LogEntry("2.2.2.2","d","GET","/b",200,100,"Mozilla/5.0"));
        assertEquals(1L, s.countByUserAgent("curl/7.68.0"));
    }
}
