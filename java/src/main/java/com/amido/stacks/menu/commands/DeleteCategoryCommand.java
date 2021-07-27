package com.amido.stacks.menu.commands;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/** @author ArathyKrishna */
@Getter
@Setter
public class DeleteCategoryCommand extends MenuCommand {

  private UUID categoryId;

  public DeleteCategoryCommand(String correlationId, UUID menuId, UUID categoryId) {
    super(OperationCode.DELETE_CATEGORY, correlationId, menuId);
    this.categoryId = categoryId;
  }
}
