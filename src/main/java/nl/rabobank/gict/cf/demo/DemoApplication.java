package nl.rabobank.gict.cf.demo;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.readytalk.metrics.StatsDReporter;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class DemoApplication extends Application<DemoConfiguration> {
    public static final String VERSION = "2016-06-24 11:41";
    private static Logger LOG = LoggerFactory.getLogger(DemoApplication.class);
    public static final String PROP_DROPWIZARD_YAML = "DROPWIZARD_YAML";


    @Override
    public void initialize(Bootstrap<DemoConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));

        // support some static content (from the assets folder)
//        bootstrap.addBundle(new AssetsBundle("/assets/favicon.ico", "/favicon.ico", "/favicon.ico","favicon-bundle"));
        bootstrap.addBundle(new AssetsBundle("/assets", "/","index.htm"));

        super.initialize(bootstrap);
        LOG.warn("initialized");
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 0) {
            System.out.println("do not specify arguments, we take the value of the envvar \"DROPWIZARD_YAML\" as the name of the dropwizard config file");
        }
        String DY = System.getenv(PROP_DROPWIZARD_YAML);
        if (DY == null) {
            System.out.println("Specify envvar " + PROP_DROPWIZARD_YAML + " to specify the dropwizard yaml config file");
            System.exit(8);
        }
        new DemoApplication().run("server", DY);
    }


    @Override
    public void run(DemoConfiguration configuration, Environment environment) throws Exception {
        // register our health check
        final DemoHealthCheck healthCheck = new DemoHealthCheck();
        environment.healthChecks().register("demo-healthcheck", healthCheck);

        // managed components (do something during start and/or stop of application)
        environment.lifecycle().manage(new DemoManaged());

        // we want to use HttpSessions
        //environment.servlets().setSessionHandler(new SessionHandler());

        final DemoResource resource = new DemoResource(configuration);
        environment.jersey().setUrlPattern("/api/*");
        environment.jersey().register(resource);

        startStatsD(environment, configuration);
    }

    private void startStatsD(Environment environment, DemoConfiguration config) {
        final MetricRegistry metricsRegistry = environment.metrics();
        String prefix;
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            prefix = "host-" + hostName;
        } catch (UnknownHostException e) {
            prefix = "unknownhost";
        }
        MetricFilter filter = new DemoMetricFilter(config.getMetricsfilterregex());
        StatsDReporter.forRegistry(metricsRegistry).
                prefixedWith(prefix).
                filter(filter).
                build(config.getMetricsaddress(),config.getMetricsport()).
                start(15, TimeUnit.SECONDS);

        LOG.warn("StatsD client started, sending data to " + config.getMetricsaddress()+":"+config.getMetricsport() + ", prefix:" + prefix);
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

}
