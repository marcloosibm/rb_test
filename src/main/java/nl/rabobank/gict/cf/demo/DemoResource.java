package nl.rabobank.gict.cf.demo;

import com.codahale.metrics.annotation.Timed;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Enumeration;


@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DemoResource {
    private static final Logger LOG = LoggerFactory.getLogger(DemoResource.class);
    private long hitcount;
    DemoConfiguration config;
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String DC1 = "best";
    public static final String DC2 = "boxtel";
    public static final int DNSPORT = 8600;
    private static final long loadTime = System.currentTimeMillis();

    public DemoResource(DemoConfiguration config) {
        this.config = config;
        LOG.info("constructed");
    }

    @GET
    @Path("/dns")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DemoDNSResponse> doDNSLookup(@QueryParam("servicename") String servicename) throws Exception {
        List<DemoDNSResponse> responses = new ArrayList<>();
        for (String DC : new String[]{DC2, DC1}) {
            String query = servicename + ".service." + DC + ".consul";
            Lookup lookup = new Lookup(query, Type.SRV);
            Resolver resolver = new SimpleResolver("localhost");
            resolver.setPort(DNSPORT);
            resolver.setTCP(true); // or your results will be limited to 3 
            lookup.setResolver(resolver);
            Record[] records = lookup.run();
            if (records != null) {
                for (Record record : records) {
                    DemoDNSResponse response = new DemoDNSResponse();
                    response.setDc(DC);
                    response.setService(servicename);
                    SRVRecord srv = (SRVRecord) record;
                    response.setTtl(srv.getTTL());
                    response.setPort(srv.getPort());
                    String hostname = srv.getTarget().toString();
                    response.setHost(hostname);
                    String status = "unknown";
                    try {
                        // now try to get the IP address
                        Lookup lkup = new Lookup(srv.getTarget(), Type.A);
                        lkup.setResolver(resolver);
                        Record[] rcords = lkup.run();
                        // should always return 1 record
                        ARecord aRecord = (ARecord) rcords[0];
                        response.setIP(aRecord.getAddress().getHostAddress());
                        // try to open the port
                        Socket socket = new Socket(aRecord.getAddress(), srv.getPort());
                        status = "port open";
                    } catch (IOException e) {
                        status = e.toString();
                        LOG.error("exception: " + e);
                        e.printStackTrace();
                    }
                    response.setStatus(status);
                    responses.add(response);
                }
            } else {
                LOG.error("DNS lookup for query " + query + " gave zero results");
            }
        }

        return responses;
    }

    @GET
    @Timed
    @Path("/server-info")
    public DemoResponse getServerInfo(@Context HttpServletRequest request) throws UnknownHostException {
        DemoResponse demoResponse = new DemoResponse()
                .setHitcount(++hitcount)
                .setHost(InetAddress.getLocalHost().getHostName())
                .setServertime(df.format(System.currentTimeMillis()))
                .setLoadtime(df.format(loadTime))
                .setAppversion(DemoApplication.VERSION)
                .setRequesturl(request.getRequestURL().toString())
                .setRemotehost(request.getRemoteHost());

        appendHeaders(request, demoResponse);
        appendEnvvars(request, demoResponse);
        appendConnectTo(request, demoResponse);
        return demoResponse;
    }

    private void appendHeaders(HttpServletRequest request, DemoResponse demoResponse) {
        String headersRequested = request.getParameter("headers");
        List<String> headers = new ArrayList<>();
        if (headersRequested != null) {
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers.add(headerName + ":" + request.getHeader(headerName));
            }
        } else {
            headers.add("specify the headers query parameter to get a dump of the request headers");
        }
        demoResponse.setHeaders(headers);
    }

    private void appendEnvvars(HttpServletRequest request, DemoResponse demoResponse) {
        String envvarsRequested = request.getParameter("envvars");
        List<String> envvars = new ArrayList<>();
        Map envvarsMap = System.getenv();
        if (envvarsRequested != null) {
            Set envKeys = envvarsMap.keySet();
            for (Object envKey : envKeys) {
                envvars.add(envKey + ":" + envvarsMap.get(envKey));
            }
        } else {
            envvars.add("specify the envvars query parameter to get a dump of the envvars");
        }
        demoResponse.setEnvvars(envvars);
    }

    private void appendConnectTo(HttpServletRequest request, DemoResponse demoResponse) {
        String connectTo = request.getParameter("connect");
        if (connectTo != null) {
            String server = StringUtils.substringBefore(connectTo, ":");
            String port = StringUtils.substringAfter(connectTo, ":");
            String inetaddress = "unknown";
            try {
                // try to open the host:port
                LOG.info("trying to connect to " + connectTo);
                Socket socket = new Socket(server, Integer.valueOf(port));
                socket.close();
                inetaddress = InetAddress.getByName(server).getHostAddress();
                demoResponse.setConnectResult("connect successful to " + connectTo + " (" + inetaddress + ")");
            } catch (Exception e) {
                LOG.error(e.getMessage());
                demoResponse.setConnectResult("connect to " + connectTo + " (" + inetaddress + ") failed: " + e.getMessage());
            }
        } else {
            demoResponse.setConnectResult("specify the connect query parameter to test a port-connect to another server (format host:port)");
        }
    }
}
