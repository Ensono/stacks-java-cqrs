package com.amido.stacks.workloads.actuator;

import static com.amido.stacks.workloads.util.TestHelper.getBaseURL;
import static org.assertj.core.api.BDDAssertions.then;

import com.amido.stacks.workloads.Application;
import com.amido.stacks.workloads.menu.repository.MenuRepository;
import java.util.Map;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = Application.class)
@TestPropertySource(
    properties = {
      "management.port=0",
      "aws.xray.enabled=false",
      "aws.secretsmanager.enabled=false"
    })
@Tag("Component")
@ActiveProfiles("test")
class ActuatorTest {

  @Value("${local.management.port}")
  private int mgt;

  @Autowired private TestRestTemplate testRestTemplate;

  @MockBean private MenuRepository menuRepository;

  @Test
  void shouldReturn200WhenSendingRequestToHealthEndpoint() {

    var entity = this.testRestTemplate.getForEntity(getBaseURL(this.mgt) + "/health", Map.class);
    then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void shouldReturn200WhenSendingRequestToManagementEndpoint() {

    var entity = this.testRestTemplate.getForEntity(getBaseURL(this.mgt) + "/info", Map.class);
    then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
