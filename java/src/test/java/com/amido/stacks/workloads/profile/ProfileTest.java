package com.amido.stacks.workloads.profile;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.amido.stacks.workloads.menu.repository.MenuRepository;
import com.azure.spring.autoconfigure.cosmos.CosmosAutoConfiguration;
import com.azure.spring.autoconfigure.cosmos.CosmosHealthConfiguration;
import com.azure.spring.autoconfigure.cosmos.CosmosRepositoriesAutoConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"management.port=0"})
@EnableAutoConfiguration(
    exclude = {
      CosmosRepositoriesAutoConfiguration.class,
      CosmosAutoConfiguration.class,
      CosmosHealthConfiguration.class
    })
@Tag("Integration")
class ProfileTest {

  /*
    @Value("${spring.profiles.active:}")
    private String activeProfiles;
  */
  @Autowired private Environment env;

  @MockBean private MenuRepository menuRepository;

  @Test
  void shouldReturnprofiles() {

    String[] activeProfile = env.getActiveProfiles();
    assertEquals(99, activeProfile.length);
  }
}
