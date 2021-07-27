package com.amido.stacks.menu.commands;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/** @author ArathyKrishna */
@Getter
@Setter
public class DeleteItemCommand extends MenuCommand {

  private UUID categoryId;
  private UUID itemId;

  public DeleteItemCommand(String correlationId, UUID menuId, UUID categoryId, UUID itemId) {
    super(OperationCode.DELETE_MENU_ITEM, correlationId, menuId);
    this.categoryId = categoryId;
    this.itemId = itemId;
  }
}
