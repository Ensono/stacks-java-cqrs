package com.amido.stacks.workloads.menu.handlers;

import com.amido.stacks.workloads.menu.commands.CreateItemCommand;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.service.v1.ItemService;
import com.amido.stacks.workloads.menu.service.v1.MenuService;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CreateItemHandler extends MenuBaseCommandHandler<CreateItemCommand> {

  public CreateItemHandler(MenuService menuService,
      ItemService itemService) {
    super(menuService);
    this.itemService = itemService;
  }

  protected ItemService itemService;

  @Override
  Optional<UUID> handleCommand(Menu menu, CreateItemCommand command) {

    itemService.create(menu, command);
    return Optional.of(command.getItemId());
  }

  @Override
  public Optional<UUID> handle(CreateItemCommand createItemCommand) {

    return Optional.empty();
  }
}
