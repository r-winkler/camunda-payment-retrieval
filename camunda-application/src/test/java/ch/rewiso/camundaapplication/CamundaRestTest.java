package ch.rewiso.camundaapplication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * start full server on random port
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CamundaRestTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void smokeTestRestEndpoint() {

        ResponseEntity<String> response1 = restTemplate.getForEntity("/rest/process-definition", String.class);
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> response2 = restTemplate.getForEntity("/rest/engine/", String.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
