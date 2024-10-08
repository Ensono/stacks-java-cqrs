package com.amido.stacks.workloads.menu.api.v1;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.springframework.http.HttpStatus.OK;

import com.amido.stacks.core.api.annotations.CreateAPIResponses;
import com.amido.stacks.core.api.annotations.DeleteAPIResponses;
import com.amido.stacks.core.api.annotations.SearchAPIResponses;
import com.amido.stacks.core.api.annotations.UpdateAPIResponses;
import com.amido.stacks.core.api.dto.response.ResourceCreatedResponse;
import com.amido.stacks.core.api.dto.response.ResourceUpdatedResponse;
import com.amido.stacks.workloads.menu.api.v1.dto.request.CreateMenuRequest;
import com.amido.stacks.workloads.menu.api.v1.dto.request.UpdateMenuRequest;
import com.amido.stacks.workloads.menu.api.v1.dto.response.MenuDTO;
import com.amido.stacks.workloads.menu.api.v1.dto.response.SearchMenuResult;
import com.amido.stacks.workloads.menu.commands.DeleteMenuCommand;
import com.amido.stacks.workloads.menu.commands.MenuCommand;
import com.amido.stacks.workloads.menu.commands.UpdateMenuCommand;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.exception.MenuNotFoundException;
import com.amido.stacks.workloads.menu.handlers.CreateMenuHandler;
import com.amido.stacks.workloads.menu.handlers.DeleteMenuHandler;
import com.amido.stacks.workloads.menu.handlers.UpdateMenuHandler;
import com.amido.stacks.workloads.menu.mappers.MenuMapper;
import com.amido.stacks.workloads.menu.mappers.RequestToCommandMapper;
import com.amido.stacks.workloads.menu.mappers.SearchMenuResultItemMapper;
import com.amido.stacks.workloads.menu.service.MenuQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path = "/v1/menu")
@RestController
public class MenuController {

  private final CreateMenuHandler createMenuHandler;

  private final RequestToCommandMapper requestToCommandMapper;

  private final MenuQueryService menuQueryService;

  private final SearchMenuResultItemMapper searchMenuResultItemMapper;

  private final MenuMapper menuMapper;

  private final UpdateMenuHandler updateMenuHandler;

  private final DeleteMenuHandler deleteMenuHandler;

  public MenuController(
      CreateMenuHandler createMenuHandler,
      RequestToCommandMapper requestToCommandMapper,
      MenuQueryService menuQueryService,
      SearchMenuResultItemMapper searchMenuResultItemMapper,
      MenuMapper menuMapper,
      UpdateMenuHandler updateMenuHandler,
      DeleteMenuHandler deleteMenuHandler) {
    this.createMenuHandler = createMenuHandler;
    this.requestToCommandMapper = requestToCommandMapper;
    this.menuQueryService = menuQueryService;
    this.searchMenuResultItemMapper = searchMenuResultItemMapper;
    this.menuMapper = menuMapper;
    this.updateMenuHandler = updateMenuHandler;
    this.deleteMenuHandler = deleteMenuHandler;
  }

  @PostMapping
  @Operation(
      tags = "Menu",
      summary = "Create a menu",
      description = "Adds a menu",
      operationId = "CreateMenu")
  @CreateAPIResponses
  ResponseEntity<ResourceCreatedResponse> createMenu(
      @Validated @RequestBody CreateMenuRequest body,
      @Parameter(hidden = true) @RequestAttribute("CorrelationId") String correlationId) {
    return new ResponseEntity<>(
        new ResourceCreatedResponse(
            createMenuHandler
                .handle(requestToCommandMapper.map(correlationId, body))
                .orElseThrow()),
        HttpStatus.CREATED);
  }

  @GetMapping
  @Operation(
      tags = "Menu",
      summary = "Get or search a list of menus",
      description =
          "By passing in the appropriate options, you can search for available menus in the system")
  @ApiResponse(
      responseCode = "200",
      description = "Search results matching criteria",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = SearchMenuResult.class)))
  @SearchAPIResponses
  ResponseEntity<SearchMenuResult> searchMenu(
      @RequestParam(value = "searchTerm", required = false) String searchTerm,
      @RequestParam(value = "restaurantId", required = false) UUID restaurantId,
      @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
      @RequestParam(value = "pageNumber", required = false, defaultValue = "1")
          Integer pageNumber) {
    List<Menu> menuList;

    if (isNotEmpty(searchTerm) && nonNull(restaurantId)) {
      menuList =
          this.menuQueryService.findAllByRestaurantIdAndNameContaining(
              restaurantId, searchTerm, pageSize, pageNumber);
    } else if (isNotEmpty(searchTerm)) {
      menuList = this.menuQueryService.findAllByNameContaining(searchTerm, pageSize, pageNumber);
    } else if (nonNull(restaurantId)) {
      menuList = this.menuQueryService.findAllByRestaurantId(restaurantId, pageSize, pageNumber);
    } else {
      menuList = this.menuQueryService.findAll(pageNumber, pageSize);
    }

    return ResponseEntity.ok(
        new SearchMenuResult(
            pageSize,
            pageNumber,
            menuList.stream().map(searchMenuResultItemMapper::toDto).toList()));
  }

  @GetMapping(value = "/{id}")
  @Operation(
      tags = "Menu",
      summary = "Get a menu",
      description =
          "By passing the menu id, you can get access to available categories and items in the menu")
  @ApiResponse(
      responseCode = "200",
      description = "Menu",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MenuDTO.class)))
  @SearchAPIResponses
  ResponseEntity<MenuDTO> getMenu(
      @PathVariable(name = "id") UUID id,
      @Parameter(hidden = true) @RequestAttribute("CorrelationId") String correlationId) {
    Menu menu =
        this.menuQueryService
            .findById(id)
            .orElseThrow(() -> new MenuNotFoundException(new MenuCommand(correlationId, id)));
    return ResponseEntity.ok(menuMapper.toDto(menu));
  }

  @PutMapping(value = "/{id}")
  @Operation(
      tags = "Menu",
      summary = "Update a menu",
      description = "Update a menu with new information")
  @UpdateAPIResponses
  ResponseEntity<ResourceUpdatedResponse> updateMenu(
      @Parameter(description = "Menu id", required = true) @PathVariable("id") UUID menuId,
      @Validated @RequestBody UpdateMenuRequest body,
      @Parameter(hidden = true) @RequestAttribute("CorrelationId") String correlationId) {
    UpdateMenuCommand command = requestToCommandMapper.map(correlationId, menuId, body);
    return new ResponseEntity<>(
        new ResourceUpdatedResponse(updateMenuHandler.handle(command).orElseThrow()),
        HttpStatus.OK);
  }

  @DeleteMapping(value = "/{id}")
  @Operation(
      tags = "Menu",
      summary = "Removes a Menu with all it's Categories and Items",
      description = "Remove a menu from a restaurant",
      operationId = "DeleteMenu")
  @DeleteAPIResponses
  ResponseEntity<Void> deleteMenu(
      @Parameter(description = "Menu id", required = true) @PathVariable("id") UUID menuId,
      @Parameter(hidden = true) @RequestAttribute("CorrelationId") String correlationId) {
    deleteMenuHandler.handle(new DeleteMenuCommand(correlationId, menuId));
    return new ResponseEntity<>(OK);
  }
}
