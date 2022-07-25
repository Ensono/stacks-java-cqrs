package com.amido.stacks.workloads.menu.handlers;

import com.amido.stacks.workloads.menu.commands.DeleteItemCommand;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.service.v1.ItemService;
import com.amido.stacks.workloads.menu.service.v1.MenuService;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** @author ArathyKrishna */
@Component
public class DeleteItemHandler extends MenuBaseCommandHandler<DeleteItemCommand> {

  protected ItemService itemService;

  public DeleteItemHandler(MenuService menuService, ItemService itemService) {
    super(menuService);
    this.itemService = itemService;
  }

  @Override
  Optional<UUID> handleCommand(Menu menu, DeleteItemCommand command) {

    itemService.delete(menu, command);

    return Optional.empty();
  }
}
