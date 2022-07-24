package com.amido.stacks.workloads.menu.handlers;

import com.amido.stacks.core.cqrs.handler.CommandHandler;
import com.amido.stacks.workloads.menu.commands.CreateMenuCommand;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.mappers.cqrs.CreateMenuCommandMapper;
import com.amido.stacks.workloads.menu.service.v1.MenuService;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CreateMenuHandler implements CommandHandler<CreateMenuCommand> {

  protected MenuService menuService;
  protected CreateMenuCommandMapper createMenuCommandMapper;

  @Override
  public Optional<UUID> handle(CreateMenuCommand command) {
    final Menu menu = createMenuCommandMapper.fromDto(command);

    menuService.verifyMenuNotAlreadyExisting(menu, command);
    final UUID id = UUID.randomUUID();

    menuService.create(menu);
    return Optional.of(id);
  }
}
