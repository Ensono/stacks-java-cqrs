package com.amido.stacks.workloads.menu.handlers;

import com.amido.stacks.workloads.menu.commands.DeleteCategoryCommand;
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
public class DeleteCategoryHandler extends MenuBaseCommandHandler<DeleteCategoryCommand> {

  protected CategoryService categoryService;

  public DeleteCategoryHandler(MenuService menuService,
      CategoryService categoryService) {
    super(menuService);
    this.categoryService = categoryService;
  }

  Optional<UUID> handleCommand(Menu menu, DeleteCategoryCommand command) {

    categoryService.delete(menu, command);
    return Optional.empty();
  }

  @Override
  public Optional<UUID> handle(DeleteCategoryCommand deleteCategoryCommand) {
    return Optional.empty();
  }
}
