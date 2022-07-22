package com.amido.stacks.workloads.menu.api.v1;

import static com.amido.stacks.workloads.menu.domain.MenuHelper.createMenu;
import static com.amido.stacks.workloads.util.TestHelper.getBaseURL;
import static com.amido.stacks.workloads.util.TestHelper.getRequestHttpEntity;
import static com.azure.cosmos.implementation.Utils.randomUUID;
import static java.util.UUID.fromString;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import com.amido.stacks.core.api.dto.ErrorResponse;
import com.amido.stacks.core.api.dto.response.ResourceCreatedResponse;
import com.amido.stacks.core.api.dto.response.ResourceUpdatedResponse;
import com.amido.stacks.workloads.Application;
import com.amido.stacks.workloads.menu.api.v1.dto.request.CreateMenuRequest;
import com.amido.stacks.workloads.menu.api.v1.dto.request.UpdateMenuRequest;
import com.amido.stacks.workloads.menu.commands.CreateMenuCommand;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.domain.MenuHelper;
import com.amido.stacks.workloads.menu.mappers.RequestToCommandMapper;
import com.amido.stacks.workloads.menu.repository.MenuRepository;
import com.amido.stacks.workloads.menu.service.v1.MenuService;
import com.azure.spring.autoconfigure.cosmos.CosmosAutoConfiguration;
import com.azure.spring.autoconfigure.cosmos.CosmosHealthConfiguration;
import com.azure.spring.autoconfigure.cosmos.CosmosRepositoriesAutoConfiguration;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = Application.class)
@EnableAutoConfiguration(
    exclude = {
        CosmosRepositoriesAutoConfiguration.class,
        CosmosAutoConfiguration.class,
        CosmosHealthConfiguration.class
    })
@Tag("Integration")
@ActiveProfiles("test")
class MenuControllerTest {

  public static final String CREATE_MENU = "/v1/menu";
  public static final String UPDATE_MENU = "%s/v1/menu/%s";
  public static final String DELETE_MENU = "%s/v1/menu/%s";

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @MockBean
  private MenuRepository menuRepository;
  @MockBean
  private MenuService menuService;
  @Autowired
  private RequestToCommandMapper requestToCommandMapper;


  @Test
  void testCreateNewMenu() {
    // Given
    Menu m = createMenu(1);

    CreateMenuRequest request =
        new CreateMenuRequest(
            m.getName(), m.getDescription(), UUID.fromString(m.getRestaurantId()), m.getEnabled());

    doNothing().when(menuService).verifyMenuNotAlreadyExisting(any(CreateMenuCommand.class));
    when(menuRepository.findAllByRestaurantIdAndName(
        eq(m.getRestaurantId()), eq(m.getName()), any(Pageable.class)))
        .thenReturn(new PageImpl<>(Collections.emptyList()));
    when(menuRepository.save(any(Menu.class))).thenReturn(m);
    when(menuService.create(any(Menu.class))).thenReturn(
        Optional.of(m));

    // When
    var response =
        this.testRestTemplate.postForEntity(
            getBaseURL(port) + CREATE_MENU, request, ResourceCreatedResponse.class);
    // Then
    then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  @Test
  void testThrowsErrorOnExists() {
    // Given
    Menu m = createMenu(1);
    CreateMenuRequest request =
        new CreateMenuRequest(
            m.getName(), m.getDescription(), UUID.fromString(m.getRestaurantId()), m.getEnabled());
    doNothing().when(menuService).verifyMenuNotAlreadyExisting(any(CreateMenuCommand.class));

    // When
    var response =
        this.testRestTemplate.postForEntity(
            getBaseURL(port) + CREATE_MENU, request, ErrorResponse.class);

    // Then
    then(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void testWhenDescriptionNotGivenReturnsBadRequest() {
    // Given
    Menu m = createMenu(1);
    CreateMenuRequest request =
        new CreateMenuRequest(m.getName(), "", fromString(m.getRestaurantId()), m.getEnabled());

    when(menuRepository.save(any(Menu.class))).thenReturn(m);
    // When
    var response =
        this.testRestTemplate.postForEntity(
            getBaseURL(port) + CREATE_MENU, request, ErrorResponse.class);

    // Then
    then(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    then(response.getBody().getDescription())
        .isEqualTo("Invalid Request: {description=must not be blank}");
  }

  @Test
  void testWhenNoNameReturnsBadRequest() {
    // Given
    Menu m = createMenu(1);
    CreateMenuRequest request =
        new CreateMenuRequest(
            "", m.getDescription(), fromString(m.getRestaurantId()), m.getEnabled());

    when(menuRepository.save(any(Menu.class))).thenReturn(m);
    // When
    var response =
        this.testRestTemplate.postForEntity(
            getBaseURL(port) + CREATE_MENU, request, ErrorResponse.class);

    // Then
    then(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    then(response.getBody().getDescription())
        .isEqualTo("Invalid Request: {name=must not be blank}");
  }

  @Test
  void testUpdateSuccess() {
    // Given
    Menu menu = createMenu(0);
    when(menuRepository.findById(eq(menu.getId()))).thenReturn(Optional.of(menu));

    UpdateMenuRequest request = new UpdateMenuRequest("new name", "new description", false);

    // When
    var response =
        this.testRestTemplate.exchange(
            String.format(UPDATE_MENU, getBaseURL(port), menu.getId()),
            HttpMethod.PUT,
            new HttpEntity<>(request, getRequestHttpEntity()),
            ResourceUpdatedResponse.class);

    // Then
    ArgumentCaptor<Menu> captor = ArgumentCaptor.forClass(Menu.class);
    verify(menuRepository, times(1)).save(captor.capture());
    Menu updated = captor.getValue();

    then(updated.getName()).isEqualTo(request.getName());
    then(updated.getDescription()).isEqualTo(request.getDescription());
    then(updated.getEnabled()).isEqualTo(request.getEnabled());
    then(updated.getRestaurantId()).isEqualTo(menu.getRestaurantId());

    then(response).isNotNull();
    then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void testCannotUpdateIfMenuDoesntExist() {
    // Given
    UUID menuId = randomUUID();
    when(menuRepository.findById(eq(menuId.toString()))).thenReturn(Optional.empty());

    UpdateMenuRequest request = new UpdateMenuRequest("name", "description", true);

    // When
    var response =
        this.testRestTemplate.exchange(
            String.format(UPDATE_MENU, getBaseURL(port), menuId),
            HttpMethod.PUT,
            new HttpEntity<>(request, getRequestHttpEntity()),
            ErrorResponse.class);

    // Then
    then(response).isNotNull();
    then(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testUpdateMenuWithNoNameReturnsBadRequest() {
    // Given
    Menu menu = createMenu(0);
    when(menuRepository.findById(eq(menu.getId()))).thenReturn(Optional.of(menu));

    UpdateMenuRequest request = new UpdateMenuRequest("", "new description", false);

    // When
    var response =
        this.testRestTemplate.exchange(
            String.format(UPDATE_MENU, getBaseURL(port), menu.getId()),
            HttpMethod.PUT,
            new HttpEntity<>(request, getRequestHttpEntity()),
            ErrorResponse.class);

    // Then
    then(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    then(response.getBody().getDescription())
        .isEqualTo("Invalid Request: {name=must not be blank}");
  }

  @Test
  void testUpdateMenuWithNoDescriptionReturnsBadRequest() {
    // Given
    Menu menu = createMenu(0);
    when(menuRepository.findById(eq(menu.getId()))).thenReturn(Optional.of(menu));

    UpdateMenuRequest request = new UpdateMenuRequest("Updated Name", "", false);

    // When
    var response =
        this.testRestTemplate.exchange(
            String.format(UPDATE_MENU, getBaseURL(port), menu.getId()),
            HttpMethod.PUT,
            new HttpEntity<>(request, getRequestHttpEntity()),
            ErrorResponse.class);

    // Then
    then(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    then(response.getBody().getDescription())
        .isEqualTo("Invalid Request: {description=must not be blank}");
  }

  @Test
  void testDeleteMenuSuccess() {
    // Given
    Menu menu = MenuHelper.createMenu(1);
    when(menuService.findById(eq(menu.getId()))).thenReturn(Optional.of(menu));

    var response =
        this.testRestTemplate.exchange(
            String.format(DELETE_MENU, getBaseURL(port), menu.getId()),
            HttpMethod.DELETE,
            new HttpEntity<>(getRequestHttpEntity()),
            ResponseEntity.class);
    // Then
    verify(menuService, times(1)).delete(menu);
    then(response.getStatusCode()).isEqualTo(OK);
  }

  @Test
  void testDeleteMenuWithInvalidId() {
    // Given
    Menu menu = MenuHelper.createMenu(1);
    when(menuRepository.findById(eq(menu.getId()))).thenReturn(Optional.of(menu));

    var response =
        this.testRestTemplate.exchange(
            String.format(DELETE_MENU, getBaseURL(port), UUID.randomUUID().toString()),
            HttpMethod.DELETE,
            new HttpEntity<>(getRequestHttpEntity()),
            ErrorResponse.class);
    // Then
    verify(menuRepository, times(0)).delete(menu);
    then(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }
}