package com.amido.stacks.menu.mappers;

import com.amido.stacks.menu.api.v1.dto.request.*;
import com.amido.stacks.menu.commands.*;
import java.util.UUID;

public class RequestToCommandMapper {

  public static CreateMenuCommand map(String correlationId, CreateMenuRequest r) {
    return new CreateMenuCommand(
        correlationId, r.getName(), r.getDescription(), r.getTenantId(), r.getEnabled());
  }

  public static UpdateMenuCommand map(String correlationId, UUID menuId, UpdateMenuRequest r) {
    return new UpdateMenuCommand(
        correlationId, menuId, r.getName(), r.getDescription(), r.getEnabled());
  }

  public static CreateCategoryCommand map(
      String correlationId, UUID menuId, CreateCategoryRequest r) {
    return new CreateCategoryCommand(correlationId, menuId, r.getName(), r.getDescription());
  }

  public static UpdateCategoryCommand map(
      String correlationId, UUID menuId, UUID categoryId, UpdateCategoryRequest r) {
    return new UpdateCategoryCommand(
        correlationId, menuId, categoryId, r.getName(), r.getDescription());
  }

  public static CreateItemCommand map(
      String correlationId, UUID menuId, UUID categoryId, CreateItemRequest r) {
    return new CreateItemCommand(
        correlationId,
        menuId,
        categoryId,
        r.getName(),
        r.getDescription(),
        r.getPrice(),
        r.getAvailable());
  }

  public static UpdateItemCommand map(
      String correlationId, UUID menuId, UUID categoryId, UUID itemId, UpdateItemRequest r) {
    return new UpdateItemCommand(
        correlationId,
        menuId,
        categoryId,
        itemId,
        r.getName(),
        r.getDescription(),
        r.getPrice(),
        r.getAvailable());
  }
}
