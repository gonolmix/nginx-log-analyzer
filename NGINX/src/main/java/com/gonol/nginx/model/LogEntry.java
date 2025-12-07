package com.gonol.nginx.model;

public class LogEntry {
    private final String ip;
    private final String datetime;
    private final String method;
    private final String path;
    private final int status;
    private final long bytes;
    private final String userAgent;

    public LogEntry(String ip, String datetime, String method, String path, int status, long bytes, String userAgent) {
        this.ip = ip;
        this.datetime = datetime;
        this.method = method;
        this.path = path;
        this.status = status;
        this.bytes = bytes;
        this.userAgent = userAgent;
    }

    public String getIp() { return ip; }
    public String getDatetime() { return datetime; }
    public String getMethod() { return method; }
    public String getPath() { return path; }
    public int getStatus() { return status; }
    public long getBytes() { return bytes; }
    public String getUserAgent() { return userAgent; }
}
