package com.gonol.nginx.service;

import com.gonol.nginx.config.AppConfig;
import com.gonol.nginx.model.LogEntry;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Business logic — анализ логов: top IPs, aggregation by status, filtering by user agent.
 *
 * Все публичные методы должны иметь Javadoc (Checkstyle requirement).
 */
public class AnalyzerService {

    private final AppConfig config;
    private final List<LogEntry> entries = new ArrayList<>();

    public AnalyzerService(AppConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    /**
     * Добавить одну распаршенную запись.
     */
    public void addEntry(LogEntry entry) {
        entries.add(entry);
    }

    /**
     * Получить top N IP адресов с числом запросов.
     */
    public List<Map.Entry<String, Long>> topNips(int n) {
        Map<String, Long> counts = entries.stream()
                .collect(Collectors.groupingBy(LogEntry::getIp, Collectors.counting()));
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(n)
                .collect(Collectors.toList());
    }

    /**
     * Сгруппировать количество запросов по HTTP-кодам.
     */
    public Map<Integer, Long> aggregateByStatus() {
        return entries.stream()
                .collect(Collectors.groupingBy(LogEntry::getStatus, Collectors.counting()));
    }

    /**
     * Посчитать количество запросов от заданного User-Agent (contains).
     */
    public long countByUserAgent(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) return 0;
        return entries.stream()
                .filter(e -> e.getUserAgent() != null && e.getUserAgent().contains(userAgent))
                .count();
    }

    /**
     * Построить структуру отчёта для вывода.
     */
    public Report buildReport() {
        List<Map.Entry<String, Long>> topIps = topNips(config.getReportTopIpCount());
        Map<String, Long> topIpsMap = new LinkedHashMap<>();
        for (var e : topIps) topIpsMap.put(e.getKey(), e.getValue());

        Map<Integer, Long> status = aggregateByStatus();
        long uaHits = countByUserAgent(config.getFilterUserAgent());

        return new Report(topIpsMap, status, uaHits);
    }

    // Inner report DTO
    public static class Report {
        private final Map<String, Long> top_ips;
        private final Map<Integer, Long> status_codes;
        private final long user_agent_hits;

        public Report(Map<String, Long> top_ips, Map<Integer, Long> status_codes, long user_agent_hits) {
            this.top_ips = top_ips;
            this.status_codes = status_codes;
            this.user_agent_hits = user_agent_hits;
        }

        public Map<String, Long> getTop_ips() { return top_ips; }
        public Map<Integer, Long> getStatus_codes() { return status_codes; }
        public long getUser_agent_hits() { return user_agent_hits; }
    }
}
