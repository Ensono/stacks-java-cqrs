package com.amido.stacks.menu.handlers;

import com.amido.stacks.menu.commands.UpdateMenuCommand;
import com.amido.stacks.menu.domain.Menu;
import com.amido.stacks.menu.events.MenuEvent;
import com.amido.stacks.menu.events.MenuUpdatedEvent;
import com.amido.stacks.menu.repository.MenuRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UpdateMenuHandler extends MenuBaseCommandHandler<UpdateMenuCommand> {

  public UpdateMenuHandler(MenuRepository menuRepository) {
    super(menuRepository);
  }

  @Override
  Optional<UUID> handleCommand(Menu menu, UpdateMenuCommand command) {
    menu.setName(command.getName());
    menu.setDescription(command.getDescription());
    menu.setEnabled(command.getEnabled());
    menuRepository.save(menu);
    return Optional.of(command.getMenuId());
  }

  @Override
  List<MenuEvent> raiseApplicationEvents(Menu menu, UpdateMenuCommand command) {
    return Collections.singletonList(new MenuUpdatedEvent(command));
  }
}
