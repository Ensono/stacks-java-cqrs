package com.amido.stacks.workloads.menu.api.v1;

import static com.amido.stacks.workloads.menu.domain.utility.CategoryHelper.createCategory;
import static com.amido.stacks.workloads.menu.domain.utility.ItemHelper.createItem;
import static com.amido.stacks.workloads.menu.domain.utility.ItemHelper.createItems;
import static com.amido.stacks.workloads.menu.domain.utility.MenuHelper.createMenu;
import static com.amido.stacks.workloads.util.TestHelper.getBaseURL;
import static com.amido.stacks.workloads.util.TestHelper.getRequestHttpEntity;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
import com.amido.stacks.workloads.menu.api.v1.dto.request.CreateItemRequest;
import com.amido.stacks.workloads.menu.api.v1.dto.request.UpdateItemRequest;
import com.amido.stacks.workloads.menu.domain.Category;
import com.amido.stacks.workloads.menu.domain.Item;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.repository.MenuRepository;
import com.amido.stacks.workloads.menu.service.v1.utility.MenuHelperService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
    properties = {
        "management.port=0",
        "aws.xray.enabled=false",
        "aws.secretsmanager.enabled=false",
        "cosmos.enabled=false"
    })
@Tag("Integration")
@ActiveProfiles("test")
class ItemControllerTest {

  public static final String CREATE_ITEM = "%s/v1/menu/%s/category/%s/items";
  public static final String UPDATE_ITEM = "%s/v1/menu/%s/category/%s/items/%s";
  public static final String DELETE_ITEM = "%s/v1/menu/%s/category/%s/items/%s";

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private MenuHelperService menuHelperService;

  @MockBean
  private MenuRepository menuRepository;

  @Test
  void testAddItem() {
    // Given
    Menu menu = createMenu(1);
    Category category =
        new Category(randomUUID().toString(), "cat name", "cat description", new ArrayList<>());
    menuHelperService.addOrUpdateCategory(menu, category);
    when(menuRepository.findById(menu.getId())).thenReturn(
        Optional.of(menu));
    // when(menuRepository.create(any(Menu.class), any(CreateItemCommand.class)))
    //    .thenReturn(Optional.of(menu));
    when(menuRepository.save(any(Menu.class))).thenReturn(menu);

    CreateItemRequest request =
        new CreateItemRequest("Some Name", "Some Description", 13.56d, true);

    // When
    var response =
        this.testRestTemplate.postForEntity(
            String.format(CREATE_ITEM, getBaseURL(port), menu.getId(), category.getId()),
            request,
            ResourceCreatedResponse.class);

    // Then
    ArgumentCaptor<Menu> captor = ArgumentCaptor.forClass(Menu.class);
    verify(menuRepository, times(1)).save(captor.capture());
    Menu created = captor.getValue();

    then(created.getName()).isEqualTo(menu.getName());
    then(created.getDescription()).isEqualTo(menu.getDescription());
    then(created.getCategories().size()).isEqualTo(1);
    then(created.getCategories().get(0).getName()).isEqualTo(category.getName());
    then(created.getCategories().get(0).getDescription()).isEqualTo(category.getDescription());

    then(created.getCategories().get(0).getItems()).isNotNull();
    then(created.getCategories().get(0).getItems().size()).isEqualTo(1);

    Item createdItem = created.getCategories().get(0).getItems().get(0);
    then(createdItem.getName()).isEqualTo(request.getName());
    then(createdItem.getDescription()).isEqualTo(request.getDescription());
    then(createdItem.getPrice()).isEqualTo(request.getPrice());
    then(createdItem.getAvailable()).isEqualTo(request.getAvailable());

    then(response).isNotNull();
    then(response.getBody()).isNotNull();
    then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    then(createdItem.getId()).isEqualTo(response.getBody().getId().toString());
  }

  @Test
  void testInvalidCategoryIdWilThrowBadRequest() {

    // Given
    CreateItemRequest request =
        new CreateItemRequest("Some Name", "Some Description", 13.56d, true);

    // When
    var response =
        this.testRestTemplate.postForEntity(
            String.format(CREATE_ITEM, getBaseURL(port), randomUUID(), "xyz"),
            request,
            ErrorResponse.class);

    // Then
    then(response).isNotNull();
    then(response.getStatusCode()).isEqualTo(BAD_REQUEST);
  }

  @Test
  void testAddItemWhenInvalidCategoryIdGiven() {

    // Given
    Menu menu = createMenu(1);
    when(menuRepository.findById(menu.getId())).thenReturn(
        Optional.of(menu));

    CreateItemRequest request =
        new CreateItemRequest("Some Name", "Some Description", 13.56d, true);

    // When
    var response =
        this.testRestTemplate.postForEntity(
            String.format(CREATE_ITEM, getBaseURL(port), menu.getId(), randomUUID()),
            request,
            ErrorResponse.class);

    // Then
    verify(menuRepository, never()).save(any(Menu.class));

    // Then
    then(response).isNotNull();
    then(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  void testCannotAddItemWhichAlreadyExists() {
    // Given
    Menu menu = createMenu(1);
    Item item = new Item(randomUUID().toString(), "item name", "item description", 5.99d, true);
    Category category =
        new Category(randomUUID().toString(), "cat name", "cat description", Arrays.asList(item));
    menuHelperService.addOrUpdateCategory(menu, category);

    when(menuRepository.findById(menu.getId())).thenReturn(
        Optional.of(menu));

    CreateItemRequest request =
        new CreateItemRequest(item.getName(), "Some Description", 13.56d, true);

    // When
    var response =
        this.testRestTemplate.postForEntity(
            String.format(CREATE_ITEM, getBaseURL(port), menu.getId(), category.getId()),
            request,
            ErrorResponse.class);

    // Then
    verify(menuRepository, never()).save(any(Menu.class));

    // Then
    then(response).isNotNull();
    then(response.getStatusCode()).isEqualTo(CONFLICT);
  }

  @Test
  void testNoItemNameReturnsBadRequest() {
    // Given
    Menu menu = createMenu(1);
    Category category = createCategory(1);
    menuHelperService.addOrUpdateCategory(menu, category);

    when(menuRepository.findById(menu.getId())).thenReturn(
        Optional.of(menu));
    when(menuRepository.save(any(Menu.class))).thenReturn(menu);
    CreateItemRequest request = new CreateItemRequest("", "Some Description", 13.56d, true);

    // When
    var response =
        this.testRestTemplate.postForEntity(
            String.format(CREATE_ITEM, getBaseURL(port), menu.getId(), category.getId()),
            request,
            ErrorResponse.class);

    // Then
    then(response).isNotNull();
    then(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    then(response.getBody().getDescription())
        .isEqualTo("Invalid Request: {name=must not be blank}");
  }

  @Test
  void testNoItemDescriptionReturnsBadRequest() {
    // Given
    Menu menu = createMenu(1);
    Category category = createCategory(1);
    menuHelperService.addOrUpdateCategory(menu, category);

    when(menuRepository.findById(menu.getId())).thenReturn(
        Optional.of(menu));
    when(menuRepository.save(any(Menu.class))).thenReturn(menu);
    CreateItemRequest request = new CreateItemRequest("Some name", "", 13.56d, true);

    // When
    var response =
        this.testRestTemplate.postForEntity(
            String.format(CREATE_ITEM, getBaseURL(port), menu.getId(), category.getId()),
            request,
            ErrorResponse.class);

    // Then
    then(response).isNotNull();
    then(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    then(response.getBody().getDescription())
        .isEqualTo("Invalid Request: {description=must not be blank}");
  }

  @Test
  void testInvalidPriceDescriptionReturnsBadRequest() {
    // Given
    Menu menu = createMenu(1);
    Category category = createCategory(1);
    menuHelperService.addOrUpdateCategory(menu, category);

    when(menuRepository.findById(menu.getId())).thenReturn(
        Optional.of(menu));
    when(menuRepository.save(any(Menu.class))).thenReturn(menu);
    CreateItemRequest request = new CreateItemRequest("Some name", "Item description", 0d, true);

    // When
    var response =
        this.testRestTemplate.postForEntity(
            String.format(CREATE_ITEM, getBaseURL(port), menu.getId(), category.getId()),
            request,
            ErrorResponse.class);

    // Then
    then(response).isNotNull();
    then(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    then(response.getBody().getDescription())
        .isEqualTo("Invalid Request: {price=Price must be greater than zero}");
  }

  @Test
  void testUpdateItemSuccess() {
    // Given
    Menu menu = createMenu(0);
    Category category = createCategory(0);
    Item item = createItem(0);
    menuHelperService.addOrUpdateItem(category, item);
    menuHelperService.addOrUpdateCategory(menu, category);
    when(menuRepository.findById(menu.getId())).thenReturn(
        Optional.of(menu));

    UpdateItemRequest request =
        new UpdateItemRequest("Some Name", "Some Description", 13.56d, true);

    // When
    String requestUrl =
        String.format(UPDATE_ITEM, getBaseURL(port), menu.getId(), category.getId(), item.getId());

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
    verify(menuRepository, times(1)).save(captor.capture());
    Menu updated = captor.getValue();
    then(updated.getCategories()).hasSize(1);
    Category updatedCategory = updated.getCategories().get(0);
    then(updatedCategory.getItems()).hasSize(1);
    Item updatedItem = updatedCategory.getItems().get(0);

    then(updatedItem.getDescription()).isEqualTo(request.getDescription());
    then(updatedItem.getName()).isEqualTo(request.getName());
    then(updatedItem.getPrice()).isEqualTo(request.getPrice());
    then(updatedItem.getAvailable()).isTrue();
  }

  @Test
  void testUpdateItemDescription() {
    // Given
    Menu menu = createMenu(0);
    Category category = createCategory(0);
    List<Item> items = createItems(2);
    category.setItems(items);
    menuHelperService.addOrUpdateCategory(menu, category);
    when(menuRepository.findById(menu.getId())).thenReturn(
        Optional.of(menu));

    UpdateItemRequest request =
        new UpdateItemRequest(items.get(0).getName(), "Some Description2", 13.56d, true);

    // When
    String requestUrl =
        String.format(
            UPDATE_ITEM, getBaseURL(port), menu.getId(), category.getId(), items.get(0).getId());

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
    verify(menuRepository, times(1)).save(captor.capture());
    Category updatedCategory = captor.getValue().getCategories().get(0);
    then(updatedCategory.getItems()).hasSize(2);

    Item updatedItem = updatedCategory.getItems().get(1);
    then(updatedItem.getDescription()).isEqualTo(request.getDescription());
    then(updatedItem.getName()).isEqualTo(request.getName());
    then(updatedItem.getPrice()).isEqualTo(request.getPrice());
    then(updatedItem.getAvailable()).isTrue();

    Item nonUpdatedItem = updatedCategory.getItems().get(0);
    then(nonUpdatedItem.getDescription()).isEqualTo(items.get(1).getDescription());
    then(nonUpdatedItem.getName()).isEqualTo(items.get(1).getName());
    then(nonUpdatedItem.getPrice()).isEqualTo(items.get(1).getPrice());
    then(nonUpdatedItem.getAvailable()).isEqualTo(items.get(1).getAvailable());
  }

  @Test
  void testUpdateItemWithInvalidCategoryId() {
    // Given
    Menu menu = createMenu(0);
    Category category = createCategory(0);
    Item item = new Item(randomUUID().toString(), "New Item", "Item description", 12.2d, true);
    menuHelperService.addOrUpdateItem(category, item);
    menuHelperService.addOrUpdateCategory(menu, category);

    when(menuRepository.findById(menu.getId())).thenReturn(
        Optional.of(menu));

    UpdateItemRequest request =
        new UpdateItemRequest("Some Name", "Some Description", 13.56d, true);

    // When
    String requestUrl =
        String.format(UPDATE_ITEM, getBaseURL(port), menu.getId(), randomUUID(), item.getId());

    var response =
        this.testRestTemplate.exchange(
            requestUrl,
            HttpMethod.PUT,
            new HttpEntity<>(request, getRequestHttpEntity()),
            ErrorResponse.class);

    // Then
    then(response).isNotNull();
    then(response.getStatusCode()).isEqualTo(NOT_FOUND);

    ArgumentCaptor<Menu> captor = ArgumentCaptor.forClass(Menu.class);
    verify(menuRepository, times(0)).save(captor.capture());
  }

  @Test
  void testUpdateItemWithInvalidItemId() {
    // Given
    Menu menu = createMenu(0);
    Category category = createCategory(0);
    Item item = new Item(randomUUID().toString(), "New Item", "Item description", 12.2d, true);
    menuHelperService.addOrUpdateItem(category, item);
    menuHelperService.addOrUpdateCategory(menu, category);
    when(menuRepository.findById(menu.getId())).thenReturn(
        Optional.of(menu));

    UpdateItemRequest request =
        new UpdateItemRequest("Some Name", "Some Description", 13.56d, true);

    // When
    String requestUrl =
        String.format(UPDATE_ITEM, getBaseURL(port), menu.getId(), category.getId(), randomUUID());

    var response =
        this.testRestTemplate.exchange(
            requestUrl,
            HttpMethod.PUT,
            new HttpEntity<>(request, getRequestHttpEntity()),
            ResourceUpdatedResponse.class);

    // Then
    then(response).isNotNull();
    then(response.getStatusCode()).isEqualTo(NOT_FOUND);

    ArgumentCaptor<Menu> captor = ArgumentCaptor.forClass(Menu.class);
    verify(menuRepository, times(0)).save(captor.capture());
  }

  @Test
  void testUpdateItemWithNoNameReturnsBadRequest() {
    // Given
    Menu menu = createMenu(0);
    Category category = createCategory(0);
    Item item = new Item(randomUUID().toString(), "New Item", "Item description", 12.2d, true);
    menuHelperService.addOrUpdateItem(category, item);
    menuHelperService.addOrUpdateCategory(menu, category);
    when(menuRepository.findById(menu.getId())).thenReturn(
        Optional.of(menu));

    UpdateItemRequest request = new UpdateItemRequest("", "Some Description", 13.56d, true);

    // When
    String requestUrl =
        String.format(UPDATE_ITEM, getBaseURL(port), menu.getId(), category.getId(), randomUUID());

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
  void testUpdateItemWithNoDescriptionReturnsBadRequest() {
    // Given
    Menu menu = createMenu(0);
    Category category = createCategory(0);
    Item item = createItem(0);
    menuHelperService.addOrUpdateItem(category, item);
    menuHelperService.addOrUpdateCategory(menu, category);
    when(menuRepository.findById(menu.getId())).thenReturn(
        Optional.of(menu));

    UpdateItemRequest request = new UpdateItemRequest("Updated Name", "", 13.56d, true);

    // When
    String requestUrl =
        String.format(UPDATE_ITEM, getBaseURL(port), menu.getId(), category.getId(), randomUUID());

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
  void testUpdateItemWithInvalidPriceReturnsBadRequest() {
    // Given
    Menu menu = createMenu(0);
    Category category = createCategory(0);
    Item item = createItem(0);
    menuHelperService.addOrUpdateItem(category, item);
    menuHelperService.addOrUpdateCategory(menu, category);
    when(menuRepository.findById(menu.getId())).thenReturn(
        Optional.of(menu));

    UpdateItemRequest request = new UpdateItemRequest("Updated Name", "la alal", 0d, true);

    // When
    String requestUrl =
        String.format(UPDATE_ITEM, getBaseURL(port), menu.getId(), category.getId(), randomUUID());

    var response =
        this.testRestTemplate.exchange(
            requestUrl,
            HttpMethod.PUT,
            new HttpEntity<>(request, getRequestHttpEntity()),
            ErrorResponse.class);

    // Then
    then(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    then(response.getBody().getDescription())
        .isEqualTo("Invalid Request: {price=Price must be greater than zero}");
  }

  @Test
  void testDeleteItemSuccess() {
    // Given
    Menu menu = createMenu(1);
    Category category = createCategory(0);
    Item item = new Item(randomUUID().toString(), "New Item", "Item description", 12.2d, true);
    menuHelperService.addOrUpdateItem(category, item);
    menuHelperService.addOrUpdateCategory(menu, category);
    when(menuRepository.findById(menu.getId())).thenReturn(
        Optional.of(menu));

    // When
    String requestUrl =
        String.format(DELETE_ITEM, getBaseURL(port), menu.getId(), category.getId(), item.getId());

    var response =
        this.testRestTemplate.exchange(
            requestUrl,
            HttpMethod.DELETE,
            new HttpEntity<>(getRequestHttpEntity()),
            ResponseEntity.class);

    // Then
    verify(menuRepository, times(1)).save(menu);
    then(response.getStatusCode()).isEqualTo(OK);
    Optional<Menu> optMenu = menuRepository.findById(menu.getId());
    Menu updated = optMenu.get();
    then(updated.getCategories()).hasSize(1);
    then(updated.getCategories().get(0).getItems()).isNotNull();
  }

  @Test
  void testDeleteItemWithInvalidCategoryId() {
    // Given
    Menu menu = createMenu(1);
    Category category = createCategory(0);
    Item item = new Item(randomUUID().toString(), "New Item", "Item description", 12.2d, true);
    menuHelperService.addOrUpdateItem(category, item);
    menuHelperService.addOrUpdateCategory(menu, category);
    when(menuRepository.findById(menu.getId())).thenReturn(
        Optional.of(menu));

    // When
    String requestUrl =
        String.format(DELETE_ITEM, getBaseURL(port), menu.getId(), item.getId(), item.getId());

    var response =
        this.testRestTemplate.exchange(
            requestUrl,
            HttpMethod.DELETE,
            new HttpEntity<>(getRequestHttpEntity()),
            ErrorResponse.class);

    // Then
    verify(menuRepository, times(0)).save(menu);
    then(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  void testDeleteItemWithInvalidItemId() {
    // Given
    Menu menu = createMenu(1);
    Category category = createCategory(0);
    Item item = new Item(randomUUID().toString(), "New Item", "Item description", 12.2d, true);
    menuHelperService.addOrUpdateItem(category, item);
    menuHelperService.addOrUpdateCategory(menu, category);
    when(menuRepository.findById(menu.getId())).thenReturn(
        Optional.of(menu));

    // When
    String requestUrl =
        String.format(DELETE_ITEM, getBaseURL(port), menu.getId(), category.getId(), randomUUID());

    var response =
        this.testRestTemplate.exchange(
            requestUrl,
            HttpMethod.DELETE,
            new HttpEntity<>(getRequestHttpEntity()),
            ErrorResponse.class);

    // Then
    verify(menuRepository, times(0)).save(menu);
    then(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }
}
