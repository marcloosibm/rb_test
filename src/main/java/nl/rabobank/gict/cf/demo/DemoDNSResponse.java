package nl.rabobank.gict.cf.demo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DemoDNSResponse {
    @JsonProperty
    private String dc;
    @JsonProperty
    private String service;
    @JsonProperty
    private String host;
    @JsonProperty
    private String IP;
    @JsonProperty
    private int port;
    @JsonProperty
    private String status;
    @JsonProperty
    private long ttl;

    public String getDc() {
        return dc;
    }

    public void setDc(String dc) {
        this.dc = dc;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }
}
