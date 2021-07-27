package com.amido.stacks.menu.commands;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/** @author ArathyKrishna */
@Getter
@Setter
public class UpdateCategoryCommand extends MenuCommand {

  private String name;
  private String description;
  private UUID categoryId;

  public UpdateCategoryCommand(
      String correlationId, UUID menuId, UUID categoryId, String name, String description) {
    super(OperationCode.UPDATE_CATEGORY, correlationId, menuId);
    this.name = name;
    this.description = description;
    this.categoryId = categoryId;
  }
}
