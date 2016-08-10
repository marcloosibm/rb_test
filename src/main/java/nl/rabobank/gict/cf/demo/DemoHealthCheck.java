package nl.rabobank.gict.cf.demo;

import com.codahale.metrics.health.HealthCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoHealthCheck extends HealthCheck {
    private static final Logger LOG = LoggerFactory.getLogger(DemoHealthCheck.class);

    @Override
    protected Result check() throws Exception {
        LOG.debug("health checked");
        return Result.healthy("all fine here");
    }
}
