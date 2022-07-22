package com.amido.stacks.workloads.menu.handlers;

import com.amido.stacks.workloads.menu.commands.UpdateCategoryCommand;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.service.v1.CategoryService;
import com.amido.stacks.workloads.menu.service.v1.MenuService;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * @author ArathyKrishna
 */
@Component
public class UpdateCategoryHandler extends MenuBaseCommandHandler<UpdateCategoryCommand> {

  protected CategoryService categoryService;

  public UpdateCategoryHandler(MenuService menuService,
      CategoryService categoryService) {
    super(menuService);
    this.categoryService = categoryService;
  }

  @Override
  Optional<UUID> handleCommand(Menu menu, UpdateCategoryCommand command) {
    categoryService.update(menu, command);
    return Optional.of(command.getCategoryId());
  }

  @Override
  public Optional<UUID> handle(UpdateCategoryCommand updateCategoryCommand) {
    return Optional.empty();
  }
}
