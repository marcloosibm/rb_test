package nl.rabobank.gict.cf.demo;

import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoManaged implements Managed{
    private  static Logger LOG = LoggerFactory.getLogger(DemoManaged.class);

    public void start() throws Exception {
        LOG.error("started");
    }

    public void stop() throws Exception {
        LOG.error("stopped");
    }
}
