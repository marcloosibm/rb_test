package nl.rabobank.gict.cf.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.Test;

import static io.dropwizard.testing.FixtureHelpers.fixture;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.net.InetAddress;

import static javax.ws.rs.core.HttpHeaders.ACCEPT_ENCODING;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class DemoResourceTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @ClassRule
    public static final DropwizardAppRule<DemoConfiguration> RULE =
            new DropwizardAppRule<>(DemoApplication.class, ResourceHelpers.resourceFilePath("demoapp1.yaml"));

    @Test
    public void testGet1() throws Exception {
        final Response clientResponse = ClientBuilder.newClient().target("http://localhost:" + RULE.getLocalPort() + "/api/server-info?headers&envvars")
                .request()
                .header(ACCEPT_ENCODING, "application/json")
                .get();

        assertThat(clientResponse.getStatus() == 200);

        DemoResponse expected = MAPPER.readValue(fixture("fixtures/ResponseGet1.json"), DemoResponse.class);
        expected.setHost(expected.getHost().replace("@HOST@", InetAddress.getLocalHost().getHostName()));
        DemoResponse response = clientResponse.readEntity(DemoResponse.class);
        assertEquals("wrong hitcount in json response body", expected.getHitcount(), response.getHitcount());
        assertEquals("wrong hostname in json response body", expected.getHost(), response.getHost());
    }

    @Test
    public void testGetHealth() throws Exception {
        final Response clientResponse = ClientBuilder.newClient().target("http://localhost:" + RULE.getLocalPort() + "/admin/healthcheck")
                .request()
                .header(ACCEPT_ENCODING, "application/json")
                .get();

        assertThat(clientResponse.getStatus() == 200);

        String stringResponse = clientResponse.readEntity(String.class);
        assertEquals("not healthy?","{\"deadlocks\":{\"healthy\":true},\"demo-healthcheck\":{\"healthy\":true,\"message\":\"all fine here\"}}",stringResponse);
    }
}
