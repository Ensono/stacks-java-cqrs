package com.amido.stacks.workloads.menu.handlers;

import com.amido.stacks.workloads.menu.commands.CreateCategoryCommand;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.service.v1.CategoryService;
import com.amido.stacks.workloads.menu.service.v1.MenuService;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CreateCategoryHandler extends MenuBaseCommandHandler<CreateCategoryCommand> {

  protected CategoryService categoryService;

  public CreateCategoryHandler(MenuService menuService,
      CategoryService categoryService) {
    super(menuService);
    this.categoryService = categoryService;
  }

  @Override
  Optional<UUID> handleCommand(Menu menu, CreateCategoryCommand command) {
    categoryService.create(menu, command);
    return Optional.of(command.getCategoryId());
  }

}
