package com.amido.stacks.menu.commands;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/** @author ArathyKrishna */
@Getter
@Setter
public class UpdateItemCommand extends MenuCommand {

  private String name;
  private String description;
  private UUID categoryId;
  private UUID itemId;
  private Double price;
  private Boolean available;

  public UpdateItemCommand(
      String correlationId,
      UUID menuId,
      UUID categoryId,
      UUID itemId,
      String name,
      String description,
      Double price,
      Boolean available) {
    super(OperationCode.UPDATE_MENU_ITEM, correlationId, menuId);
    this.name = name;
    this.description = description;
    this.categoryId = categoryId;
    this.itemId = itemId;
    this.price = price;
    this.available = available;
  }
}
