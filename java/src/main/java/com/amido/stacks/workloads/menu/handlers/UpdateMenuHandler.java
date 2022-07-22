package com.amido.stacks.workloads.menu.handlers;

import com.amido.stacks.workloads.menu.commands.UpdateMenuCommand;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.service.v1.MenuService;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UpdateMenuHandler extends MenuBaseCommandHandler<UpdateMenuCommand> {

  protected MenuService menuService;

  @Override
  Optional<UUID> handleCommand(Menu menu, UpdateMenuCommand command) {

    menuService.update(menu, command);

    return Optional.of(command.getMenuId());
  }

  @Override
  public Optional<UUID> handle(UpdateMenuCommand updateMenuCommand) {
    return Optional.empty();
  }
}
