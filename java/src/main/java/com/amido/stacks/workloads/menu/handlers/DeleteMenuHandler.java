package com.amido.stacks.workloads.menu.handlers;

import com.amido.stacks.workloads.menu.commands.DeleteMenuCommand;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.service.v1.MenuService;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Handler for deleting menu
 *
 * @author ArathyKrishna
 */

@Component
public class DeleteMenuHandler extends MenuBaseCommandHandler<DeleteMenuCommand> {

  public DeleteMenuHandler(MenuService menuService) {
    super(menuService);
  }

  @Override
  Optional<UUID> handleCommand(Menu menu, DeleteMenuCommand command) {
    menuService.delete(menu);
    return Optional.empty();
  }
}
