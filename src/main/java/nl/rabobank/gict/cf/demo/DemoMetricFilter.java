package nl.rabobank.gict.cf.demo;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class DemoMetricFilter implements MetricFilter {
    private static Logger LOG = LoggerFactory.getLogger(DemoMetricFilter.class);
    private Pattern pattern;

    public DemoMetricFilter(String patternString) {
        pattern = Pattern.compile(patternString);
        LOG.warn("using metricfilter " + pattern);
    }

    @Override
    public boolean matches(String name, Metric metric) {
        if (pattern.matcher(name).matches()) {
            LOG.info("reporting metric " + name);
            return true;
        }
        return false;
    }

}
