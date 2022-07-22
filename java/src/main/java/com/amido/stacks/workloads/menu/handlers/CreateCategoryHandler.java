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

  @Override
  Optional<UUID> handleCommand(Menu menu, CreateCategoryCommand command) {
    categoryService.create(menu, command);
    return Optional.of(command.getCategoryId());
  }

}
