package com.amido.stacks.menu.commands;

import com.amido.stacks.core.cqrs.command.ApplicationCommand;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MenuCommand extends ApplicationCommand {
  private UUID menuId;

  public MenuCommand(OperationCode operationCode, String correlationId, UUID menuId) {
    super(operationCode.getCode(), correlationId);
    this.menuId = menuId;
  }
}
