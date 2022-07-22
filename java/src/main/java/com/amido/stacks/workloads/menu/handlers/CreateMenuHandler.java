package com.amido.stacks.workloads.menu.handlers;

import com.amido.stacks.core.cqrs.handler.CommandHandler;
import com.amido.stacks.workloads.menu.commands.CreateMenuCommand;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.service.v1.MenuService;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CreateMenuHandler implements CommandHandler<CreateMenuCommand> {

  protected MenuService menuService;

  @Override
  public Optional<UUID> handle(CreateMenuCommand command) {

    menuService.verifyMenuNotAlreadyExisting(command);
    final UUID id = UUID.randomUUID();
    final Menu menu =
        new Menu(
            id.toString(),
            command.getRestaurantId().toString(),
            command.getName(),
            command.getDescription(),
            new ArrayList<>(),
            command.getEnabled());

    menuService.create(menu);
    return Optional.of(id);
  }
}
