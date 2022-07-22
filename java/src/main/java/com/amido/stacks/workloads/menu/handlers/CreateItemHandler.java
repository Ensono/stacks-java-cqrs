package com.amido.stacks.workloads.menu.handlers;

import com.amido.stacks.workloads.menu.commands.CreateItemCommand;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.service.v1.ItemService;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CreateItemHandler extends MenuBaseCommandHandler<CreateItemCommand> {

  ItemService itemService;

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
