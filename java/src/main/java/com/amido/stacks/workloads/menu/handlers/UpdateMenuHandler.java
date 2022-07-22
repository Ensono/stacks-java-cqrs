package com.amido.stacks.workloads.menu.handlers;

import com.amido.stacks.workloads.menu.commands.UpdateMenuCommand;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.service.v1.MenuService;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UpdateMenuHandler extends MenuBaseCommandHandler<UpdateMenuCommand> {

  public UpdateMenuHandler(MenuService menuService) {
    super(menuService);
  }

  @Override
  Optional<UUID> handleCommand(Menu menu, UpdateMenuCommand command) {

    menuService.update(menu, command);

    return Optional.of(command.getMenuId());
  }

}
