package com.amido.stacks.workloads.menu.api.v1;

import static com.amido.stacks.workloads.menu.domain.CategoryHelper.createCategories;
import static com.amido.stacks.workloads.menu.domain.CategoryHelper.createCategory;
import static com.amido.stacks.workloads.menu.domain.ItemHelper.createItem;
import static com.amido.stacks.workloads.menu.domain.MenuHelper.createMenu;
import static com.amido.stacks.workloads.util.TestHelper.getBaseURL;
import static com.amido.stacks.workloads.util.TestHelper.getRequestHttpEntity;
import static com.azure.cosmos.implementation.Utils.randomUUID;
import static java.util.UUID.fromString;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import com.amido.stacks.core.api.dto.ErrorResponse;
import com.amido.stacks.core.api.dto.response.ResourceCreatedResponse;
import com.amido.stacks.core.api.dto.response.ResourceUpdatedResponse;
import com.amido.stacks.workloads.menu.api.v1.dto.request.CreateCategoryRequest;
import com.amido.stacks.workloads.menu.api.v1.dto.request.UpdateCategoryRequest;
import com.amido.stacks.workloads.menu.domain.Category;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.service.v1.MenuService;
import com.azure.spring.autoconfigure.cosmos.CosmosAutoConfiguration;
import com.azure.spring.autoconfigure.cosmos.CosmosHealthConfiguration;
import com.azure.spring.autoconfigure.cosmos.CosmosRepositoriesAutoConfiguration;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(
    exclude = {
      CosmosRepositoriesAutoConfiguration.class,
      CosmosAutoConfiguration.class,
      CosmosHealthConfiguration.class
    })
@Tag("Integration")
@ActiveProfiles("test")
class CategoryControllerTest {

  public static final String CREATE_CATEGORY = "%s/v1/menu/%s/category";
  public static final String UPDATE_CATEGORY = "%s/v1/menu/%s/category/%s";
  public static final String DELETE_CATEGORY = "%s/v1/menu/%s/category/%s";

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate testRestTemplate;

  @MockBean private MenuService menuService;

  @Test
  void testCanNotAddCategoryIfMenuNotPresent() {
    // Given
    UUID menuId = randomUUID();
    when(menuService.findById(eq(menuId.toString()))).thenReturn(Optional.empty());

    CreateCategoryRequest request =
        new CreateCategoryRequest("test Category Name", "test Category Description");

    // When
    var response =
        this.testRestTemplate.postForEntity(
            String.format(CREATE_CATEGORY, getBaseURL(port), menuId), request, ErrorResponse.class);

    // Then
    then(response).isNotNull();
    then(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testNoDescriptionGivenReturnsBadRequest() {
    // Given
    CreateCategoryRequest request = new CreateCategoryRequest("test Category Name", "");

    // When
    var response =
        this.testRestTemplate.postForEntity(
            String.format(CREATE_CATEGORY, getBaseURL(port), randomUUID()),
            request,
            ErrorResponse.class);

    // Then
    then(response).isNotNull();
    then(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    then(response.getBody().getDescription())
        .isEqualTo("Invalid Request: {description=must not be blank}");
  }

  @Test
  void testInvalidMenuIdWilThrowBadRequest() {
    // Given
    CreateCategoryRequest request = new CreateCategoryRequest("test Category Name", "");

    // When
    var response =
        this.testRestTemplate.postForEntity(
            String.format(CREATE_CATEGORY, getBaseURL(port), "XXXXXX"),
            request,
            ErrorResponse.class);

    // Then
    then(response).isNotNull();
    then(response.getStatusCode()).isEqualTo(BAD_REQUEST);
  }

  @Test
  void testAddCategory() {
    // Given
    Menu menu = createMenu(1);
    when(menuService.findById(menu.getId())).thenReturn(Optional.of(menu));
    when(menuService.create(any(Menu.class))).thenReturn(Optional.of(menu));

    CreateCategoryRequest request =
        new CreateCategoryRequest("test Category Name", "test Category Description");

    // When
    var response =
        this.testRestTemplate.postForEntity(
            String.format(CREATE_CATEGORY, getBaseURL(port), menu.getId()),
            request,
            ResourceCreatedResponse.class);

    // Then
    ArgumentCaptor<Menu> captor = ArgumentCaptor.forClass(Menu.class);
    verify(menuService, times(1)).create(captor.capture());
    Menu created = captor.getValue();

    then(created.getName()).isEqualTo(menu.getName());
    then(created.getDescription()).isEqualTo(menu.getDescription());
    then(created.getCategories().size()).isEqualTo(1);
    then(created.getCategories().get(0).getName()).isEqualTo(request.getName());
    then(created.getCategories().get(0).getDescription()).isEqualTo(request.getDescription());

    then(response).isNotNull();
    then(response.getBody()).isNotNull();
    then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    then(created.getCategories().get(0).getId()).isEqualTo(response.getBody().getId().toString());
  }

  @Test
  void testCannotAddCategoryWhichAlreadyExists() {
    // Given
    Menu menu = createMenu(1);
    Category category =
        new Category(
            UUID.randomUUID().toString(), "cat name", "cat description", new ArrayList<>());
    menu.addOrUpdateCategory(category);

    when(menuService.findById(eq(menu.getId()))).thenReturn(Optional.of(menu));

    CreateCategoryRequest request =
        new CreateCategoryRequest(category.getName(), "test Category Description");

    // When
    var response =
        this.testRestTemplate.postForEntity(
            String.format(CREATE_CATEGORY, getBaseURL(port), menu.getId()),
            request,
            ErrorResponse.class);

    // Then
    then(response).isNotNull();
    then(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void testNoNameGivenReturnsBadRequest() {
    // Given
    CreateCategoryRequest request = new CreateCategoryRequest("", "Some description");

    // When
    var response =
        this.testRestTemplate.postForEntity(
            String.format(CREATE_CATEGORY, getBaseURL(port), randomUUID()),
            request,
            ErrorResponse.class);

    // Then
    then(response).isNotNull();
    then(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    then(response.getBody().getDescription())
        .isEqualTo("Invalid Request: {name=must not be blank}");
  }

  @Test
  void testUpdateCategorySuccess() {
    // Given
    Menu menu = createMenu(0);
    Category category = createCategory(0);
    menu.addOrUpdateCategory(category);
    when(menuService.findById(eq(menu.getId()))).thenReturn(Optional.of(menu));

    UpdateCategoryRequest request = new UpdateCategoryRequest("new Category", "new Description");

    // When
    String requestUrl =
        String.format(
            UPDATE_CATEGORY,
            getBaseURL(port),
            fromString(menu.getId()),
            fromString(category.getId()));

    var response =
        this.testRestTemplate.exchange(
            requestUrl,
            HttpMethod.PUT,
            new HttpEntity<>(request, getRequestHttpEntity()),
            ResourceUpdatedResponse.class);

    // Then
    then(response).isNotNull();
    then(response.getStatusCode()).isEqualTo(OK);

    ArgumentCaptor<Menu> captor = ArgumentCaptor.forClass(Menu.class);
    verify(menuService, times(1)).create(captor.capture());
    Menu updated = captor.getValue();
    then(updated.getCategories()).hasSize(1);
    Category updatedCategory = updated.getCategories().get(0);
    then(updatedCategory.getDescription()).isEqualTo(request.getDescription());
    then(updatedCategory.getName()).isEqualTo(request.getName());
  }

  @Test
  void testCannotUpdateCategoryIfNoMenuExists() {
    // Given
    UUID menuId = randomUUID();
    when(menuService.findById(eq(menuId.toString()))).thenReturn(Optional.empty());

    UpdateCategoryRequest request = new UpdateCategoryRequest("new Category", "new Description");

    // When
    String requestUrl = String.format(UPDATE_CATEGORY, getBaseURL(port), menuId, randomUUID());
    var response =
        this.testRestTemplate.exchange(
            requestUrl,
            HttpMethod.PUT,
            new HttpEntity<>(request, getRequestHttpEntity()),
            ErrorResponse.class);

    // Then
    then(response).isNotNull();
    then(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  void testCannotUpdateCategoryIfNotAlreadyExists() {
    // Given
    Menu menu = createMenu(0);
    menu.setCategories(null);
    when(menuService.findById(eq(menu.getId()))).thenReturn(Optional.of(menu));

    UpdateCategoryRequest request = new UpdateCategoryRequest("new Category", "new Description");

    // When
    String requestUrl =
        String.format(
            UPDATE_CATEGORY, getBaseURL(port), fromString(menu.getId()), UUID.randomUUID());

    var response =
        this.testRestTemplate.exchange(
            requestUrl,
            HttpMethod.PUT,
            new HttpEntity<>(request, getRequestHttpEntity()),
            ErrorResponse.class);

    // Then
    then(response).isNotNull();
    then(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  void testCannotUpdateCategoryNameIfNameAlreadyExists() {
    // Given
    Menu menu = createMenu(0);
    List<Category> categoryList = createCategories(2);
    categoryList.get(0).setName("new Category");

    menu.setCategories(categoryList);
    when(menuService.findById(eq(menu.getId()))).thenReturn(Optional.of(menu));

    UpdateCategoryRequest request = new UpdateCategoryRequest("new Category", "new Description");

    // When
    String requestUrl =
        String.format(
            UPDATE_CATEGORY,
            getBaseURL(port),
            fromString(menu.getId()),
            fromString(categoryList.get(1).getId()));
    var response =
        this.testRestTemplate.exchange(
            requestUrl,
            HttpMethod.PUT,
            new HttpEntity<>(request, getRequestHttpEntity()),
            ErrorResponse.class);

    // Then
    then(response).isNotNull();
    then(response.getStatusCode()).isEqualTo(CONFLICT);
  }

  @Test
  void testUpdateCategoryWhenMultipleCategoriesExists() {
    // Given
    Menu menu = createMenu(0);
    List<Category> categories = createCategories(2);
    menu.setCategories(categories);
    when(menuService.findById(eq(menu.getId()))).thenReturn(Optional.of(menu));

    UpdateCategoryRequest request = new UpdateCategoryRequest("new Category", "new Description");

    // When
    String requestUrl =
        String.format(UPDATE_CATEGORY, getBaseURL(port), menu.getId(), categories.get(0).getId());

    var response =
        this.testRestTemplate.exchange(
            requestUrl,
            HttpMethod.PUT,
            new HttpEntity<>(request, getRequestHttpEntity()),
            ResourceUpdatedResponse.class);

    // Then
    then(response).isNotNull();
    then(response.getStatusCode()).isEqualTo(OK);

    ArgumentCaptor<Menu> captor = ArgumentCaptor.forClass(Menu.class);
    verify(menuService, times(1)).create(captor.capture());
    Menu updated = captor.getValue();
    then(updated.getCategories()).hasSize(2);
    Optional<Category> updatedCategory =
        menu.getCategories().stream()
            .filter(c -> c.getId().equals(categories.get(0).getId()))
            .findFirst();
    then(updatedCategory.get().getDescription()).isEqualTo(request.getDescription());
    then(updatedCategory.get().getName()).isEqualTo(request.getName());
  }

  @Test
  void testUpdateOnlyCategoryDescription() {
    // Given
    Menu menu = createMenu(0);
    List<Category> categoryList = createCategories(2);
    menu.setCategories(categoryList);
    when(menuService.findById(eq(menu.getId()))).thenReturn(Optional.of(menu));

    UpdateCategoryRequest request =
        new UpdateCategoryRequest(categoryList.get(1).getName(), "new Description");

    // When
    String requestUrl =
        String.format(
            UPDATE_CATEGORY,
            getBaseURL(port),
            fromString(menu.getId()),
            fromString(categoryList.get(1).getId()));
    var response =
        this.testRestTemplate.exchange(
            requestUrl,
            HttpMethod.PUT,
            new HttpEntity<>(request, getRequestHttpEntity()),
            ErrorResponse.class);

    // Then
    then(response).isNotNull();
    then(response.getStatusCode()).isEqualTo(OK);

    ArgumentCaptor<Menu> captor = ArgumentCaptor.forClass(Menu.class);
    verify(menuService, times(1)).create(captor.capture());
    Menu updated = captor.getValue();
    then(updated.getCategories()).hasSize(2);

    Category updatedCategory = updated.getCategories().get(1);
    then(updatedCategory.getDescription()).isEqualTo(request.getDescription());
    then(updatedCategory.getName()).isEqualTo(request.getName());

    Category originalCategory = updated.getCategories().get(0);
    then(originalCategory.getDescription()).isEqualTo(categoryList.get(0).getDescription());
    then(originalCategory.getName()).isEqualTo(categoryList.get(0).getName());
  }

  @Test
  void testUpdateCategoryWithNoNameReturnsBadRequest() {
    // Given
    Menu menu = createMenu(0);
    Category category = createCategory(0);
    menu.addOrUpdateCategory(category);
    when(menuService.findById(eq(menu.getId()))).thenReturn(Optional.of(menu));

    UpdateCategoryRequest request = new UpdateCategoryRequest("", "new Description");

    // When
    String requestUrl =
        String.format(UPDATE_CATEGORY, getBaseURL(port), menu.getId(), category.getId());

    var response =
        this.testRestTemplate.exchange(
            requestUrl,
            HttpMethod.PUT,
            new HttpEntity<>(request, getRequestHttpEntity()),
            ErrorResponse.class);

    // Then
    then(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    then(response.getBody().getDescription())
        .isEqualTo("Invalid Request: {name=must not be blank}");
  }

  @Test
  void testUpdateCategoryWithNoDescriptionReturnsBadRequest() {
    // Given
    Menu menu = createMenu(0);
    Category category = createCategory(0);
    menu.addOrUpdateCategory(category);
    when(menuService.findById(eq(menu.getId()))).thenReturn(Optional.of(menu));

    UpdateCategoryRequest request = new UpdateCategoryRequest("Updated Name", "");

    // When
    String requestUrl =
        String.format(UPDATE_CATEGORY, getBaseURL(port), menu.getId(), category.getId());

    var response =
        this.testRestTemplate.exchange(
            requestUrl,
            HttpMethod.PUT,
            new HttpEntity<>(request, getRequestHttpEntity()),
            ErrorResponse.class);

    // Then
    then(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    then(response.getBody().getDescription())
        .isEqualTo("Invalid Request: {description=must not be blank}");
  }

  @Test
  void testDeleteCategorySuccess() {
    // Given
    Menu menu = createMenu(1);
    Category category = createCategory(0);
    menu.setCategories(List.of(category));
    when(menuService.findById(eq(menu.getId()))).thenReturn(Optional.of(menu));

    // When
    String requestUrl =
        String.format(DELETE_CATEGORY, getBaseURL(port), menu.getId(), category.getId());
    var response =
        this.testRestTemplate.exchange(
            requestUrl,
            HttpMethod.DELETE,
            new HttpEntity<>(getRequestHttpEntity()),
            ErrorResponse.class);

    // Then
    verify(menuService, times(1)).create(menu);
    then(response.getStatusCode()).isEqualTo(OK);
    Optional<Menu> optMenu = menuService.findById(menu.getId());
    Menu updated = optMenu.get();
    then(updated.getCategories()).isNotNull();
  }

  @Test
  void testDeleteCategoryWithAnItem() {
    // Given
    Menu menu = createMenu(1);
    Category category = createCategory(0);
    category.addOrUpdateItem(createItem(0));
    menu.addOrUpdateCategory(category);

    when(menuService.findById(eq(menu.getId()))).thenReturn(Optional.of(menu));

    // When
    String requestUrl =
        String.format(DELETE_CATEGORY, getBaseURL(port), menu.getId(), category.getId());
    var response =
        this.testRestTemplate.exchange(
            requestUrl,
            HttpMethod.DELETE,
            new HttpEntity<>(getRequestHttpEntity()),
            ErrorResponse.class);

    // Then
    then(response.getStatusCode()).isEqualTo(OK);
    ArgumentCaptor<Menu> captor = ArgumentCaptor.forClass(Menu.class);
    verify(menuService, times(1)).create(captor.capture());
    Menu updatedMenu = captor.getValue();
    then(updatedMenu.getCategories()).isNotNull();
  }

  @Test
  void testDeleteCategoryWithInvalidCategoryId() {
    // Given
    Menu menu = createMenu(1);
    Category category = createCategory(0);
    menu.setCategories(List.of(category));
    when(menuService.findById(eq(menu.getId()))).thenReturn(Optional.of(menu));

    // When
    String requestUrl =
        String.format(DELETE_CATEGORY, getBaseURL(port), menu.getId(), menu.getId());
    var response =
        this.testRestTemplate.exchange(
            requestUrl,
            HttpMethod.DELETE,
            new HttpEntity<>(getRequestHttpEntity()),
            ErrorResponse.class);

    // Then
    verify(menuService, times(0)).create(menu);
    then(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  void testDeleteACategoryFromList() {
    // Given
    Menu menu = createMenu(1);
    List<Category> categories = createCategories(2);
    menu.setCategories(categories);
    when(menuService.findById(eq(menu.getId()))).thenReturn(Optional.of(menu));

    // When
    String requestUrl =
        String.format(DELETE_CATEGORY, getBaseURL(port), menu.getId(), categories.get(0).getId());
    var response =
        this.testRestTemplate.exchange(
            requestUrl,
            HttpMethod.DELETE,
            new HttpEntity<>(getRequestHttpEntity()),
            ErrorResponse.class);

    // Then
    verify(menuService, times(1)).create(menu);
    then(response.getStatusCode()).isEqualTo(OK);
    Optional<Menu> byId = menuService.findById(menu.getId());
    Menu updatedMenu = byId.get();
    then(updatedMenu.getCategories()).hasSize(1);
  }
}
