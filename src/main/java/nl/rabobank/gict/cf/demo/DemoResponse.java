package nl.rabobank.gict.cf.demo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DemoResponse {
    @JsonProperty
    private String host;
    @JsonProperty
    private String servertime;
    @JsonProperty
    private String appversion;
    @JsonProperty
    private long hitcount;
    @JsonProperty
    private String loadtime;
    @JsonProperty
    private String requesturl;
    @JsonProperty
    private String remotehost;
    @JsonProperty
    private List<String> headers;
    @JsonProperty
    private List<String> envvars;
    @JsonProperty
    private String connectResult;

    @JsonProperty
    public long getHitcount() {
        return hitcount;
    }

    public DemoResponse setHitcount(long hitcount) {
        this.hitcount = hitcount;
        return this;
    }

    @JsonProperty
    public String getHost() {
        return host;
    }

    public DemoResponse setHost(String host) {
        this.host = host;
        return this;
    }

    public DemoResponse setServertime(String servertime) {
        this.servertime = servertime;
        return this;
    }

    public DemoResponse setAppversion(String appversion) {
        this.appversion = appversion;
        return this;
    }

    public DemoResponse setLoadtime(String loadtime) {
        this.loadtime = loadtime;
        return this;
    }

    public DemoResponse setRequesturl(String requesturl) {
        this.requesturl = requesturl;
        return this;
    }

    public DemoResponse setRemotehost(String remotehost) {
        this.remotehost = remotehost;
        return this;
    }

    public DemoResponse setHeaders(List<String> headers) {
        this.headers = headers;
        return this;
    }

    public DemoResponse setEnvvars(List<String> envvars) {
        this.envvars = envvars;
        return this;
    }

    public String getConnectResult() {
        return connectResult;
    }

    public void setConnectResult(String connectResult) {
        this.connectResult = connectResult;
    }

    @Override
    public String toString() {
        return "DemoResponse{" +
                "host='" + host + '\'' +
                ", servertime='" + servertime + '\'' +
                ", appversion='" + appversion + '\'' +
                ", hitcount=" + hitcount +
                ", loadtime='" + loadtime + '\'' +
                ", requesturl='" + requesturl + '\'' +
                ", remotehost='" + remotehost + '\'' +
                ", headers=" + headers +
                ", envvars=" + envvars +
                ", connectResult=" + connectResult +
                '}';
    }
}
