package com.amido.stacks.workloads.menu.handlers;

import com.amido.stacks.workloads.menu.commands.CreateCategoryCommand;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.service.v1.CategoryService;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CreateCategoryHandler extends MenuBaseCommandHandler<CreateCategoryCommand> {

  protected CategoryService categoryService;

  public CreateCategoryHandler(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @Override
  Optional<UUID> handleCommand(Menu menu, CreateCategoryCommand command) {
    categoryService.create(menu, command);
    return Optional.of(command.getCategoryId());
  }

  @Override
  public Optional<UUID> handle(CreateCategoryCommand createCategoryCommand) {
    return Optional.empty();
  }
}
