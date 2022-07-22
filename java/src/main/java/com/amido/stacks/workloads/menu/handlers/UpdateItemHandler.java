package com.amido.stacks.workloads.menu.handlers;

import com.amido.stacks.workloads.menu.commands.UpdateItemCommand;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.service.v1.ItemService;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author ArathyKrishna
 */
@RequiredArgsConstructor
@Component
public class UpdateItemHandler extends MenuBaseCommandHandler<UpdateItemCommand> {

  ItemService itemService;

  @Override
  Optional<UUID> handleCommand(Menu menu, UpdateItemCommand command) {

    itemService.update(menu, command);
    return Optional.of(command.getItemId());
  }

  @Override
  public Optional<UUID> handle(UpdateItemCommand updateItemCommand) {
    return Optional.empty();
  }
}
