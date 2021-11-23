package com.amido.stacks.workloads.menu.handlers;

import com.amido.stacks.workloads.menu.commands.UpdateMenuCommand;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.repository.MenuRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

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
}
