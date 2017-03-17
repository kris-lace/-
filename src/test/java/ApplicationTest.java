import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


public class ApplicationTest {

    protected static final int ES_PORT = 9200;

    private JestClient client;

    @ClassRule
    public static GenericContainer environment = new GenericContainer("elasticsearch:5.2.0").withExposedPorts(ES_PORT);

    @Before
    public void initJestClient() throws InterruptedException {
        String esUrl = "http://" + environment.getContainerIpAddress() + ":" + environment.getMappedPort(ES_PORT);
        System.out.println("ES URL = " + esUrl);

        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig.Builder(esUrl).multiThreaded(true).build());
        client = factory.getObject();
    }

    @Test
    public void canIndexAndGet() throws IOException {
        final Payload payload = new Payload("123", "some-payload");
        final Index build = createIndexForPayload(payload);
        final DocumentResult result = client.execute(build);

        assertThat(result.isSucceeded(), is(true));
        tryGetting();
    }

    private void tryGetting() throws IOException {
        // Retrieve a real object
        final Get get = new Get.Builder("test-index", "123").build();
        DocumentResult result = client.execute(get);
        assertThat(result.getId(), is("123"));

        // Shouldn't find a non-existing object
        final Get fakeGet = new Get.Builder("test-index", "234").build();
        DocumentResult fakeResult = client.execute(fakeGet);
        assertThat(fakeResult.isSucceeded(), is(false));
    }

    private static Index createIndexForPayload(Payload payload) {
        return new Index.Builder(payload)
            .index("test-index")
            .type("test-type")
            .build();
    }

}
