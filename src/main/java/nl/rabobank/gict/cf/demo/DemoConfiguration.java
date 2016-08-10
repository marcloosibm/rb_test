package nl.rabobank.gict.cf.demo;

import io.dropwizard.Configuration;

public class DemoConfiguration extends Configuration {

    private String metricsaddress;
    private int metricsport;
    private String metricsfilterregex;

    public String getMetricsfilterregex() {        return metricsfilterregex;    }

    public void setMetricsfilterregex(String metricsfilterregex) {        this.metricsfilterregex = metricsfilterregex;    }

    public String getMetricsaddress() {
        return metricsaddress;
    }

    public void setMetricsaddress(String metricsaddress) {
        this.metricsaddress = metricsaddress;
    }

    public int getMetricsport() {
        return metricsport;
    }

    public void setMetricsport(int metricsport) {
        this.metricsport = metricsport;
    }
}
