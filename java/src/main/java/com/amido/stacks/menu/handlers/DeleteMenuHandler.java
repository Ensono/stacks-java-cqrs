package com.amido.stacks.menu.handlers;

import com.amido.stacks.menu.commands.DeleteMenuCommand;
import com.amido.stacks.menu.domain.Menu;
import com.amido.stacks.menu.events.MenuDeletedEvent;
import com.amido.stacks.menu.events.MenuEvent;
import com.amido.stacks.menu.repository.MenuRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

  @Override
  List<MenuEvent> raiseApplicationEvents(Menu menu, DeleteMenuCommand command) {
    return Collections.singletonList(new MenuDeletedEvent(command));
  }
}
