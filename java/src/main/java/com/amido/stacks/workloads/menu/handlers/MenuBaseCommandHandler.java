package com.amido.stacks.workloads.menu.handlers;

import com.amido.stacks.core.cqrs.handler.CommandHandler;
import com.amido.stacks.workloads.menu.commands.MenuCommand;
import com.amido.stacks.workloads.menu.domain.Menu;
import java.util.Optional;
import java.util.UUID;

public abstract class MenuBaseCommandHandler<T extends MenuCommand> implements CommandHandler<T> {

  abstract Optional<UUID> handleCommand(Menu menu, T command);

  /**
   * find a category for the id provided
   *
   * @param menu menu object
   * @param categoryId category id
   * @return category if found else optional.empty
   */
}
