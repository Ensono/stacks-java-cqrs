package com.amido.workloads.menu.handlers;

import com.amido.workloads.menu.commands.DeleteMenuCommand;
import com.amido.workloads.menu.domain.Menu;
import com.amido.workloads.menu.repository.MenuRepository;
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

  public DeleteMenuHandler(MenuRepository menuRepository) {
    super(menuRepository);
  }

  @Override
  Optional<UUID> handleCommand(Menu menu, DeleteMenuCommand command) {
    menuRepository.delete(menu);
    return Optional.empty();
  }
}
